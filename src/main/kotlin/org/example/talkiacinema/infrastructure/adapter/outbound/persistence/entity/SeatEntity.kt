package org.example.talkiacinema.infrastructure.adapter.outbound.persistence.entity

import jakarta.persistence.*
import org.example.talkiacinema.domain.model.SeatStatus
import java.util.UUID

@Entity
@Table(name = "seats")
data class SeatEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    
    @Column(name = "\"row\"", nullable = false)
    val row: Int,
    
    @Column(name = "\"column\"", nullable = false)
    val column: Int,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: SeatStatus,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_id", nullable = false)
    val screening: ScreeningEntity
)
