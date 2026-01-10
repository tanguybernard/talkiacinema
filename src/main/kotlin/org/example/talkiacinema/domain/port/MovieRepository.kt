package org.example.talkiacinema.domain.port

import org.example.talkiacinema.domain.model.Movie
import java.util.UUID

interface MovieRepository {
    fun findById(id: UUID): Movie?
    fun findAll(): List<Movie>
    fun save(movie: Movie): Movie
    fun delete(id: UUID)
}
