package org.example.talkiacinema.infrastructure.adapter.outbound.persistence

import org.example.talkiacinema.infrastructure.adapter.outbound.persistence.entity.ScreeningEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface JpaScreeningRepository : JpaRepository<ScreeningEntity, UUID> {
    fun findByMovieId(movieId: UUID): List<ScreeningEntity>
    fun findByStartTimeBetween(start: LocalDateTime, end: LocalDateTime): List<ScreeningEntity>
}
