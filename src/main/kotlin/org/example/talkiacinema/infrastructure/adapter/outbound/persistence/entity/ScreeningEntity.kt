package org.example.talkiacinema.infrastructure.adapter.outbound.persistence.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "screenings")
data class ScreeningEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    
    @Column(nullable = false)
    val movieId: UUID,
    
    @Column(nullable = false)
    val startTime: LocalDateTime,
    
    @Column(nullable = false)
    val priceAmount: BigDecimal,
    
    @Column(nullable = false)
    val priceCurrency: String = "EUR",
    
    @Column(nullable = false)
    val totalRows: Int,
    
    @Column(nullable = false)
    val totalColumns: Int,
    
    @OneToMany(mappedBy = "screening", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    val seats: MutableList<SeatEntity> = mutableListOf()
)
