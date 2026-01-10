package org.example.talkiacinema.application.usecase

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.example.talkiacinema.domain.model.Reservation
import org.example.talkiacinema.domain.model.ReservationStatus
import org.example.talkiacinema.domain.model.Screening
import org.example.talkiacinema.domain.port.ReservationRepository
import org.example.talkiacinema.domain.port.ScreeningRepository
import org.example.talkiacinema.domain.service.ReservationService
import org.example.talkiacinema.domain.model.Price
import org.example.talkiacinema.domain.model.SeatNumber
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

class CancelReservationUseCaseTest {
    
    private lateinit var reservationRepository: ReservationRepository
    private lateinit var screeningRepository: ScreeningRepository
    private lateinit var reservationService: ReservationService
    private lateinit var useCase: CancelReservationUseCase
    
    @BeforeEach
    fun setup() {
        reservationRepository = mockk()
        screeningRepository = mockk()
        reservationService = ReservationService()
        useCase = CancelReservationUseCase(reservationRepository, screeningRepository, reservationService)
    }
    
    @Test
    fun `should successfully cancel reservation`() {
        // Given
        val reservationId = UUID.randomUUID()
        val screeningId = UUID.randomUUID()
        val seatNumbers = listOf(SeatNumber(1, 1), SeatNumber(1, 2))
        
        val reservation = Reservation(
            id = reservationId,
            screeningId = screeningId,
            customerName = "John Doe",
            customerEmail = "john@example.com",
            seatNumbers = seatNumbers,
            totalPrice = Price(BigDecimal("30.00")),
            status = ReservationStatus.ACTIVE
        )
        
        val screening = Screening.createWithSeats(
            movieId = UUID.randomUUID(),
            startTime = LocalDateTime.now().plusDays(1),
            price = Price(BigDecimal("15.00")),
            rows = 5,
            columns = 10
        ).reserveSeats(seatNumbers)
        
        val screeningSlot = slot<Screening>()
        val reservationSlot = slot<Reservation>()
        
        every { reservationRepository.findById(reservationId) } returns reservation
        every { screeningRepository.findById(screeningId) } returns screening
        every { screeningRepository.save(capture(screeningSlot)) } answers { screeningSlot.captured }
        every { reservationRepository.save(capture(reservationSlot)) } answers { reservationSlot.captured }
        
        // When
        useCase.execute(reservationId)
        
        // Then
        verify { reservationRepository.findById(reservationId) }
        verify { screeningRepository.findById(screeningId) }
        verify { screeningRepository.save(any()) }
        verify { reservationRepository.save(any()) }
        
        // Verify seats were released
        val savedScreening = screeningSlot.captured
        assertThat(savedScreening.getAvailableSeats()).hasSize(50)
        
        // Verify reservation was cancelled
        val savedReservation = reservationSlot.captured
        assertThat(savedReservation.status).isEqualTo(ReservationStatus.CANCELLED)
    }
    
    @Test
    fun `should throw exception when reservation not found`() {
        val reservationId = UUID.randomUUID()
        
        every { reservationRepository.findById(reservationId) } returns null
        
        assertThatThrownBy { useCase.execute(reservationId) }
            .isInstanceOf(ReservationNotFoundException::class.java)
    }
    
    @Test
    fun `should throw exception when reservation already cancelled`() {
        val reservationId = UUID.randomUUID()
        val reservation = Reservation(
            id = reservationId,
            screeningId = UUID.randomUUID(),
            customerName = "Jane",
            customerEmail = "jane@example.com",
            seatNumbers = listOf(SeatNumber(1, 1)),
            totalPrice = Price(BigDecimal("15.00")),
            status = ReservationStatus.CANCELLED
        )
        
        every { reservationRepository.findById(reservationId) } returns reservation
        
        assertThatThrownBy { useCase.execute(reservationId) }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("already cancelled")
    }
}
