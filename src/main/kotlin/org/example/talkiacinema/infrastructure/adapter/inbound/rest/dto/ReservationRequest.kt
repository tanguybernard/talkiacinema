package org.example.talkiacinema.infrastructure.adapter.inbound.rest.dto

import org.example.talkiacinema.domain.model.SeatNumber
import java.util.UUID

data class ReservationRequest(
    val screeningId: UUID,
    val customerName: String,
    val customerEmail: String,
    val seats: List<SeatNumberDto>
)

data class SeatNumberDto(
    val row: Int,
    val column: Int
) {
    fun toDomain() = SeatNumber(row, column)
}
