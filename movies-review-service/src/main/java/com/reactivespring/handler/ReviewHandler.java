package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.exception.ReviewNotFoundException;
import com.reactivespring.repository.ReviewReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.stream.Collectors;

import static java.lang.Long.parseLong;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewHandler {
    private final Validator validator;
    private final ReviewReactiveRepository reviewRepository;

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .doOnNext(this::validate)
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
        String reviewId = request.pathVariable("id");
        return reviewRepository
                .findById(reviewId)
                // Can either return a Mono::error here, or a ServerResponse at the end
                .switchIfEmpty(
                        Mono.error(
                                new ReviewNotFoundException(
                                        "Review with id " + reviewId + " not found")))
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

    private void validate(Review review) {
        var violations = validator.validate(review);

        log.info("Constraint violations: " + violations);

        if (!violations.isEmpty()) {
            var errorMessage =
                    violations.stream()
                            .map(ConstraintViolation::getMessage)
                            .sorted()
                            .collect(Collectors.joining("; "));

            throw new ReviewDataException(errorMessage);
        }
    }

    private Mono<ServerResponse> buildResponse(Flux<Review> reviews) {
        return ServerResponse.ok().body(reviews, Review.class);
    }
}
