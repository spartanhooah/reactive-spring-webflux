package com.reactivespring.repository

import com.reactivespring.TestSetup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import reactor.test.StepVerifier

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryIntegrationTest extends TestSetup {
    @Autowired
    MovieInfoRepository movieInfoRepository

    def setup() {
       movieInfoRepository.saveAll(generateMovies()).blockLast()
    }

    def cleanup() {
        movieInfoRepository.deleteAll().block()
    }

    def "Find all movies"() {
        when:
        def result = movieInfoRepository.findAll().log()

        then:
        StepVerifier.create(result)
            .expectNextCount(3)
            .verifyComplete()
    }

    def "Find a movie by ID"() {
        when:
        def result = movieInfoRepository.findById("abc").log()

        then:
        StepVerifier.create(result)
            .assertNext {
                it.name == "Dark Knight Rises"
            }
            .verifyComplete()
    }

    def "Save a movie"() {
        when:
        def result = movieInfoRepository.save(SERENITY).log()

        then:
        StepVerifier.create(result)
            .assertNext {
                it.name == "Serenity"
                it.movieInfoId
            }
            .verifyComplete()
    }

    def "Update a movie"() {
        given:
        def movie = movieInfoRepository.findById("abc").block()
        movie.setYear(2021)

        when:
        def result = movieInfoRepository.save(movie).log()

        then:
        StepVerifier.create(result)
            .assertNext {
                // Need assert keyword here
                assert it.year == 2021
            }
            .verifyComplete()
    }

    def "Delete a movie"() {
        given:
        movieInfoRepository.deleteById("abc").log().block()

        when:
        def result = movieInfoRepository.findAll().log()

        then:
        StepVerifier.create(result)
            .expectNextCount(2)
            .verifyComplete()
    }

    def "Get all movies from a given year"() {
        when:
        def result = movieInfoRepository.findByYear(2005).log()

        then:
        StepVerifier.create(result)
            .assertNext {
                assert it.name == "Batman Begins"
            }
            .verifyComplete()
    }
}
