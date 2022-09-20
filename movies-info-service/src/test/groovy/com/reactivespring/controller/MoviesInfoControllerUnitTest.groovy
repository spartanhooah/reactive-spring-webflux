package com.reactivespring.controller

import com.reactivespring.TestSetup
import com.reactivespring.domain.MovieInfo
import com.reactivespring.service.MoviesInfoService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

import java.time.LocalDate

@WebFluxTest(controllers = MoviesInfoController)
@AutoConfigureWebTestClient
class MoviesInfoControllerUnitTest extends TestSetup {
    static final def PATH = "/v1/movieinfos"

    @Autowired
    WebTestClient client

    @SpringBean
    MoviesInfoService moviesService = Stub()

    def "Get all movies"() {
        given:
        moviesService.getAllMovieInfos() >> Flux.fromIterable(generateMovies())

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
        given:
        moviesService.getMovieInfoById("abc") >> Mono.just(generateMovies()[2])

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

    def "Add a movie"() {
        given:
        moviesService.addMovieInfo(_ as MovieInfo) >> Mono.just(SERENITY)

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
                assert it.responseBody.movieInfoId == "mockId"
            }
    }

    def "A movie that doesn't validate"() {
        given:
        def brokenMovie = new MovieInfo(
            movieInfoId: "mockId",
            name: "",
            cast: [],
            releaseDate: LocalDate.parse("2005-09-30")
        )

        expect:
        client
            .post()
            .uri(PATH)
            .bodyValue(brokenMovie)
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody(String)
            .consumeWith {
                assert it.responseBody == "Cast list must be nonempty.,Movie must have a name.,Year must be a positive value."
            }
    }

    def "Update a movie"() {
        given:
        moviesService.updateMovieInfo(_ as MovieInfo, _ as String) >> Mono.just(SERENITY)

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
                assert it.responseBody.movieInfoId == "mockId"
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
