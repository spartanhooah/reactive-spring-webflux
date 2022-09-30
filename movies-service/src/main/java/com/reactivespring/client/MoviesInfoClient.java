package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.util.RetryUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MoviesInfoClient {
    private final WebClient client;

    @Value("${restClient.moviesInfoUrl}")
    private String moviesInfoUrl;

    public Mono<MovieInfo> retrieveMovieInfo(String movieId) {
        return client.get()
                .uri(moviesInfoUrl.concat("/{id}"), movieId)
                .retrieve()
                .onStatus(
                        HttpStatus::is4xxClientError,
                        clientResponse -> {
                            HttpStatus statusCode = clientResponse.statusCode();

                            if (statusCode.equals(HttpStatus.NOT_FOUND)) {
                                return Mono.error(
                                        new MoviesInfoClientException(
                                                "No movie info found for movie ID " + movieId,
                                                statusCode.value()));
                            }

                            return clientResponse
                                    .bodyToMono(String.class)
                                    .flatMap(
                                            responseMessage ->
                                                    Mono.error(
                                                            new MoviesInfoClientException(
                                                                    responseMessage,
                                                                    statusCode.value())));
                        })
                .onStatus(
                        HttpStatus::is5xxServerError,
                        clientResponse ->
                                clientResponse
                                        .bodyToMono(String.class)
                                        .flatMap(
                                                responseMessage ->
                                                        Mono.error(
                                                                new MoviesInfoServerException(
                                                                        responseMessage))))
                .bodyToMono(MovieInfo.class)
                // .retry(3)
                .retryWhen(RetryUtil.retrySpec())
                .log();
    }

    public Flux<MovieInfo> retrieveMovieInfoStream() {
        return client.get()
            .uri(moviesInfoUrl.concat("/stream"))
            .retrieve()
            .onStatus(
                HttpStatus::is4xxClientError,
                clientResponse -> {
                    HttpStatus statusCode = clientResponse.statusCode();

                    return clientResponse
                        .bodyToMono(String.class)
                        .flatMap(
                            responseMessage ->
                                Mono.error(
                                    new MoviesInfoClientException(
                                        responseMessage,
                                        statusCode.value())));
                })
            .onStatus(
                HttpStatus::is5xxServerError,
                clientResponse ->
                    clientResponse
                        .bodyToMono(String.class)
                        .flatMap(
                            responseMessage ->
                                Mono.error(
                                    new MoviesInfoServerException(
                                        responseMessage))))
            .bodyToFlux(MovieInfo.class)
            // .retry(3)
            .retryWhen(RetryUtil.retrySpec())
            .log();
    }
}
