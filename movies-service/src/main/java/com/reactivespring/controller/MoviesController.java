package com.reactivespring.controller;

import com.reactivespring.client.MoviesInfoClient;
import com.reactivespring.client.ReviewsClient;
import com.reactivespring.domain.Movie;
import com.reactivespring.domain.MovieInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/movies")
@RequiredArgsConstructor
public class MoviesController {
    private final MoviesInfoClient infoClient;
    private final ReviewsClient reviewsClient;

    @GetMapping("/{id}")
    public Mono<Movie> retrieveMovieById(@PathVariable("id") String movieId) {
        return infoClient
                .retrieveMovieInfo(movieId)
                .flatMap(
                        movieInfo ->
                                reviewsClient
                                        .getReviewsForMovie(movieId)
                                        .collectList()
                                        .map(reviews -> new Movie(movieInfo, reviews)));
    }

    @GetMapping(value = "/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MovieInfo> retrieveMovieInfos() {
        return infoClient.retrieveMovieInfoStream();
    }
}
