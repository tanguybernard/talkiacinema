package org.example.talkiacinema.infrastructure.adapter.outbound.persistence.entity

import jakarta.persistence.*
import org.example.talkiacinema.domain.model.ReservationStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "reservations")
data class ReservationEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    
    @Column(nullable = false)
    val screeningId: UUID,
    
    @Column(nullable = false)
    val customerName: String,
    
    @Column(nullable = false)
    val customerEmail: String,
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "reservation_seats", joinColumns = [JoinColumn(name = "reservation_id")])
    val seatNumbers: List<SeatNumberEmbeddable>,
    
    @Column(nullable = false)
    val totalPriceAmount: BigDecimal,
    
    @Column(nullable = false)
    val totalPriceCurrency: String = "EUR",
    
    @Column(nullable = false)
    val reservedAt: LocalDateTime,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: ReservationStatus
)

@Embeddable
data class SeatNumberEmbeddable(
    @Column(name = "\"row\"")
    val row: Int,
    
    @Column(name = "\"column\"")
    val column: Int
)
