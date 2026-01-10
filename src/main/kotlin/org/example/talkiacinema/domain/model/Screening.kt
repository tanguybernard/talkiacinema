package org.example.talkiacinema.domain.model

import org.example.talkiacinema.domain.model.Price
import org.example.talkiacinema.domain.model.SeatNumber
import java.time.LocalDateTime
import java.util.UUID

data class Screening(
    val id: UUID = UUID.randomUUID(),
    val movieId: UUID,
    val startTime: LocalDateTime,
    val price: Price,
    val seats: List<Seat>,
    val totalRows: Int,
    val totalColumns: Int
) {
    init {
        require(totalRows > 0) { "Total rows must be positive" }
        require(totalColumns > 0) { "Total columns must be positive" }
        require(seats.size == totalRows * totalColumns) { 
            "Number of seats must match grid dimensions" 
        }
    }

    fun getAvailableSeats(): List<Seat> = seats.filter { it.isAvailable() }

    fun getSeatByNumber(seatNumber: SeatNumber): Seat? {
        return seats.find { it.seatNumber == seatNumber }
    }

    fun reserveSeats(seatNumbers: List<SeatNumber>): Screening {
        val updatedSeats = seats.map { seat ->
            if (seatNumbers.contains(seat.seatNumber)) {
                seat.reserve()
            } else {
                seat
            }
        }
        return copy(seats = updatedSeats)
    }

    fun releaseSeats(seatNumbers: List<SeatNumber>): Screening {
        val updatedSeats = seats.map { seat ->
            if (seatNumbers.contains(seat.seatNumber)) {
                seat.release()
            } else {
                seat
            }
        }
        return copy(seats = updatedSeats)
    }

    companion object {
        fun createWithSeats(
            movieId: UUID,
            startTime: LocalDateTime,
            price: Price,
            rows: Int,
            columns: Int
        ): Screening {
            val seats = mutableListOf<Seat>()
            for (row in 1..rows) {
                for (col in 1..columns) {
                    seats.add(Seat(seatNumber = SeatNumber(row, col)))
                }
            }
            return Screening(
                movieId = movieId,
                startTime = startTime,
                price = price,
                seats = seats,
                totalRows = rows,
                totalColumns = columns
            )
        }
    }
}
