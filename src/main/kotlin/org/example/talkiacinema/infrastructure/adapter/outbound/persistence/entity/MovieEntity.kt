package org.example.talkiacinema.infrastructure.adapter.outbound.persistence.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "movies")
data class MovieEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    
    @Column(nullable = false)
    val title: String,
    
    @Column(length = 1000)
    val description: String,
    
    @Column(nullable = false)
    val durationMinutes: Int
)
