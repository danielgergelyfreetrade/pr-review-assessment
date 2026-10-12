package io.freetrade.exampleapplication.data

import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface MarketDataSource {

    // Opens a new network connection to get the stream
    fun priceUpdates(ticker: String): Flow<PriceUpdate>
}

data class PriceUpdate(
    val ticker: String,
    val price: BigDecimal,
    val timestamp: Long,
)
