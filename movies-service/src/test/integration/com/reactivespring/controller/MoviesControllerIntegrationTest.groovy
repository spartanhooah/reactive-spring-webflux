package com.reactivespring.controller

import com.github.tomakehurst.wiremock.client.WireMock
import com.reactivespring.domain.Movie
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import static com.github.tomakehurst.wiremock.client.WireMock.get

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 8084)
@TestPropertySource(properties = [
    "restClient.moviesInfoUrl=http://localhost:8084/v1/movieinfos",
    "restClient.reviewsUrl=http://localhost:8084/v1/reviews",
])
class MoviesControllerIntegrationTest extends Specification {
    @Autowired
    WebTestClient client

    def "Get movie by ID"() {
        given:
        def movieId = "abc"
        stubFor(get(urlEqualTo("/v1/movieinfos/$movieId"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBodyFile("movieinfo.json")))

        stubFor(get(urlEqualTo("/v1/reviews?movieInfoId=$movieId"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBodyFile("reviews.json")))

        expect:
        client
            .get()
            .uri("/v1/movies/$movieId")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Movie)
            .consumeWith {
                assert it.responseBody.reviewList.size() == 2
                assert it.responseBody.movieInfo.name == "Batman Begins"
            }
    }

    def "404 from movie info endpoint"() {
        given:
        def movieId = "abc"
        stubFor(get(urlEqualTo("/v1/movieinfos/$movieId"))
            .willReturn(aResponse()
                .withStatus(404)))

        expect:
        client
            .get()
            .uri("/v1/movies/$movieId")
            .exchange()
            .expectStatus()
            .is4xxClientError()
            .expectBody(String)
            .isEqualTo("No movie info found for movie ID abc")

        WireMock.verify(1, getRequestedFor(urlEqualTo("/v1/movieinfos/$movieId")))
    }

    def "404 from reviews endpoint"() {
        given:
        def movieId = "abc"
        stubFor(get(urlEqualTo("/v1/movieinfos/$movieId"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBodyFile("movieinfo.json")))

        stubFor(get(urlEqualTo("/v1/reviews?movieInfoId=$movieId"))
            .willReturn(aResponse()
                .withStatus(404)))

        expect:
        client
            .get()
            .uri("/v1/movies/$movieId")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Movie)
            .consumeWith {
                assert it.responseBody.reviewList.size() == 0
                assert it.responseBody.movieInfo.name == "Batman Begins"
            }
    }

    def "500 from movie info endpoint"() {
        given:
        def movieId = "abc"
        stubFor(get(urlEqualTo("/v1/movieinfos/$movieId"))
            .willReturn(aResponse()
                .withStatus(500)
                .withBody("MovieInfo Service Unavailable")))

        expect:
        client
            .get()
            .uri("/v1/movies/$movieId")
            .exchange()
            .expectStatus()
            .is5xxServerError()
            .expectBody(String)
            .isEqualTo("MovieInfo Service Unavailable")
    }

    def "Validate retries"() {
        given:
        def movieId = "abc"
        stubFor(get(urlEqualTo("/v1/movieinfos/$movieId"))
            .willReturn(aResponse()
                .withStatus(500)
                .withBody("MovieInfo Service Unavailable")))

        expect:
        client
            .get()
            .uri("/v1/movies/$movieId")
            .exchange()
            .expectStatus()
            .is5xxServerError()
            .expectBody(String)
            .isEqualTo("MovieInfo Service Unavailable")

        WireMock.verify(4, getRequestedFor(urlEqualTo("/v1/movieinfos/$movieId")))
    }
}
