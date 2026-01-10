package org.example.talkiacinema.domain.model

import org.example.talkiacinema.domain.model.SeatNumber
import java.util.UUID

data class Seat(
    val id: UUID = UUID.randomUUID(),
    val seatNumber: SeatNumber,
    val status: SeatStatus = SeatStatus.AVAILABLE
) {
    fun reserve(): Seat {
        require(status == SeatStatus.AVAILABLE) { 
            "Cannot reserve seat $seatNumber - current status: $status" 
        }
        return copy(status = SeatStatus.RESERVED)
    }

    fun occupy(): Seat {
        require(status == SeatStatus.RESERVED) { 
            "Cannot occupy seat $seatNumber - must be reserved first" 
        }
        return copy(status = SeatStatus.OCCUPIED)
    }

    fun release(): Seat {
        require(status != SeatStatus.OCCUPIED) { 
            "Cannot release occupied seat $seatNumber" 
        }
        return copy(status = SeatStatus.AVAILABLE)
    }

    fun isAvailable(): Boolean = status == SeatStatus.AVAILABLE
}
