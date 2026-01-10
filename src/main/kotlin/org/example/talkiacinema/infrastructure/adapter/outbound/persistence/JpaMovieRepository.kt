package org.example.talkiacinema.infrastructure.adapter.outbound.persistence

import org.example.talkiacinema.infrastructure.adapter.outbound.persistence.entity.MovieEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface JpaMovieRepository : JpaRepository<MovieEntity, UUID>
