package com.reactivespring.router

import com.reactivespring.domain.Review
import com.reactivespring.exception.GlobalErrorHandler
import com.reactivespring.handler.ReviewHandler
import com.reactivespring.repository.ReviewReactiveRepository
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import spock.lang.Specification

@WebFluxTest
@ContextConfiguration(classes = [ReviewRouter, ReviewHandler, GlobalErrorHandler])
@AutoConfigureWebTestClient
class ReviewsTest extends Specification {
    static final def PATH = "/v1/reviews"

    @SpringBean
    ReviewReactiveRepository repository = Mock()

    @Autowired
    WebTestClient client

    def "Add review"() {
        given:
        def review = new Review(null, 1L, "Awesome Movie", 9.0)
        repository.save(_ as Review) >> Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0))

        expect:
        client
            .post()
            .uri(PATH)
            .bodyValue(review)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Review)
            .consumeWith {
                assert it.responseBody.reviewId == "abc"
            }
    }

    def "Fail bean validation"() {
        given:
        def review = new Review(null, null, "Awesome Movie", -1)

        expect:
        client
            .post()
            .uri(PATH)
            .bodyValue(review)
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody(String)
            .isEqualTo("movieInfoId is required; please provide a non-negative rating")
    }
}
