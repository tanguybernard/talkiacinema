package org.example.talkiacinema.infrastructure.config

import org.example.talkiacinema.domain.service.ReservationService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DomainConfig {
    
    @Bean
    fun reservationService(): ReservationService {
        return ReservationService()
    }
}
