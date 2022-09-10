package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class FluxAndMonoGeneratorService {
    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("Ben", "Alex", "Chloe"))
            .log();
    }

    public Mono<String> namesMono() {
        return Mono.just("Alex").log();
    }

    public static void main(String[] args) {
        FluxAndMonoGeneratorService service = new FluxAndMonoGeneratorService();

        service.namesFlux()
            .subscribe(name -> System.out.println("Name is " + name));

        service.namesMono()
            .subscribe(name -> System.out.println("Single name is " + name));
    }
}
