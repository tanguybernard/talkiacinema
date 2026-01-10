package org.example.talkiacinema.application.usecase

import org.example.talkiacinema.domain.port.ReservationRepository
import org.example.talkiacinema.domain.port.ScreeningRepository
import org.example.talkiacinema.domain.service.ReservationService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CancelReservationUseCase(
    private val reservationRepository: ReservationRepository,
    private val screeningRepository: ScreeningRepository,
    private val reservationService: ReservationService
) {
    
    @Transactional
    fun execute(reservationId: UUID) {
        // Find reservation
        val reservation = reservationRepository.findById(reservationId)
            ?: throw ReservationNotFoundException(reservationId)
        
        // Validate cancellation
        if (!reservationService.canCancelReservation(reservation)) {
            throw IllegalStateException("Reservation is already cancelled")
        }
        
        // Find screening
        val screening = screeningRepository.findById(reservation.screeningId)
            ?: throw ScreeningNotFoundException(reservation.screeningId)
        
        // Release seats
        val updatedScreening = screening.releaseSeats(reservation.seatNumbers)
        screeningRepository.save(updatedScreening)
        
        // Cancel reservation
        val cancelledReservation = reservation.cancel()
        reservationRepository.save(cancelledReservation)
    }
}

class ReservationNotFoundException(reservationId: UUID) : 
    RuntimeException("Reservation not found: $reservationId")
