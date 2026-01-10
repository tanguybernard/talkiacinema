package org.example.talkiacinema.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.example.talkiacinema.domain.model.SeatNumber
import org.junit.jupiter.api.Test

class SeatTest {
    
    @Test
    fun `should create seat with available status by default`() {
        val seat = Seat(seatNumber = SeatNumber(1, 1))
        
        assertThat(seat.status).isEqualTo(SeatStatus.AVAILABLE)
        assertThat(seat.isAvailable()).isTrue()
    }
    
    @Test
    fun `should reserve available seat`() {
        val seat = Seat(seatNumber = SeatNumber(1, 1))
        
        val reservedSeat = seat.reserve()
        
        assertThat(reservedSeat.status).isEqualTo(SeatStatus.RESERVED)
        assertThat(reservedSeat.isAvailable()).isFalse()
    }
    
    @Test
    fun `should not reserve already reserved seat`() {
        val seat = Seat(seatNumber = SeatNumber(1, 1), status = SeatStatus.RESERVED)
        
        assertThatThrownBy { seat.reserve() }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Cannot reserve seat")
    }
    
    @Test
    fun `should occupy reserved seat`() {
        val seat = Seat(seatNumber = SeatNumber(1, 1), status = SeatStatus.RESERVED)
        
        val occupiedSeat = seat.occupy()
        
        assertThat(occupiedSeat.status).isEqualTo(SeatStatus.OCCUPIED)
    }
    
    @Test
    fun `should not occupy available seat`() {
        val seat = Seat(seatNumber = SeatNumber(1, 1), status = SeatStatus.AVAILABLE)
        
        assertThatThrownBy { seat.occupy() }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("must be reserved first")
    }
    
    @Test
    fun `should release reserved seat`() {
        val seat = Seat(seatNumber = SeatNumber(1, 1), status = SeatStatus.RESERVED)
        
        val releasedSeat = seat.release()
        
        assertThat(releasedSeat.status).isEqualTo(SeatStatus.AVAILABLE)
        assertThat(releasedSeat.isAvailable()).isTrue()
    }
    
    @Test
    fun `should not release occupied seat`() {
        val seat = Seat(seatNumber = SeatNumber(1, 1), status = SeatStatus.OCCUPIED)
        
        assertThatThrownBy { seat.release() }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Cannot release occupied seat")
    }
}
