package org.example.talkiacinema.application.usecase

import org.example.talkiacinema.domain.model.Screening
import org.example.talkiacinema.domain.port.ScreeningRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ListScreeningsUseCase(
    private val screeningRepository: ScreeningRepository
) {
    
    fun execute(query: ListScreeningsQuery = ListScreeningsQuery()): List<Screening> {
        return when {
            query.startDate != null && query.endDate != null -> {
                screeningRepository.findByStartTimeBetween(query.startDate, query.endDate)
            }
            else -> {
                screeningRepository.findAll()
            }
        }
    }
}

data class ListScreeningsQuery(
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null
)
