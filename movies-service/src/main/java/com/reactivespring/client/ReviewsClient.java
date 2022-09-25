package com.reactivespring.client;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewsClientException;
import com.reactivespring.exception.ReviewsServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReviewsClient {
    private final WebClient client;

    @Value("${restClient.reviewsUrl}")
    private String reviewsUrl;

    public Flux<Review> getReviewsForMovie(String movieId) {
        return client.get()
                .uri(
                        UriComponentsBuilder.fromHttpUrl(reviewsUrl)
                                .queryParam("movieInfoId", movieId)
                                .buildAndExpand()
                                .toUriString())
                .retrieve()
                .onStatus(
                        HttpStatus::is4xxClientError,
                        clientResponse -> {
                            HttpStatus statusCode = clientResponse.statusCode();

                            if (statusCode.equals(HttpStatus.NOT_FOUND)) {
                                return Mono.empty();
                            }

                            return clientResponse
                                    .bodyToMono(String.class)
                                    .flatMap(
                                            responseMessage ->
                                                    Mono.error(
                                                            new ReviewsClientException(
                                                                    responseMessage)));
                        })
                .onStatus(
                        HttpStatus::is5xxServerError,
                        clientResponse ->
                                clientResponse
                                        .bodyToMono(String.class)
                                        .flatMap(
                                                responseMessage ->
                                                        Mono.error(
                                                                new ReviewsServerException(
                                                                        responseMessage))))
                .bodyToFlux(Review.class)
                .log();
    }
}
