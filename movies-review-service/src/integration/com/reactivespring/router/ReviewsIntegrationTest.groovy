package com.reactivespring.router

import com.reactivespring.domain.Review
import com.reactivespring.repository.ReviewReactiveRepository
import org.spockframework.spring.EnableSharedInjection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import spock.lang.Shared
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@EnableSharedInjection
class ReviewsIntegrationTest extends Specification {
    static final def PATH = "/v1/reviews"

    @Autowired
    WebTestClient client

    @Shared
    @Autowired
    ReviewReactiveRepository reviewRepository

    def setupSpec() {
        def reviews = [
            new Review(null, 1L, "Awesome Movie", 9.0),
            new Review(null, 1L, "Awesome Movie1", 9.0),
            new Review(null, 2L, "Excellent Movie", 8.0)
        ]

        reviewRepository.saveAll(reviews).blockLast()
    }

    def cleanupSpec() {
        reviewRepository.deleteAll().block()
    }

    def "Add a review"() {
        given:
        def review = new Review(null, 1L, "Awesome Movie", 9.0)

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
                assert it.responseBody.reviewId
            }
    }
}
