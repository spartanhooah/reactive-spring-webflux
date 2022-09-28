package com.reactivespring.router

import com.reactivespring.domain.Review
import com.reactivespring.repository.ReviewReactiveRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class ReviewsIntegrationTest extends Specification {
    static final def PATH = "/v1/reviews"

    @Autowired
    WebTestClient client

    @Autowired
    ReviewReactiveRepository reviewRepository

    def setup() {
        def reviews = [
            new Review("1", 1L, "Awesome Movie", 9.0),
            new Review("2", 1L, "Awesome Movie1", 9.0),
            new Review("3", 2L, "Excellent Movie", 8.0)
        ]

        reviewRepository.saveAll(reviews).blockLast()
    }

    def cleanup() {
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

    def "Get all reviews"() {
        expect:
        client
            .get()
            .uri(PATH)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(Review)
            .consumeWith {
                assert it.responseBody.size() >= 3
            }
    }

    def "Update a review"() {
        given:
        def updatedReview = new Review("1", 2L, "Meh", 5)

        expect:
        client
            .put()
            .uri("$PATH/1")
            .bodyValue(updatedReview)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Review)
            .consumeWith {
                assert it.responseBody.movieInfoId
            }
    }

    def "Delete a review"() {
        expect:
        client
            .delete()
            .uri("$PATH/1")
            .exchange()
            .expectStatus()
            .isNoContent()
    }

    def "Get all reviews for a particular movie"() {
        expect:
        client
            .get()
            .uri("$PATH?movieInfoId=1")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(Review)
            .consumeWith {
                assert it.responseBody.size() >= 2
            }
    }
}
