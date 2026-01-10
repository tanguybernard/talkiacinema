package org.example.talkiacinema.application.usecase

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.example.talkiacinema.domain.model.Reservation
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

class ReserveSeatsUseCaseTest {
    
    private lateinit var screeningRepository: ScreeningRepository
    private lateinit var reservationRepository: ReservationRepository
    private lateinit var reservationService: ReservationService
    private lateinit var useCase: ReserveSeatsUseCase
    
    @BeforeEach
    fun setup() {
        screeningRepository = mockk()
        reservationRepository = mockk()
        reservationService = ReservationService()
        useCase = ReserveSeatsUseCase(screeningRepository, reservationRepository, reservationService)
    }
    
    @Test
    fun `should successfully reserve seats`() {
        // Given
        val screeningId = UUID.randomUUID()
        val screening = Screening.createWithSeats(
            movieId = UUID.randomUUID(),
            startTime = LocalDateTime.now().plusDays(1),
            price = Price(BigDecimal("15.00")),
            rows = 5,
            columns = 10
        )
        val seatNumbers = listOf(SeatNumber(1, 1), SeatNumber(1, 2))
        val command = ReserveSeatsCommand(
            screeningId = screeningId,
            customerName = "Alice Smith",
            customerEmail = "alice@example.com",
            seatNumbers = seatNumbers
        )
        
        val screeningSlot = slot<Screening>()
        val reservationSlot = slot<Reservation>()
        
        every { screeningRepository.findById(screeningId) } returns screening
        every { screeningRepository.save(capture(screeningSlot)) } answers { screeningSlot.captured }
        every { reservationRepository.save(capture(reservationSlot)) } answers { reservationSlot.captured }
        
        // When
        val result = useCase.execute(command)
        
        // Then
        assertThat(result.customerName).isEqualTo("Alice Smith")
        assertThat(result.customerEmail).isEqualTo("alice@example.com")
        assertThat(result.seatNumbers).hasSize(2)
        assertThat(result.totalPrice.amount).isEqualTo(BigDecimal("30.00"))
        
        verify { screeningRepository.findById(screeningId) }
        verify { screeningRepository.save(any()) }
        verify { reservationRepository.save(any()) }
        
        // Verify seats were reserved in screening
        val savedScreening = screeningSlot.captured
        assertThat(savedScreening.getAvailableSeats()).hasSize(48)
    }
    
    @Test
    fun `should throw exception when screening not found`() {
        val screeningId = UUID.randomUUID()
        val command = ReserveSeatsCommand(
            screeningId = screeningId,
            customerName = "Bob",
            customerEmail = "bob@example.com",
            seatNumbers = listOf(SeatNumber(1, 1))
        )
        
        every { screeningRepository.findById(screeningId) } returns null
        
        assertThatThrownBy { useCase.execute(command) }
            .isInstanceOf(ScreeningNotFoundException::class.java)
    }
    
    @Test
    fun `should throw exception when seat not available`() {
        val screeningId = UUID.randomUUID()
        val screening = Screening.createWithSeats(
            movieId = UUID.randomUUID(),
            startTime = LocalDateTime.now().plusDays(1),
            price = Price(BigDecimal("15.00")),
            rows = 2,
            columns = 2
        ).reserveSeats(listOf(SeatNumber(1, 1)))
        
        val command = ReserveSeatsCommand(
            screeningId = screeningId,
            customerName = "Charlie",
            customerEmail = "charlie@example.com",
            seatNumbers = listOf(SeatNumber(1, 1))
        )
        
        every { screeningRepository.findById(screeningId) } returns screening
        
        assertThatThrownBy { useCase.execute(command) }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("not available")
    }
}
