package com.reactivespring.router;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.reactivespring.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ReviewRouter {
    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler) {
        return route().nest( // how to group endpoints that have the same path
                        path("/v1/reviews"),
                        builder ->
                                builder.POST(reviewHandler::addReview)
                                        .GET(reviewHandler::getReviews)
                                        .PUT("/{id}", reviewHandler::updateReview)
                                        .DELETE("/{id}", reviewHandler::deleteReview)
                                        .GET("/stream", reviewHandler::getReviewsStream))
                .GET("/v1/hello-world", (request -> ServerResponse.ok().bodyValue("Hello, world!")))
                // the POST and GET for /v1/reviews could be here as well in separate lines
                .build();
    }
}
