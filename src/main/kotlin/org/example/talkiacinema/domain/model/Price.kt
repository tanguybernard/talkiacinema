package org.example.talkiacinema.domain.model

import java.math.BigDecimal

data class Price(
    val amount: BigDecimal,
    val currency: String = "EUR"
) {
    init {
        require(amount >= BigDecimal.ZERO) { "Price cannot be negative" }
        require(currency.isNotBlank()) { "Currency cannot be blank" }
    }

    operator fun plus(other: Price): Price {
        require(currency == other.currency) { "Cannot add prices with different currencies" }
        return Price(amount + other.amount, currency)
    }

    operator fun times(multiplier: Int): Price {
        require(multiplier >= 0) { "Multiplier cannot be negative" }
        return Price(amount * BigDecimal(multiplier), currency)
    }

    override fun toString(): String = "$amount $currency"
}
