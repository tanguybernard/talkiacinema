package org.example.talkiacinema.application.usecase

import org.example.talkiacinema.domain.model.Seat
import org.example.talkiacinema.domain.port.ScreeningRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ViewSeatAvailabilityUseCase(
    private val screeningRepository: ScreeningRepository
) {
    
    fun execute(screeningId: UUID): SeatAvailabilityResult {
        val screening = screeningRepository.findById(screeningId)
            ?: throw ScreeningNotFoundException(screeningId)
        
        val availableSeats = screening.getAvailableSeats()
        val totalSeats = screening.seats.size
        val reservedSeats = screening.seats.filter { !it.isAvailable() }
        
        return SeatAvailabilityResult(
            screeningId = screeningId,
            totalSeats = totalSeats,
            availableSeats = availableSeats,
            reservedSeats = reservedSeats,
            availabilityPercentage = (availableSeats.size.toDouble() / totalSeats * 100)
        )
    }
}

data class SeatAvailabilityResult(
    val screeningId: UUID,
    val totalSeats: Int,
    val availableSeats: List<Seat>,
    val reservedSeats: List<Seat>,
    val availabilityPercentage: Double
)
