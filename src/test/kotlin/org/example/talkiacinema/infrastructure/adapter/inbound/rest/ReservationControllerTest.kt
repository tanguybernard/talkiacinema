package org.example.talkiacinema.infrastructure.adapter.inbound.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.example.talkiacinema.application.usecase.*
import org.example.talkiacinema.domain.model.Reservation
import org.example.talkiacinema.domain.model.ReservationStatus
import org.example.talkiacinema.domain.model.Price
import org.example.talkiacinema.domain.model.SeatNumber
import org.example.talkiacinema.infrastructure.adapter.inbound.rest.dto.ReservationRequest
import org.example.talkiacinema.infrastructure.adapter.inbound.rest.dto.SeatNumberDto
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@WebMvcTest(ReservationController::class)
class ReservationControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @MockkBean
    private lateinit var reserveSeatsUseCase: ReserveSeatsUseCase
    
    @MockkBean
    private lateinit var cancelReservationUseCase: CancelReservationUseCase
    
    @Test
    fun `should create reservation successfully`() {
        // Given
        val screeningId = UUID.randomUUID()
        val request = ReservationRequest(
            screeningId = screeningId,
            customerName = "Alice",
            customerEmail = "alice@example.com",
            seats = listOf(SeatNumberDto(1, 1), SeatNumberDto(1, 2))
        )
        
        val reservation = Reservation(
            id = UUID.randomUUID(),
            screeningId = screeningId,
            customerName = "Alice",
            customerEmail = "alice@example.com",
            seatNumbers = listOf(SeatNumber(1, 1), SeatNumber(1, 2)),
            totalPrice = Price(BigDecimal("30.00")),
            reservedAt = LocalDateTime.now(),
            status = ReservationStatus.ACTIVE
        )
        
        every { reserveSeatsUseCase.execute(any()) } returns reservation
        
        // When & Then
        mockMvc.perform(
            post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.customerName").value("Alice"))
            .andExpect(jsonPath("$.customerEmail").value("alice@example.com"))
            .andExpect(jsonPath("$.seats").isArray)
            .andExpect(jsonPath("$.seats.length()").value(2))
            .andExpect(jsonPath("$.totalPrice").value(30.00))
            .andExpect(jsonPath("$.status").value("ACTIVE"))
        
        verify { reserveSeatsUseCase.execute(any()) }
    }
    
    @Test
    fun `should return 404 when screening not found`() {
        val request = ReservationRequest(
            screeningId = UUID.randomUUID(),
            customerName = "Bob",
            customerEmail = "bob@example.com",
            seats = listOf(SeatNumberDto(1, 1))
        )
        
        every { reserveSeatsUseCase.execute(any()) } throws ScreeningNotFoundException(UUID.randomUUID())
        
        mockMvc.perform(
            post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(404))
    }
    
    @Test
    fun `should return 409 when seat not available`() {
        val request = ReservationRequest(
            screeningId = UUID.randomUUID(),
            customerName = "Charlie",
            customerEmail = "charlie@example.com",
            seats = listOf(SeatNumberDto(1, 1))
        )
        
        every { reserveSeatsUseCase.execute(any()) } throws IllegalStateException("Seat not available")
        
        mockMvc.perform(
            post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.status").value(409))
    }
    
    @Test
    fun `should cancel reservation successfully`() {
        val reservationId = UUID.randomUUID()
        
        every { cancelReservationUseCase.execute(reservationId) } returns Unit
        
        mockMvc.perform(delete("/api/reservations/$reservationId"))
            .andExpect(status().isNoContent)
        
        verify { cancelReservationUseCase.execute(reservationId) }
    }
    
    @Test
    fun `should return 404 when cancelling non-existent reservation`() {
        val reservationId = UUID.randomUUID()
        
        every { cancelReservationUseCase.execute(reservationId) } throws ReservationNotFoundException(reservationId)
        
        mockMvc.perform(delete("/api/reservations/$reservationId"))
            .andExpect(status().isNotFound)
    }
}
