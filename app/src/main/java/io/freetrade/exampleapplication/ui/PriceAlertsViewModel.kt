package io.freetrade.exampleapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.freetrade.exampleapplication.data.InstrumentUniverseDataSource
import io.freetrade.exampleapplication.domain.AlertRepository
import io.freetrade.exampleapplication.domain.Direction
import io.freetrade.exampleapplication.domain.PriceAlert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class PriceAlertsViewModel @Inject constructor(
    private val alertRepository: AlertRepository,
    instrumentUniverseDataSource: InstrumentUniverseDataSource,
) : ViewModel() {

    private val latestPrices = mutableMapOf<String, BigDecimal>()

    private val _state = MutableStateFlow(
        PriceAlertsUiState(availableTickers = instrumentUniverseDataSource.getAvailableTickers())
    )
    val state: StateFlow<PriceAlertsUiState> = _state

    init {
        viewModelScope.launch {
            alertRepository.alerts.collect { alerts ->
                removeTriggeredAlerts(alerts)
                refreshActiveAlerts(alerts)
            }
        }
        viewModelScope.launch {
            alertRepository.prices.collect { update ->
                latestPrices[update.ticker] = update.price
                refreshActiveAlerts(alertRepository.alerts.value)
            }
        }
    }

    fun onTickerSelected(ticker: String) {
        _state.update { it.copy(selectedTicker = ticker) }
    }

    fun onTargetPriceChange(input: String) {
        _state.update { it.copy(targetPriceInput = input) }
    }

    fun onDirectionSelected(direction: Direction) {
        _state.update { it.copy(direction = direction) }
    }

    fun onAddAlert() {
        val current = _state.value
        if (!current.canAddAlert) return
        val ticker = current.selectedTicker ?: return
        alertRepository.addAlert(
            PriceAlert(
                ticker = ticker,
                targetPrice = current.targetPriceInput.toBigDecimal().toDouble(),
                direction = current.direction,
                triggered = false,
            )
        )
        _state.update { it.copy(targetPriceInput = "") }
    }

    fun onRemoveAlert(alert: PriceAlert) {
        alertRepository.removeAlert(alert)
    }

    fun onDismissTriggeredBanner() {
        _state.update { it.copy(triggeredAlert = null) }
    }

    private fun removeTriggeredAlerts(alerts: List<PriceAlert>) {
        val triggered = alerts.filter { it.triggered }
        triggered.forEach(alertRepository::removeAlert)
        triggered.lastOrNull()?.let { alert ->
            _state.update { it.copy(triggeredAlert = alert) }
        }
    }

    private fun refreshActiveAlerts(alerts: List<PriceAlert>) {
        latestPrices.keys.retainAll { ticker -> alerts.any { it.ticker == ticker } }
        _state.update { state ->
            state.copy(
                activeAlerts = alerts
                    // Triggered alerts may not have been removed from the repository yet
                    .filterNot { it.triggered }
                    .map { ActiveAlertItem(alert = it, currentPrice = latestPrices[it.ticker]) },
            )
        }
    }
}
