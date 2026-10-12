package io.freetrade.exampleapplication.domain

import android.util.Log
import io.freetrade.exampleapplication.data.MarketDataSource
import io.freetrade.exampleapplication.data.PriceUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertRepository @Inject constructor(
    private val dataSource: MarketDataSource,
) {

    private val jobs = mutableMapOf<String, Job>()

    private val _alerts = MutableStateFlow<List<PriceAlert>>(emptyList())
    val alerts: StateFlow<List<PriceAlert>> = _alerts

    private val _prices = MutableSharedFlow<PriceUpdate>()
    val prices: SharedFlow<PriceUpdate> = _prices

    fun addAlert(alert: PriceAlert) {
        val updated = _alerts.updateAndGet { it + alert }
        if (updated.count { it.ticker == alert.ticker } == 1) {
            startWatching(alert.ticker)
        }
    }

    fun removeAlert(alert: PriceAlert) {
        val updated = _alerts.updateAndGet { alerts ->
            val index = alerts.indexOf(alert).takeIf { it >= 0 }
                ?: alerts.indexOfFirst { it.copy(triggered = alert.triggered) == alert }
            if (index >= 0) alerts.toMutableList().apply { removeAt(index) } else alerts
        }
        if (updated.none { it.ticker == alert.ticker }) {
            jobs.remove(alert.ticker)?.cancel()
        }
    }

    private fun startWatching(ticker: String) {
        jobs[ticker] = GlobalScope.launch(Dispatchers.IO) {
            try {
                dataSource.priceUpdates(ticker).collect { update ->
                    val updated = _alerts.updateAndGet { alerts ->
                        alerts.map { if (it.ticker == ticker) it.updatedWith(update.price) else it }
                    }
                    if (updated.any { it.ticker == ticker }) {
                        _prices.emit(update)
                    }
                }
            } catch (e: Exception) {
                Log.e("AlertManager", "error", e)
            }
        }
    }

    private fun PriceAlert.updatedWith(price: BigDecimal): PriceAlert {
        if (triggered) return this
        val target = targetPrice.toBigDecimal()
        val isReached = when (direction) {
            Direction.ABOVE -> price >= target
            Direction.BELOW -> price <= target
        }
        return if (isReached) copy(triggered = true) else this
    }
}
