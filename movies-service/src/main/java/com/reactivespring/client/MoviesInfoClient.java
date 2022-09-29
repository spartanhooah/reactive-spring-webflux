package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class MoviesInfoClient {
    private final WebClient client;

    @Value("${restClient.moviesInfoUrl}")
    private String moviesInfoUrl;

    public Mono<MovieInfo> retrieveMovieInfo(String movieId) {
        var retrySpec =
                Retry.fixedDelay(3, Duration.ofSeconds(1))
                        .filter(throwable -> throwable instanceof MoviesInfoServerException)
                        .onRetryExhaustedThrow(
                                (retryBackoffSpec, retrySignal) ->
                                        Exceptions.propagate(retrySignal.failure()));

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
                .retryWhen(retrySpec)
                .log();
    }
}
