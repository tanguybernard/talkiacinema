package org.example.talkiacinema.domain.service

import org.example.talkiacinema.domain.model.Reservation
import org.example.talkiacinema.domain.model.Screening
import org.example.talkiacinema.domain.model.SeatNumber

class ReservationService {
    
    fun validateReservation(screening: Screening, seatNumbers: List<SeatNumber>) {
        require(seatNumbers.isNotEmpty()) { "Must select at least one seat" }
        
        // Check all seats exist
        seatNumbers.forEach { seatNumber ->
            val seat = screening.getSeatByNumber(seatNumber)
                ?: throw IllegalArgumentException("Seat $seatNumber does not exist")
            
            // Check seat is available
            if (!seat.isAvailable()) {
                throw IllegalStateException("Seat $seatNumber is not available")
            }
        }
        
        // Check for duplicate seat selections
        val uniqueSeats = seatNumbers.toSet()
        if (uniqueSeats.size != seatNumbers.size) {
            throw IllegalArgumentException("Cannot reserve the same seat multiple times")
        }
    }
    
    fun calculateTotalPrice(screening: Screening, numberOfSeats: Int) = 
        screening.price * numberOfSeats
    
    fun canCancelReservation(reservation: Reservation): Boolean {
        return !reservation.isCancelled()
    }
}
