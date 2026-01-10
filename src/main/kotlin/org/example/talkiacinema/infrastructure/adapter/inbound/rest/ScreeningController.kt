package org.example.talkiacinema.infrastructure.adapter.inbound.rest

import org.example.talkiacinema.application.usecase.ListScreeningsQuery
import org.example.talkiacinema.application.usecase.ListScreeningsUseCase
import org.example.talkiacinema.application.usecase.ViewSeatAvailabilityUseCase
import org.example.talkiacinema.domain.port.ScreeningRepository
import org.example.talkiacinema.infrastructure.adapter.inbound.rest.dto.SeatAvailabilityResponse
import org.example.talkiacinema.infrastructure.adapter.inbound.rest.dto.ScreeningResponse
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.UUID

@RestController
@RequestMapping("/api/screenings")
class ScreeningController(
    private val listScreeningsUseCase: ListScreeningsUseCase,
    private val viewSeatAvailabilityUseCase: ViewSeatAvailabilityUseCase,
    private val screeningRepository: ScreeningRepository
) {
    
    @GetMapping
    fun listScreenings(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime?
    ): ResponseEntity<List<ScreeningResponse>> {
        val query = ListScreeningsQuery(startDate, endDate)
        val screenings = listScreeningsUseCase.execute(query)
        return ResponseEntity.ok(screenings.map { ScreeningResponse.from(it) })
    }
    
    @GetMapping("/{id}/availability")
    fun getAvailability(@PathVariable id: UUID): ResponseEntity<SeatAvailabilityResponse> {
        val screening = screeningRepository.findById(id)
            ?: return ResponseEntity.notFound().build()
        
        return ResponseEntity.ok(SeatAvailabilityResponse.from(screening))
    }
}
