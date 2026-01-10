package org.example.talkiacinema.infrastructure.adapter.outbound.persistence

import org.example.talkiacinema.infrastructure.adapter.outbound.persistence.entity.ReservationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface JpaReservationRepository : JpaRepository<ReservationEntity, UUID> {
    fun findByScreeningId(screeningId: UUID): List<ReservationEntity>
    fun findByCustomerEmail(email: String): List<ReservationEntity>
}
