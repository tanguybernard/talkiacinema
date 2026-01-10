package org.example.talkiacinema.domain.port

import org.example.talkiacinema.domain.model.Reservation
import java.util.UUID

interface ReservationRepository {
    fun findById(id: UUID): Reservation?
    fun findAll(): List<Reservation>
    fun findByScreeningId(screeningId: UUID): List<Reservation>
    fun findByCustomerEmail(email: String): List<Reservation>
    fun save(reservation: Reservation): Reservation
    fun delete(id: UUID)
}
