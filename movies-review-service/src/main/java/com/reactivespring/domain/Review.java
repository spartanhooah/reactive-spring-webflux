package com.reactivespring.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Review {
    @Id private String reviewId;

    @NotNull(message = "movieInfoId is required")
    private Long movieInfoId;

    private String comment;

    @Min(value = 0L, message = "please provide a non-negative rating")
    private Double rating;
}
