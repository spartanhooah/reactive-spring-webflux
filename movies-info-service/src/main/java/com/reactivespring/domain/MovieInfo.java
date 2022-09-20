package com.reactivespring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class MovieInfo {
    @Id private String movieInfoId;

    @NotBlank(message = "Movie must have a name.")
    private String name;

    @Positive(message = "Year must be a positive value.")
    private int year;

    @NotEmpty(message = "Cast list must be nonempty.")
    private List<String> cast;
    private LocalDate releaseDate;
}
