package com.reactivespring.controller

import reactor.core.publisher.Sinks
import spock.lang.Specification

class SinksTest extends Specification {
    def "Play with sinks"() {
        given:
        def replaySink = Sinks.many().replay().all()

        when:
        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST)
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST)

        then:
        replaySink.asFlux()
            .subscribe { println "Subscriber 1: $it" }

        // A second subscriber
        replaySink.asFlux()
            .subscribe { println "Subscriber 2: $it" }

        replaySink.tryEmitNext(3)
    }

    def "Multicast sink"() {
        given:
        def multicastSink = Sinks.many().multicast().onBackpressureBuffer()

        when:
        multicastSink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST)
        multicastSink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST)

        then:
        multicastSink.asFlux()
            .subscribe { println "Subscriber 1: $it" }

        multicastSink.asFlux()
            .subscribe { println "Subscriber 2: $it" }

        multicastSink.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST)
    }

    def "Unicast sink"() {
        given:
        def unicast = Sinks.many().unicast().onBackpressureBuffer()

        when:
        unicast.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST)
        unicast.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST)

        then:
        unicast.asFlux()
            .subscribe { println "Subscriber 1: $it" }

        unicast.asFlux()
            .subscribe { println "Subscriber 2: $it" }

        unicast.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST)
    }

}
