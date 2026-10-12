package io.freetrade.exampleapplication.data

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import java.math.RoundingMode
import javax.inject.Inject
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

class FakeMarketDataSource @Inject constructor() : MarketDataSource {

    override fun priceUpdates(ticker: String): Flow<PriceUpdate> {
        return flow {
            var price = Random.nextDouble(50.0, 1000.0)
            while (currentCoroutineContext().isActive) {
                delay(Random.nextLong(200L, 1000L).milliseconds)
                price *= Random.nextDouble(0.9, 1.1)
                emit(
                    PriceUpdate(
                        ticker = ticker,
                        price = price.toBigDecimal().setScale(2, RoundingMode.HALF_UP),
                        timestamp = System.currentTimeMillis(),
                    )
                )
            }
        }
    }
}

class FakeInstrumentUniverseDataSource @Inject constructor(): InstrumentUniverseDataSource {

    override fun getAvailableTickers(): List<String> {
        return listOf("TSLA", "AMZN", "MSFT", "AAPL", "LLOY")
    }
}
