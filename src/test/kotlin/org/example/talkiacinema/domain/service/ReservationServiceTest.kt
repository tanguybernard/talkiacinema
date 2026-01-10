package org.example.talkiacinema.domain.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.example.talkiacinema.domain.model.Reservation
import org.example.talkiacinema.domain.model.ReservationStatus
import org.example.talkiacinema.domain.model.Screening
import org.example.talkiacinema.domain.model.Price
import org.example.talkiacinema.domain.model.SeatNumber
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

class ReservationServiceTest {
    
    private lateinit var reservationService: ReservationService
    private lateinit var screening: Screening
    
    @BeforeEach
    fun setup() {
        reservationService = ReservationService()
        screening = Screening.createWithSeats(
            movieId = UUID.randomUUID(),
            startTime = LocalDateTime.now().plusDays(1),
            price = Price(BigDecimal("12.50")),
            rows = 5,
            columns = 10
        )
    }
    
    @Test
    fun `should validate successful reservation`() {
        val seatNumbers = listOf(SeatNumber(1, 1), SeatNumber(1, 2))
        
        // Should not throw
        reservationService.validateReservation(screening, seatNumbers)
    }
    
    @Test
    fun `should reject empty seat selection`() {
        assertThatThrownBy {
            reservationService.validateReservation(screening, emptyList())
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Must select at least one seat")
    }
    
    @Test
    fun `should reject non-existent seat`() {
        val seatNumbers = listOf(SeatNumber(99, 99))
        
        assertThatThrownBy {
            reservationService.validateReservation(screening, seatNumbers)
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("does not exist")
    }
    
    @Test
    fun `should reject already reserved seat`() {
        val seatNumbers = listOf(SeatNumber(1, 1))
        val updatedScreening = screening.reserveSeats(seatNumbers)
        
        assertThatThrownBy {
            reservationService.validateReservation(updatedScreening, seatNumbers)
        }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("not available")
    }
    
    @Test
    fun `should reject duplicate seat selection`() {
        val seatNumbers = listOf(SeatNumber(1, 1), SeatNumber(1, 1))
        
        assertThatThrownBy {
            reservationService.validateReservation(screening, seatNumbers)
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Cannot reserve the same seat multiple times")
    }
    
    @Test
    fun `should calculate total price correctly`() {
        val price = reservationService.calculateTotalPrice(screening, 3)
        
        assertThat(price.amount).isEqualTo(BigDecimal("37.50"))
        assertThat(price.currency).isEqualTo("EUR")
    }
    
    @Test
    fun `should allow cancellation of active reservation`() {
        val reservation = Reservation(
            screeningId = UUID.randomUUID(),
            customerName = "John Doe",
            customerEmail = "john@example.com",
            seatNumbers = listOf(SeatNumber(1, 1)),
            totalPrice = Price(BigDecimal("12.50")),
            status = ReservationStatus.ACTIVE
        )
        
        val canCancel = reservationService.canCancelReservation(reservation)
        
        assertThat(canCancel).isTrue()
    }
    
    @Test
    fun `should not allow cancellation of already cancelled reservation`() {
        val reservation = Reservation(
            screeningId = UUID.randomUUID(),
            customerName = "John Doe",
            customerEmail = "john@example.com",
            seatNumbers = listOf(SeatNumber(1, 1)),
            totalPrice = Price(BigDecimal("12.50")),
            status = ReservationStatus.CANCELLED
        )
        
        val canCancel = reservationService.canCancelReservation(reservation)
        
        assertThat(canCancel).isFalse()
    }
}
