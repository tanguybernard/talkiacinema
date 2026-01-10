package org.example.talkiacinema.infrastructure.adapter.inbound.rest.dto

import org.example.talkiacinema.domain.model.Screening
import org.example.talkiacinema.domain.model.SeatStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class ScreeningResponse(
    val id: UUID,
    val movieId: UUID,
    val startTime: LocalDateTime,
    val price: BigDecimal,
    val currency: String,
    val totalRows: Int,
    val totalColumns: Int,
    val availableSeatsCount: Int
) {
    companion object {
        fun from(screening: Screening) = ScreeningResponse(
            id = screening.id,
            movieId = screening.movieId,
            startTime = screening.startTime,
            price = screening.price.amount,
            currency = screening.price.currency,
            totalRows = screening.totalRows,
            totalColumns = screening.totalColumns,
            availableSeatsCount = screening.getAvailableSeats().size
        )
    }
}

data class SeatAvailabilityResponse(
    val screeningId: UUID,
    val totalSeats: Int,
    val availableSeatsCount: Int,
    val reservedSeatsCount: Int,
    val availabilityPercentage: Double,
    val seats: List<SeatDto>
) {
    companion object {
        fun from(screening: Screening) = SeatAvailabilityResponse(
            screeningId = screening.id,
            totalSeats = screening.seats.size,
            availableSeatsCount = screening.getAvailableSeats().size,
            reservedSeatsCount = screening.seats.size - screening.getAvailableSeats().size,
            availabilityPercentage = (screening.getAvailableSeats().size.toDouble() / screening.seats.size * 100),
            seats = screening.seats.map { SeatDto.from(it) }
        )
    }
}

data class SeatDto(
    val row: Int,
    val column: Int,
    val status: SeatStatus
) {
    companion object {
        fun from(seat: org.example.talkiacinema.domain.model.Seat) = SeatDto(
            row = seat.seatNumber.row,
            column = seat.seatNumber.column,
            status = seat.status
        )
    }
}
