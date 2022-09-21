package com.reactivespring

import com.reactivespring.domain.MovieInfo
import spock.lang.Specification

import java.time.LocalDate

class TestSetup extends Specification {
    static final def SERENITY = new MovieInfo(
            movieInfoId: "mockId",
            name: "Serenity",
            cast: [
                    "Nathan Fillion",
                    "Jewel Staite",
                    "Alan Tudyk",
                    "Summer Glau",
                    "Ron Glass",
                    "Gina Torres",
                    "Adam Baldwin",
                    "Morena Baccarin",
                    "Sean Maher"
            ],
            year: 2005,
            releaseDate: LocalDate.parse("2005-09-30")
    )

    static def generateMovies() {
        [
                new MovieInfo(
                        name: "Batman Begins",
                        cast: [
                                "Christian Bale",
                                "Michael Caine"
                        ],
                        year: 2005,
                        releaseDate: LocalDate.parse("2005-06-15")
                ),
                new MovieInfo(
                        name: "The Dark Knight",
                        cast: [
                                "Christian Bale",
                                "Heath Ledger"
                        ],
                        year: 2008,
                        releaseDate: LocalDate.parse("2008-07-18")
                ),
                new MovieInfo(
                        movieInfoId: "abc",
                        name: "Dark Knight Rises",
                        cast: [
                                "Christian Bale",
                                "Tom Hardy"
                        ],
                        year: 2012,
                        releaseDate: LocalDate.parse("2012-07-20")
                )
        ]
    }
}
