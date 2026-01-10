package org.example.talkiacinema.domain.model

data class SeatNumber(
    val row: Int,
    val column: Int
) {
    init {
        require(row > 0) { "Row must be positive" }
        require(column > 0) { "Column must be positive" }
    }

    override fun toString(): String = "Row $row, Seat $column"
}
