package org.example.talkiacinema.infrastructure.adapter.outbound.persistence

import org.example.talkiacinema.domain.model.Screening
import org.example.talkiacinema.domain.model.Seat
import org.example.talkiacinema.domain.port.ScreeningRepository
import org.example.talkiacinema.domain.model.Price
import org.example.talkiacinema.domain.model.SeatNumber
import org.example.talkiacinema.infrastructure.adapter.outbound.persistence.entity.ScreeningEntity
import org.example.talkiacinema.infrastructure.adapter.outbound.persistence.entity.SeatEntity
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
class ScreeningRepositoryAdapter(
    private val jpaScreeningRepository: JpaScreeningRepository
) : ScreeningRepository {
    
    override fun findById(id: UUID): Screening? {
        return jpaScreeningRepository.findById(id)
            .map { it.toDomain() }
            .orElse(null)
    }
    
    override fun findAll(): List<Screening> {
        return jpaScreeningRepository.findAll().map { it.toDomain() }
    }
    
    override fun findByMovieId(movieId: UUID): List<Screening> {
        return jpaScreeningRepository.findByMovieId(movieId).map { it.toDomain() }
    }
    
    override fun findByStartTimeBetween(start: LocalDateTime, end: LocalDateTime): List<Screening> {
        return jpaScreeningRepository.findByStartTimeBetween(start, end).map { it.toDomain() }
    }
    
    override fun save(screening: Screening): Screening {
        val entity = screening.toEntity()
        return jpaScreeningRepository.save(entity).toDomain()
    }
    
    override fun delete(id: UUID) {
        jpaScreeningRepository.deleteById(id)
    }
    
    private fun ScreeningEntity.toDomain() = Screening(
        id = id,
        movieId = movieId,
        startTime = startTime,
        price = Price(priceAmount, priceCurrency),
        seats = seats.map { it.toDomain() },
        totalRows = totalRows,
        totalColumns = totalColumns
    )
    
    private fun SeatEntity.toDomain() = Seat(
        id = id,
        seatNumber = SeatNumber(row, column),
        status = status
    )
    
    private fun Screening.toEntity(): ScreeningEntity {
        val screeningEntity = ScreeningEntity(
            id = id,
            movieId = movieId,
            startTime = startTime,
            priceAmount = price.amount,
            priceCurrency = price.currency,
            totalRows = totalRows,
            totalColumns = totalColumns
        )
        
        val seatEntities = seats.map { seat ->
            SeatEntity(
                id = seat.id,
                row = seat.seatNumber.row,
                column = seat.seatNumber.column,
                status = seat.status,
                screening = screeningEntity
            )
        }
        
        screeningEntity.seats.clear()
        screeningEntity.seats.addAll(seatEntities)
        
        return screeningEntity
    }
}
