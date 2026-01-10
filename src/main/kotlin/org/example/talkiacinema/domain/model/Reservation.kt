package org.example.talkiacinema.domain.model

import org.example.talkiacinema.domain.model.Price
import org.example.talkiacinema.domain.model.SeatNumber
import java.time.LocalDateTime
import java.util.UUID

data class Reservation(
    val id: UUID = UUID.randomUUID(),
    val screeningId: UUID,
    val customerName: String,
    val customerEmail: String,
    val seatNumbers: List<SeatNumber>,
    val totalPrice: Price,
    val reservedAt: LocalDateTime = LocalDateTime.now(),
    val status: ReservationStatus = ReservationStatus.ACTIVE
) {
    init {
        require(customerName.isNotBlank()) { "Customer name cannot be blank" }
        require(customerEmail.isNotBlank()) { "Customer email cannot be blank" }
        require(seatNumbers.isNotEmpty()) { "Must reserve at least one seat" }
    }

    fun cancel(): Reservation {
        require(status == ReservationStatus.ACTIVE) { 
            "Cannot cancel reservation - current status: $status" 
        }
        return copy(status = ReservationStatus.CANCELLED)
    }

    fun isCancelled(): Boolean = status == ReservationStatus.CANCELLED
}

enum class ReservationStatus {
    ACTIVE,
    CANCELLED
}
