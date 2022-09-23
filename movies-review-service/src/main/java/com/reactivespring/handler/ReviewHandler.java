package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.lang.Long.parseLong;

@Component
@RequiredArgsConstructor
public class ReviewHandler {
    private final ReviewReactiveRepository reviewRepository;

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .flatMap(reviewRepository::save)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }

    public Mono<ServerResponse> getReviews(ServerRequest request) {
        return request.queryParam("movieInfoId")
                .map(
                        movieInfoId ->
                                reviewRepository.findReviewsByMovieInfoId(parseLong(movieInfoId)))
                .map(this::buildResponse)
                .orElseGet(() -> buildResponse(reviewRepository.findAll()));
    }

    public Mono<ServerResponse> updateReview(ServerRequest request) {
        return reviewRepository
                .findById(request.pathVariable("id"))
                .flatMap(
                        review ->
                                request.bodyToMono(Review.class)
                                        .map(
                                                requestReview -> {
                                                    review.setComment(requestReview.getComment());
                                                    review.setRating(requestReview.getRating());

                                                    return review;
                                                }))
                .flatMap(reviewRepository::save)
                .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview));
    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {
        return reviewRepository
                .findById(request.pathVariable("id"))
                .flatMap(review -> reviewRepository.deleteById(review.getReviewId()))
                .then(ServerResponse.noContent().build());
    }

    private Mono<ServerResponse> buildResponse(Flux<Review> reviews) {
        return ServerResponse.ok().body(reviews, Review.class);
    }
}
