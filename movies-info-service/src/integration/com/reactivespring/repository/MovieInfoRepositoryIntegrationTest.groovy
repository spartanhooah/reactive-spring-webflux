package com.reactivespring.repository

import com.reactivespring.domain.MovieInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import reactor.test.StepVerifier
import spock.lang.Specification

import java.time.LocalDate

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryIntegrationTest extends Specification {
    @Autowired
    MovieInfoRepository movieInfoRepository

    def setup() {
        def moviesInfo = [
                new MovieInfo(
                        movieInfoId: null,
                        name: "Batman Begins",
                        cast: [
                                "Christian Bale",
                                "Michael Caine"
                        ],
                        releaseDate: LocalDate.parse("2005-06-15")
                ),
                new MovieInfo(
                        movieInfoId: null,
                        name: "The Dark Knight",
                        cast: [
                                "Christian Bale",
                                "Heath Ledger"
                        ],
                        releaseDate: LocalDate.parse("2008-07-18")
                ),
                new MovieInfo(
                        movieInfoId: "abc",
                        name: "Dark Knight Rises",
                        cast: [
                                "Christian Bale",
                                "Tom Hardy"
                        ],
                        releaseDate: LocalDate.parse("2012-07-20")
                )
        ]

        movieInfoRepository.saveAll(moviesInfo).blockLast()
    }

    def "find all documents"() {
        when:
        def result = movieInfoRepository.findAll()

        then:
        StepVerifier.create(result)
            .expectNextCount(3)
            .verifyComplete()
    }
}
