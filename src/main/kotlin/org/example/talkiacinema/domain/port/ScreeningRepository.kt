package org.example.talkiacinema.domain.port

import org.example.talkiacinema.domain.model.Screening
import java.time.LocalDateTime
import java.util.UUID

interface ScreeningRepository {
    fun findById(id: UUID): Screening?
    fun findAll(): List<Screening>
    fun findByMovieId(movieId: UUID): List<Screening>
    fun findByStartTimeBetween(start: LocalDateTime, end: LocalDateTime): List<Screening>
    fun save(screening: Screening): Screening
    fun delete(id: UUID)
}
