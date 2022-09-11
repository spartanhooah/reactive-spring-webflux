package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {
    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("Ben", "Alex", "Chloe"))
            .log();
    }

    public Flux<String> namesFluxMap() {
        return Flux.fromIterable(List.of("Ben", "Alex", "Chloe"))
            .map(String::toUpperCase)
            .log();
    }

    public Flux<String> namesFluxImmutability() {
        var flux = Flux.fromIterable(List.of("Ben", "Alex", "Chloe"));

        flux.map(String::toUpperCase);

        return flux;
    }

    public Flux<String> namesFluxMapFilter(int stringLength) {
        // Filter names > 3 characters
        return Flux.fromIterable(List.of("Ben", "Alex", "Chloe"))
            .filter(name -> name.length() > stringLength)
            .map(String::toUpperCase)
            .map(name -> name.length() + "-" + name)
            .log();
    }

    public Flux<String> namesFluxFlatMap(int stringLength) {
        // Filter names > 3 characters
        return Flux.fromIterable(List.of("Ben", "Alex", "Chloe"))
            .filter(name -> name.length() > stringLength)
            .map(String::toUpperCase)
            // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
            .flatMap(this::splitStringFlux)
            .log();
    }

    public Flux<String> namesFluxFlatMapAsync(int stringLength) {
        // Filter names > 3 characters
        return Flux.fromIterable(List.of("Ben", "Alex", "Chloe"))
            .filter(name -> name.length() > stringLength)
            .map(String::toUpperCase)
            // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
            .flatMap(this::splitStringWithDelay)
            .log();
    }

    public Flux<String> namesFluxConcatMap(int stringLength) {
        // Filter names > 3 characters
        return Flux.fromIterable(List.of("Ben", "Alex", "Chloe"))
            .filter(name -> name.length() > stringLength)
            .map(String::toUpperCase)
            // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
            .concatMap(this::splitStringWithDelay)
            .log();
    }

    public Flux<String> namesFluxTransform(int stringLength) {
        // Useful to extract common functionality
        Function<Flux<String>, Flux<String>> filterMap = name -> name.filter(s -> s.length() > stringLength)
            .map(String::toUpperCase);

        // Filter names > 3 characters
        return Flux.fromIterable(List.of("Ben", "Alex", "Chloe"))
            .transform(filterMap)
            // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
            .flatMap(this::splitStringFlux)
            .defaultIfEmpty("default")
            .log();
    }

    public Flux<String> namesFluxTransformSwitchIfEmpty(int stringLength) {
        // Useful to extract common functionality
        Function<Flux<String>, Flux<String>> filterMap = name -> name.filter(s -> s.length() > stringLength)
            .map(String::toUpperCase)
            .flatMap(this::splitStringFlux);

        Flux<String> defaultValue = Flux.just("default")
            .transform(filterMap);

        // Filter names > 3 characters
        return Flux.fromIterable(List.of("Ben", "Alex", "Chloe"))
            .transform(filterMap)
            // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
            .switchIfEmpty(defaultValue)
            .log();
    }

    public Flux<String> splitStringWithDelay(String name) {
        var delay = new Random().nextInt(1000);

        return Flux.just(name.split(""))
            .delayElements(Duration.ofMillis(delay));
    }

    public Mono<String> namesMono() {
        return Mono.just("Alex").log();
    }

    public Mono<List<String>> namesMonoFlatMap(int stringLength) {
        return Mono.just("Alex")
            .map(String::toUpperCase)
            .filter(name -> name.length() > stringLength)
            .flatMap(this::splitStringMono) // Mono<List of A, L, E, X>
            .log();
    }

    public Flux<String> namesMonoFlatMapMany(int stringLength) {
        return Mono.just("Alex")
            .map(String::toUpperCase)
            .filter(name -> name.length() > stringLength)
            .flatMapMany(this::splitStringFlux) // Mono<List of A, L, E, X>
            .log();
    }

    public Flux<String> exploreConcat() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return Flux.concat(abcFlux, defFlux).log();
    }

    public Flux<String> exploreConcatWith() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return abcFlux.concatWith(defFlux).log();
    }

    public Flux<String> exploreConcatWithMono() {
        var aMono = Mono.just("A");
        var bMono = Mono.just("B");

        return aMono.concatWith(bMono).log();
    }

    public Flux<String> exploreMerge() {
        var abcFlux = Flux.just("A", "B", "C")
            .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F")
            .delayElements(Duration.ofMillis(125));

        return Flux.merge(abcFlux, defFlux).log();
    }

    public Flux<String> exploreMergeWith() {
        var abcFlux = Flux.just("A", "B", "C")
            .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F")
            .delayElements(Duration.ofMillis(125));

        return abcFlux.mergeWith(defFlux).log();
    }

    public Flux<String> exploreMergeWithMono() {
        var aMono = Mono.just("A");
        var bMono = Flux.just("B");

        return aMono.mergeWith(bMono).log();
    }

    public Flux<String> exploreMergeSequential() {
        var abcFlux = Flux.just("A", "B", "C")
            .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F")
            .delayElements(Duration.ofMillis(125));

        return Flux.mergeSequential(abcFlux, defFlux).log();
    }

    public Flux<String> exploreZip() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return Flux.zip(abcFlux, defFlux, (first, second) -> first + second).log();
    }

    public Flux<String> exploreZipMany() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        var _123Flux = Flux.just("1", "2", "3");
        var _456Flux = Flux.just("4", "5", "6");

        return Flux.zip(abcFlux, defFlux, _123Flux, _456Flux)
            .map(objects -> objects.getT1() + objects.getT2() + objects.getT3() + objects.getT4())
            .log();
    }

    public Flux<String> exploreZipWith() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return abcFlux.zipWith(defFlux, (first, second) -> first + second).log();
    }

    public Mono<String> exploreZipWithMono() {
        var aMono = Mono.just("A");
        var bMono = Mono.just("B");

        return aMono.zipWith(bMono)
            .map(objects -> objects.getT1() + objects.getT2())
            .log();
    }

    private Flux<String> splitStringFlux(String name) {
        return Flux.just(name.split(""));
    }

    private Mono<List<String>> splitStringMono(String name) {
        var charList = List.of(name.split(""));

        return Mono.just(charList);
    }

    public static void main(String[] args) {
        FluxAndMonoGeneratorService service = new FluxAndMonoGeneratorService();

        service.namesFlux()
            .subscribe(name -> System.out.println("Name is " + name));

        service.namesMono()
            .subscribe(name -> System.out.println("Single name is " + name));
    }
}
