package org.example.talkiacinema.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.example.talkiacinema.domain.model.Price
import org.example.talkiacinema.domain.model.SeatNumber
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

class ScreeningTest {
    
    @Test
    fun `should create screening with factory method`() {
        val movieId = UUID.randomUUID()
        val startTime = LocalDateTime.now().plusDays(1)
        val price = Price(BigDecimal("10.00"))
        
        val screening = Screening.createWithSeats(movieId, startTime, price, 5, 10)
        
        assertThat(screening.movieId).isEqualTo(movieId)
        assertThat(screening.startTime).isEqualTo(startTime)
        assertThat(screening.price).isEqualTo(price)
        assertThat(screening.totalRows).isEqualTo(5)
        assertThat(screening.totalColumns).isEqualTo(10)
        assertThat(screening.seats).hasSize(50)
        assertThat(screening.getAvailableSeats()).hasSize(50)
    }
    
    @Test
    fun `should validate seat count matches grid dimensions`() {
        val movieId = UUID.randomUUID()
        val startTime = LocalDateTime.now()
        val price = Price(BigDecimal("10.00"))
        val seats = listOf(Seat(seatNumber = SeatNumber(1, 1)))
        
        assertThatThrownBy {
            Screening(
                movieId = movieId,
                startTime = startTime,
                price = price,
                seats = seats,
                totalRows = 5,
                totalColumns = 10
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Number of seats must match grid dimensions")
    }
    
    @Test
    fun `should get available seats`() {
        val screening = Screening.createWithSeats(
            movieId = UUID.randomUUID(),
            startTime = LocalDateTime.now(),
            price = Price(BigDecimal("10.00")),
            rows = 2,
            columns = 2
        )
        
        val updatedScreening = screening.reserveSeats(listOf(SeatNumber(1, 1)))
        
        assertThat(updatedScreening.getAvailableSeats()).hasSize(3)
        assertThat(updatedScreening.seats).hasSize(4)
    }
    
    @Test
    fun `should reserve seats`() {
        val screening = Screening.createWithSeats(
            movieId = UUID.randomUUID(),
            startTime = LocalDateTime.now(),
            price = Price(BigDecimal("10.00")),
            rows = 2,
            columns = 2
        )
        
        val seatsToReserve = listOf(SeatNumber(1, 1), SeatNumber(2, 2))
        val updatedScreening = screening.reserveSeats(seatsToReserve)
        
        val seat1 = updatedScreening.getSeatByNumber(SeatNumber(1, 1))
        val seat2 = updatedScreening.getSeatByNumber(SeatNumber(2, 2))
        
        assertThat(seat1?.status).isEqualTo(SeatStatus.RESERVED)
        assertThat(seat2?.status).isEqualTo(SeatStatus.RESERVED)
        assertThat(updatedScreening.getAvailableSeats()).hasSize(2)
    }
    
    @Test
    fun `should release seats`() {
        val screening = Screening.createWithSeats(
            movieId = UUID.randomUUID(),
            startTime = LocalDateTime.now(),
            price = Price(BigDecimal("10.00")),
            rows = 2,
            columns = 2
        )
        
        val reservedScreening = screening.reserveSeats(listOf(SeatNumber(1, 1)))
        val releasedScreening = reservedScreening.releaseSeats(listOf(SeatNumber(1, 1)))
        
        val seat = releasedScreening.getSeatByNumber(SeatNumber(1, 1))
        assertThat(seat?.status).isEqualTo(SeatStatus.AVAILABLE)
        assertThat(releasedScreening.getAvailableSeats()).hasSize(4)
    }
    
    @Test
    fun `should find seat by number`() {
        val screening = Screening.createWithSeats(
            movieId = UUID.randomUUID(),
            startTime = LocalDateTime.now(),
            price = Price(BigDecimal("10.00")),
            rows = 2,
            columns = 2
        )
        
        val seat = screening.getSeatByNumber(SeatNumber(1, 2))
        
        assertThat(seat).isNotNull
        assertThat(seat?.seatNumber).isEqualTo(SeatNumber(1, 2))
    }
    
    @Test
    fun `should return null for non-existent seat`() {
        val screening = Screening.createWithSeats(
            movieId = UUID.randomUUID(),
            startTime = LocalDateTime.now(),
            price = Price(BigDecimal("10.00")),
            rows = 2,
            columns = 2
        )
        
        val seat = screening.getSeatByNumber(SeatNumber(10, 10))
        
        assertThat(seat).isNull()
    }
}
