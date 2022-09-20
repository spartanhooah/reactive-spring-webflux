package com.reactivespring.controller

import com.reactivespring.TestSetup
import com.reactivespring.domain.MovieInfo
import com.reactivespring.repository.MovieInfoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MoviesInfoControllerIntegrationTest extends TestSetup {
    static final def PATH = "/v1/movieinfos"

    @Autowired
    MovieInfoRepository movieInfoRepository

    @Autowired
    WebTestClient client

    def setup() {
        movieInfoRepository.saveAll(generateMovies()).blockLast()
    }

    def cleanup() {
        movieInfoRepository.deleteAll().block()
    }

    def "Add a movie"() {
        expect:
        client
            .post()
            .uri(PATH)
            .bodyValue(SERENITY)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MovieInfo)
            .consumeWith {
                assert it.responseBody.movieInfoId
            }
    }

    def "Get all movies"() {
        expect:
        client
            .get()
            .uri(PATH)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(MovieInfo)
            .hasSize(3)
    }

    def "Get movie by id"() {
        expect:
        client
            .get()
            .uri("$PATH/abc")
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody(MovieInfo)
            .consumeWith {
                it.responseBody.name == "Dark Knight Rises"
            }
    }

    def "Update a movie"() {
        expect:
        client
            .put()
            .uri("$PATH/abc")
            .bodyValue(SERENITY)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody(MovieInfo)
            .consumeWith {
                assert it.responseBody.movieInfoId
                assert it.responseBody.name == "Serenity"
            }
    }

    def "Delete a movie"() {
        expect:
        client
            .delete()
            .uri("$PATH/abc")
            .exchange()
            .expectStatus()
            .isNoContent()
    }
}
