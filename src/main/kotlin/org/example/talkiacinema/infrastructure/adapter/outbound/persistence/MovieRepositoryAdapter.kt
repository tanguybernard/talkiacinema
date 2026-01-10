package org.example.talkiacinema.infrastructure.adapter.outbound.persistence

import org.example.talkiacinema.domain.model.Movie
import org.example.talkiacinema.domain.port.MovieRepository
import org.example.talkiacinema.infrastructure.adapter.outbound.persistence.entity.MovieEntity
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class MovieRepositoryAdapter(
    private val jpaMovieRepository: JpaMovieRepository
) : MovieRepository {
    
    override fun findById(id: UUID): Movie? {
        return jpaMovieRepository.findById(id)
            .map { it.toDomain() }
            .orElse(null)
    }
    
    override fun findAll(): List<Movie> {
        return jpaMovieRepository.findAll().map { it.toDomain() }
    }
    
    override fun save(movie: Movie): Movie {
        val entity = movie.toEntity()
        return jpaMovieRepository.save(entity).toDomain()
    }
    
    override fun delete(id: UUID) {
        jpaMovieRepository.deleteById(id)
    }
    
    private fun MovieEntity.toDomain() = Movie(
        id = id,
        title = title,
        description = description,
        durationMinutes = durationMinutes
    )
    
    private fun Movie.toEntity() = MovieEntity(
        id = id,
        title = title,
        description = description,
        durationMinutes = durationMinutes
    )
}
