package org.example.talkiacinema.application.usecase

import org.example.talkiacinema.domain.model.Reservation
import org.example.talkiacinema.domain.port.ReservationRepository
import org.example.talkiacinema.domain.port.ScreeningRepository
import org.example.talkiacinema.domain.service.ReservationService
import org.example.talkiacinema.domain.model.SeatNumber
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ReserveSeatsUseCase(
    private val screeningRepository: ScreeningRepository,
    private val reservationRepository: ReservationRepository,
    private val reservationService: ReservationService
) {
    
    @Transactional
    fun execute(command: ReserveSeatsCommand): Reservation {
        // Find screening
        val screening = screeningRepository.findById(command.screeningId)
            ?: throw ScreeningNotFoundException(command.screeningId)
        
        // Validate reservation
        reservationService.validateReservation(screening, command.seatNumbers)
        
        // Calculate total price
        val totalPrice = reservationService.calculateTotalPrice(screening, command.seatNumbers.size)
        
        // Reserve seats in screening
        val updatedScreening = screening.reserveSeats(command.seatNumbers)
        screeningRepository.save(updatedScreening)
        
        // Create reservation
        val reservation = Reservation(
            screeningId = command.screeningId,
            customerName = command.customerName,
            customerEmail = command.customerEmail,
            seatNumbers = command.seatNumbers,
            totalPrice = totalPrice
        )
        
        return reservationRepository.save(reservation)
    }
}

data class ReserveSeatsCommand(
    val screeningId: UUID,
    val customerName: String,
    val customerEmail: String,
    val seatNumbers: List<SeatNumber>
)

class ScreeningNotFoundException(screeningId: UUID) : 
    RuntimeException("Screening not found: $screeningId")
