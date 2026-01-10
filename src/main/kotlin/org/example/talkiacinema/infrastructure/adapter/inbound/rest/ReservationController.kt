package org.example.talkiacinema.infrastructure.adapter.inbound.rest

import org.example.talkiacinema.application.usecase.CancelReservationUseCase
import org.example.talkiacinema.application.usecase.ReserveSeatsCommand
import org.example.talkiacinema.application.usecase.ReserveSeatsUseCase
import org.example.talkiacinema.infrastructure.adapter.inbound.rest.dto.ReservationRequest
import org.example.talkiacinema.infrastructure.adapter.inbound.rest.dto.ReservationResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/reservations")
class ReservationController(
    private val reserveSeatsUseCase: ReserveSeatsUseCase,
    private val cancelReservationUseCase: CancelReservationUseCase
) {
    
    @PostMapping
    fun createReservation(@RequestBody request: ReservationRequest): ResponseEntity<ReservationResponse> {
        val command = ReserveSeatsCommand(
            screeningId = request.screeningId,
            customerName = request.customerName,
            customerEmail = request.customerEmail,
            seatNumbers = request.seats.map { it.toDomain() }
        )
        
        val reservation = reserveSeatsUseCase.execute(command)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ReservationResponse.from(reservation))
    }
    
    @DeleteMapping("/{id}")
    fun cancelReservation(@PathVariable id: UUID): ResponseEntity<Void> {
        cancelReservationUseCase.execute(id)
        return ResponseEntity.noContent().build()
    }
}
