package com.learnreactiveprogramming.service

import reactor.test.StepVerifier
import spock.lang.Specification

class FluxAndMonoGeneratorServiceTest extends Specification {
    def testSubject = new FluxAndMonoGeneratorService()

    def "namesFlux"() {
        when:
        def result = testSubject.namesFlux()

        then:
        StepVerifier.create(result)
            .expectNext("Ben", "Alex", "Chloe")
            .verifyComplete()
    }

    def "namesFluxMap"() {
        when:
        def result = testSubject.namesFluxMap()

        then:
        StepVerifier.create(result)
            .expectNext("BEN", "ALEX", "CHLOE")
            .verifyComplete()
    }

    def "namesFluxImmutability"() {
        when:
        def result = testSubject.namesFluxImmutability()

        then:
        StepVerifier.create(result)
            .expectNext("Ben", "Alex", "Chloe")
            .verifyComplete()
    }

    def "namesFluxMapFilter"() {
        when:
        def result = testSubject.namesFluxMapFilter(3)

        then:
        StepVerifier.create(result)
            .expectNext("4-ALEX", "5-CHLOE")
            .verifyComplete()
    }

    def "namesFluxFlatMap"() {
        when:
        def result = testSubject.namesFluxFlatMap(3)

        then:
        StepVerifier.create(result)
            .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
            .verifyComplete()
    }

    def "nameFluxFlatMapAsync"() {
        when:
        def result = testSubject.namesFluxFlatMapAsync(3)

        then:
        StepVerifier.create(result)
//            .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
            .expectNextCount(9)
            .verifyComplete()
    }

    def "nameFluxConcatMap"() {
        when:
        def result = testSubject.namesFluxConcatMap(3)

        then:
        StepVerifier.create(result)
            .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
//            .expectNextCount(9)
            .verifyComplete()
    }

    def "nameMonoFlatMap"() {
        when:
        def namesMono = testSubject.namesMonoFlatMap(3)

        then:
        StepVerifier.create(namesMono)
            .expectNext(List.of("A", "L", "E", "X"))
            .verifyComplete()
    }

    def "namesMonoFlatMapMan"() {
        when:
        def result = testSubject.namesMonoFlatMapMany(3)

        then:
        StepVerifier.create(result)
            .expectNext("A", "L", "E", "X")
            .verifyComplete()
    }

    def "namesFluxTransform"() {
        when:
        def result = testSubject.namesFluxTransform(3)

        then:
        StepVerifier.create(result)
            .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
            .verifyComplete()
    }

    def "namesFluxTransformLength6"() {
        when:
        def result = testSubject.namesFluxTransform(6)

        then:
        StepVerifier.create(result)
            .expectNext("default")
            .verifyComplete()
    }

    def "namesFluxTransformSwitchIfEmpty"() {
        when:
        def result = testSubject.namesFluxTransformSwitchIfEmpty(6)

        then:
        StepVerifier.create(result)
            .expectNext("D", "E", "F", "A", "U", "L", "T")
            .verifyComplete()
    }

    def "exploreConcat"() {
        when:
        def result = testSubject.exploreConcat()

        then:
        StepVerifier.create(result)
            .expectNext("A", "B", "C", "D", "E", "F")
            .verifyComplete()
    }

    def "exploreConcatWith"() {
        when:
        def result = testSubject.exploreConcatWith()

        then:
        StepVerifier.create(result)
            .expectNext("A", "B", "C", "D", "E", "F")
            .verifyComplete()
    }

    def "exploreConcatWithMono"() {
        when:
        def result = testSubject.exploreConcatWithMono()

        then:
        StepVerifier.create(result)
            .expectNext("A", "B")
            .verifyComplete()
    }

    def "exploreMerge"() {
        when:
        def result = testSubject.exploreMerge()

        then:
        StepVerifier.create(result)
            .expectNext("A", "D", "B", "E", "C", "F")
            .verifyComplete()

    }

    def "exploreMergeWith"() {
        when:
        def result = testSubject.exploreMergeWith()

        then:
        StepVerifier.create(result)
            .expectNext("A", "D", "B", "E", "C", "F")
            .verifyComplete()
    }

    def "exploreMergeWithMono"() {
        when:
        def result = testSubject.exploreMergeWithMono()

        then:
        StepVerifier.create(result)
            .expectNext("A", "B")
            .verifyComplete()
    }

    def "exploreMergeSequential"() {
        when:
        def result = testSubject.exploreMergeSequential()

        then:
        StepVerifier.create(result)
            .expectNext("A", "B", "C", "D", "E", "F")
            .verifyComplete()
    }

    def "exploreZip"() {
        when:
        def result = testSubject.exploreZip()

        then:
        StepVerifier.create(result)
            .expectNext("AD", "BE", "CF")
            .verifyComplete()
    }

    def "exploreZipMany"() {
        when:
        def result = testSubject.exploreZipMany()

        then:
        StepVerifier.create(result)
            .expectNext("AD14", "BE25", "CF36")
            .verifyComplete()
    }

    def "exploreZipWith"() {
        when:
        def result = testSubject.exploreZipWith()

        then:
        StepVerifier.create(result)
            .expectNext("AD", "BE", "CF")
            .verifyComplete()
    }

    def "exploreZipWithMono"() {
        when:
        def result = testSubject.exploreZipWithMono()

        then:
        StepVerifier.create(result)
            .expectNext("AB")
            .verifyComplete()
    }
}
