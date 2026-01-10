package org.example.talkiacinema.infrastructure.adapter.outbound.persistence

import org.example.talkiacinema.domain.model.Reservation
import org.example.talkiacinema.domain.port.ReservationRepository
import org.example.talkiacinema.domain.model.Price
import org.example.talkiacinema.domain.model.SeatNumber
import org.example.talkiacinema.infrastructure.adapter.outbound.persistence.entity.ReservationEntity
import org.example.talkiacinema.infrastructure.adapter.outbound.persistence.entity.SeatNumberEmbeddable
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ReservationRepositoryAdapter(
    private val jpaReservationRepository: JpaReservationRepository
) : ReservationRepository {
    
    override fun findById(id: UUID): Reservation? {
        return jpaReservationRepository.findById(id)
            .map { it.toDomain() }
            .orElse(null)
    }
    
    override fun findAll(): List<Reservation> {
        return jpaReservationRepository.findAll().map { it.toDomain() }
    }
    
    override fun findByScreeningId(screeningId: UUID): List<Reservation> {
        return jpaReservationRepository.findByScreeningId(screeningId).map { it.toDomain() }
    }
    
    override fun findByCustomerEmail(email: String): List<Reservation> {
        return jpaReservationRepository.findByCustomerEmail(email).map { it.toDomain() }
    }
    
    override fun save(reservation: Reservation): Reservation {
        val entity = reservation.toEntity()
        return jpaReservationRepository.save(entity).toDomain()
    }
    
    override fun delete(id: UUID) {
        jpaReservationRepository.deleteById(id)
    }
    
    private fun ReservationEntity.toDomain() = Reservation(
        id = id,
        screeningId = screeningId,
        customerName = customerName,
        customerEmail = customerEmail,
        seatNumbers = seatNumbers.map { SeatNumber(it.row, it.column) },
        totalPrice = Price(totalPriceAmount, totalPriceCurrency),
        reservedAt = reservedAt,
        status = status
    )
    
    private fun Reservation.toEntity() = ReservationEntity(
        id = id,
        screeningId = screeningId,
        customerName = customerName,
        customerEmail = customerEmail,
        seatNumbers = seatNumbers.map { SeatNumberEmbeddable(it.row, it.column) },
        totalPriceAmount = totalPrice.amount,
        totalPriceCurrency = totalPrice.currency,
        reservedAt = reservedAt,
        status = status
    )
}
