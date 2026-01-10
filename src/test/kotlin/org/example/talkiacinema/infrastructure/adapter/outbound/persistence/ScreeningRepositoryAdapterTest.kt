package org.example.talkiacinema.infrastructure.adapter.outbound.persistence

import org.assertj.core.api.Assertions.assertThat
import org.example.talkiacinema.domain.model.Screening
import org.example.talkiacinema.domain.model.Price
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@DataJpaTest
@Import(ScreeningRepositoryAdapter::class)
class ScreeningRepositoryAdapterTest {
    
    @Autowired
    private lateinit var screeningRepositoryAdapter: ScreeningRepositoryAdapter
    
    @Test
    fun `should save and find screening by id`() {
        // Given
        val screening = Screening.createWithSeats(
            movieId = UUID.randomUUID(),
            startTime = LocalDateTime.now().plusDays(1),
            price = Price(BigDecimal("12.50")),
            rows = 5,
            columns = 10
        )
        
        // When
        val saved = screeningRepositoryAdapter.save(screening)
        val found = screeningRepositoryAdapter.findById(saved.id)
        
        // Then
        assertThat(found).isNotNull
        assertThat(found?.id).isEqualTo(saved.id)
        assertThat(found?.movieId).isEqualTo(screening.movieId)
        assertThat(found?.price).isEqualTo(screening.price)
        assertThat(found?.seats).hasSize(50)
        assertThat(found?.totalRows).isEqualTo(5)
        assertThat(found?.totalColumns).isEqualTo(10)
    }
    
    @Test
    fun `should find screenings by movie id`() {
        val movieId = UUID.randomUUID()
        val screening1 = Screening.createWithSeats(
            movieId = movieId,
            startTime = LocalDateTime.now().plusDays(1),
            price = Price(BigDecimal("10.00")),
            rows = 2,
            columns = 2
        )
        val screening2 = Screening.createWithSeats(
            movieId = movieId,
            startTime = LocalDateTime.now().plusDays(2),
            price = Price(BigDecimal("10.00")),
            rows = 2,
            columns = 2
        )
        
        screeningRepositoryAdapter.save(screening1)
        screeningRepositoryAdapter.save(screening2)
        
        val found = screeningRepositoryAdapter.findByMovieId(movieId)
        
        assertThat(found).hasSize(2)
    }
    
    @Test
    fun `should persist seat reservations`() {
        // Given
        val screening = Screening.createWithSeats(
            movieId = UUID.randomUUID(),
            startTime = LocalDateTime.now().plusDays(1),
            price = Price(BigDecimal("10.00")),
            rows = 2,
            columns = 2
        )
        
        val saved = screeningRepositoryAdapter.save(screening)
        
        // When - Reserve seats
        val seatNumbers = listOf(
            org.example.talkiacinema.domain.model.SeatNumber(1, 1),
            org.example.talkiacinema.domain.model.SeatNumber(1, 2)
        )
        val updatedScreening = saved.reserveSeats(seatNumbers)
        screeningRepositoryAdapter.save(updatedScreening)
        
        // Then
        val found = screeningRepositoryAdapter.findById(saved.id)
        assertThat(found?.getAvailableSeats()).hasSize(2)
        assertThat(found?.seats?.filter { !it.isAvailable() }).hasSize(2)
    }
    
    @Test
    fun `should delete screening`() {
        val screening = Screening.createWithSeats(
            movieId = UUID.randomUUID(),
            startTime = LocalDateTime.now().plusDays(1),
            price = Price(BigDecimal("10.00")),
            rows = 2,
            columns = 2
        )
        
        val saved = screeningRepositoryAdapter.save(screening)
        screeningRepositoryAdapter.delete(saved.id)
        
        val found = screeningRepositoryAdapter.findById(saved.id)
        assertThat(found).isNull()
    }
}
