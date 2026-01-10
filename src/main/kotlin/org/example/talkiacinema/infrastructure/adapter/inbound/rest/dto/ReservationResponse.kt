package org.example.talkiacinema.infrastructure.adapter.inbound.rest.dto

import org.example.talkiacinema.domain.model.Reservation
import org.example.talkiacinema.domain.model.ReservationStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class ReservationResponse(
    val id: UUID,
    val screeningId: UUID,
    val customerName: String,
    val customerEmail: String,
    val seats: List<SeatNumberDto>,
    val totalPrice: BigDecimal,
    val currency: String,
    val reservedAt: LocalDateTime,
    val status: ReservationStatus
) {
    companion object {
        fun from(reservation: Reservation) = ReservationResponse(
            id = reservation.id,
            screeningId = reservation.screeningId,
            customerName = reservation.customerName,
            customerEmail = reservation.customerEmail,
            seats = reservation.seatNumbers.map { SeatNumberDto(it.row, it.column) },
            totalPrice = reservation.totalPrice.amount,
            currency = reservation.totalPrice.currency,
            reservedAt = reservation.reservedAt,
            status = reservation.status
        )
    }
}
