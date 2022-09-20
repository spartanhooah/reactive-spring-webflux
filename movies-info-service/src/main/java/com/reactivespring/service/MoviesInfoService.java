package com.reactivespring.service;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MoviesInfoService {
    private final MovieInfoRepository repository;

    public Mono<MovieInfo> addMovieInfo(MovieInfo movie) {
        return repository.save(movie);
    }

    public Flux<MovieInfo> getAllMovieInfos() {
        return repository.findAll();
    }

    public Mono<MovieInfo> getMovieInfoById(String id) {
        return repository.findById(id);
    }

    public Mono<MovieInfo> updateMovieInfo(MovieInfo movie, String id) {
        return repository.findById(id)
            .flatMap(movieInfo -> {
                movieInfo.setCast(movie.getCast());
                movieInfo.setName(movie.getName());
                movieInfo.setYear(movie.getYear());
                movieInfo.setReleaseDate(movie.getReleaseDate());

                return repository.save(movieInfo);
            });
    }

    public Mono<Void> deleteMovieInfo(String id) {
        return repository.deleteById(id);
    }
}
