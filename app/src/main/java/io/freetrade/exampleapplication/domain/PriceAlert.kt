package io.freetrade.exampleapplication.domain

data class PriceAlert(
    val ticker: String,
    val targetPrice: Double,
    val direction: Direction,
    val triggered: Boolean,
)

enum class Direction { ABOVE, BELOW }
