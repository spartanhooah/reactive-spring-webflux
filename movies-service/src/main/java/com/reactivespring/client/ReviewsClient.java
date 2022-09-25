package com.reactivespring.client;

import com.reactivespring.domain.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

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
                .bodyToFlux(Review.class)
                .log();
    }
}
