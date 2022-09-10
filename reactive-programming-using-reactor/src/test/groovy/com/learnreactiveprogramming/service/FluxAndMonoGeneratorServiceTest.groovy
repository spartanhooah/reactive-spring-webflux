package com.learnreactiveprogramming.service

import reactor.test.StepVerifier
import spock.lang.Specification

class FluxAndMonoGeneratorServiceTest extends Specification {
    def testSubject = new FluxAndMonoGeneratorService()

    def "namesFlux"() {
        when:
        def namesFlux = testSubject.namesFlux()

        then:
        StepVerifier.create(namesFlux)
//            .expectNext("Ben", "Alex", "Chloe")
//            .expectNextCount(3)
            .expectNext("Alex")
            .expectNextCount(2)
            .verifyComplete()
    }
}
