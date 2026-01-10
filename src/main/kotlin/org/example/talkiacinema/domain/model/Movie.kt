package org.example.talkiacinema.domain.model

import java.util.UUID

data class Movie(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val description: String,
    val durationMinutes: Int
) {
    init {
        require(title.isNotBlank()) { "Movie title cannot be blank" }
        require(durationMinutes > 0) { "Movie duration must be positive" }
    }
}
