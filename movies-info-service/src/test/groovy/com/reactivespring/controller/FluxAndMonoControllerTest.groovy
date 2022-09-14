package com.reactivespring.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.test.StepVerifier
import spock.lang.Specification

@WebFluxTest(controllers = FluxAndMonoController)
class FluxAndMonoControllerTest extends Specification {
    @Autowired
    WebTestClient client

    def "a basic test against the flux endpoint"() {
        expect:
        client.get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Integer)
                .hasSize(3)
    }

    def "test the flux endpoint and check the body"() {
        when:
        def result = client.get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Integer)
                .getResponseBody()

        then:
        StepVerifier.create(result)
                .expectNext(1, 2, 3)
                .verifyComplete()
    }

    def "using consumeWith to check the body content for flux"() {
        expect:
        client.get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Integer)
                .consumeWith {
                    def body = it.getResponseBody()

                    // Assertions are needed here, not sure why exactly
                    assert body.size() == 3
                    assert body[0] == 1
                }
    }

    def "using consumeWith to check the body content for mono"() {
        expect:
        client.get()
                .uri("/mono")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String)
                .consumeWith {
                    def body = it.getResponseBody()

                    // Assertions are needed here, not sure why exactly
                    assert body == "Hello, world!"
                }
    }

    def "test the streaming endpoint"() {
        when:
        def result = client.get()
                .uri("/stream")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Long)
                .getResponseBody()

        then:
        StepVerifier.create(result)
            .expectNext(0L, 1L, 2L, 3L)
            .thenCancel()
            .verify()
    }
}
