package io.freetrade.exampleapplication.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.freetrade.exampleapplication.R
import io.freetrade.exampleapplication.designsystem.Banner
import io.freetrade.exampleapplication.designsystem.DecimalTextField
import io.freetrade.exampleapplication.designsystem.Dropdown
import io.freetrade.exampleapplication.designsystem.EmptyState
import io.freetrade.exampleapplication.designsystem.ListRow
import io.freetrade.exampleapplication.designsystem.PrimaryButton
import io.freetrade.exampleapplication.designsystem.SectionTitle
import io.freetrade.exampleapplication.domain.Direction
import io.freetrade.exampleapplication.domain.PriceAlert
import java.math.BigDecimal
import java.math.RoundingMode


data class PriceAlertsUiState(
    val activeAlerts: List<ActiveAlertItem> = emptyList(),
    val availableTickers: List<String> = emptyList(),
    val selectedTicker: String? = null,
    val targetPriceInput: String = "",
    val direction: Direction = Direction.ABOVE,
    val triggeredAlert: PriceAlert? = null,
) {
    val canAddAlert: Boolean
        get() = selectedTicker != null && targetPriceInput.toBigDecimalOrNull()?.signum() == 1
}

data class ActiveAlertItem(
    val alert: PriceAlert,
    val currentPrice: BigDecimal?,
)

@Composable
fun PriceAlertsScreen(
    state: PriceAlertsUiState,
    onTickerSelected: (String) -> Unit,
    onTargetPriceChange: (String) -> Unit,
    onDirectionSelected: (Direction) -> Unit,
    onAddAlert: () -> Unit,
    onRemoveAlert: (PriceAlert) -> Unit,
    onDismissTriggeredBanner: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().imePadding()) {
        state.triggeredAlert?.let { alert ->
            TriggeredAlertBanner(alert = alert, onDismiss = onDismissTriggeredBanner)
        }
        ActiveAlertsSection(
            alerts = state.activeAlerts,
            onRemoveAlert = onRemoveAlert,
            modifier = Modifier.weight(1f),
        )
        HorizontalDivider()
        AddAlertSection(
            tickers = state.availableTickers,
            selectedTicker = state.selectedTicker,
            targetPriceInput = state.targetPriceInput,
            direction = state.direction,
            canAddAlert = state.canAddAlert,
            onTickerSelected = onTickerSelected,
            onTargetPriceChange = onTargetPriceChange,
            onDirectionSelected = onDirectionSelected,
            onAddAlert = onAddAlert,
        )
    }
}

@Composable
private fun TriggeredAlertBanner(
    alert: PriceAlert,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Banner(
        message = stringResource(
            alert.direction.triggeredMessageRes(),
            alert.ticker,
            formatPrice(alert.targetPrice),
        ),
        onDismiss = onDismiss,
        dismissContentDescription = stringResource(R.string.price_alerts_dismiss_banner),
        modifier = modifier,
    )
}

@Composable
private fun ActiveAlertsSection(
    alerts: List<ActiveAlertItem>,
    onRemoveAlert: (PriceAlert) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionTitle(
            text = stringResource(R.string.price_alerts_active_title),
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
        )
        if (alerts.isEmpty()) {
            EmptyState(text = stringResource(R.string.price_alerts_empty))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                // PriceAlert has no id and duplicates are allowed, so there is no stable key
                itemsIndexed(alerts) { index, item ->
                    if (index > 0) HorizontalDivider()
                    ActiveAlertRow(
                        item = item,
                        onRemove = { onRemoveAlert(item.alert) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveAlertRow(
    item: ActiveAlertItem,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListRow(
        title = item.alert.ticker,
        subtitle = stringResource(
            item.alert.direction.targetRes(),
            formatPrice(item.alert.targetPrice),
        ),
        value = item.currentPrice?.let(::formatPrice)
            ?: stringResource(R.string.price_alerts_price_placeholder),
        valueCaption = stringResource(R.string.price_alerts_last_price),
        actionIcon = painterResource(R.drawable.ic_delete),
        actionContentDescription = stringResource(
            R.string.price_alerts_remove_alert,
            item.alert.ticker,
        ),
        onActionClick = onRemove,
        modifier = modifier,
    )
}

@Composable
private fun AddAlertSection(
    tickers: List<String>,
    selectedTicker: String?,
    targetPriceInput: String,
    direction: Direction,
    canAddAlert: Boolean,
    onTickerSelected: (String) -> Unit,
    onTargetPriceChange: (String) -> Unit,
    onDirectionSelected: (Direction) -> Unit,
    onAddAlert: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        SectionTitle(text = stringResource(R.string.price_alerts_add_title))
        Dropdown(
            options = tickers,
            selected = selectedTicker,
            onSelected = onTickerSelected,
            label = stringResource(R.string.price_alerts_instrument_label),
            optionLabel = { it },
            modifier = Modifier.fillMaxWidth(),
        )
        DecimalTextField(
            value = targetPriceInput,
            onValueChange = onTargetPriceChange,
            label = stringResource(R.string.price_alerts_target_label),
            modifier = Modifier.fillMaxWidth(),
        )
        DirectionSelector(
            selected = direction,
            onDirectionSelected = onDirectionSelected,
            modifier = Modifier.fillMaxWidth(),
        )
        PrimaryButton(
            text = stringResource(R.string.price_alerts_add_button),
            onClick = onAddAlert,
            enabled = canAddAlert,
        )
    }
}

@Composable
private fun DirectionSelector(
    selected: Direction,
    onDirectionSelected: (Direction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val directions = Direction.entries
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        directions.forEachIndexed { index, direction ->
            SegmentedButton(
                selected = direction == selected,
                onClick = { onDirectionSelected(direction) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = directions.size),
                label = { Text(stringResource(direction.labelRes())) },
            )
        }
    }
}

private fun formatPrice(price: Double): String = formatPrice(price.toBigDecimal())

private fun formatPrice(price: BigDecimal): String =
    price.setScale(2, RoundingMode.HALF_UP).toPlainString()

@StringRes
private fun Direction.labelRes(): Int = when (this) {
    Direction.ABOVE -> R.string.price_alerts_direction_above
    Direction.BELOW -> R.string.price_alerts_direction_below
}

@StringRes
private fun Direction.targetRes(): Int = when (this) {
    Direction.ABOVE -> R.string.price_alerts_target_above
    Direction.BELOW -> R.string.price_alerts_target_below
}

@StringRes
private fun Direction.triggeredMessageRes(): Int = when (this) {
    Direction.ABOVE -> R.string.price_alerts_triggered_above
    Direction.BELOW -> R.string.price_alerts_triggered_below
}

private val previewTickers = listOf("TSLA", "AMZN", "MSFT", "AAPL", "LLOY")

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun PriceAlertsScreenPreview() {
    MaterialTheme {
        PriceAlertsScreen(
            state = PriceAlertsUiState(
                activeAlerts = listOf(
                    ActiveAlertItem(
                        alert = PriceAlert("TSLA", 250.0, Direction.ABOVE, triggered = false),
                        currentPrice = BigDecimal("243.17"),
                    ),
                    ActiveAlertItem(
                        alert = PriceAlert("AAPL", 180.0, Direction.BELOW, triggered = false),
                        currentPrice = BigDecimal("191.42"),
                    ),
                    ActiveAlertItem(
                        alert = PriceAlert("LLOY", 55.5, Direction.ABOVE, triggered = false),
                        currentPrice = null,
                    ),
                ),
                availableTickers = previewTickers,
                selectedTicker = "MSFT",
                targetPriceInput = "420",
                direction = Direction.BELOW,
                triggeredAlert = PriceAlert("AMZN", 200.0, Direction.ABOVE, triggered = true),
            ),
            onTickerSelected = {},
            onTargetPriceChange = {},
            onDirectionSelected = {},
            onAddAlert = {},
            onRemoveAlert = {},
            onDismissTriggeredBanner = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun PriceAlertsScreenEmptyPreview() {
    MaterialTheme {
        PriceAlertsScreen(
            state = PriceAlertsUiState(availableTickers = previewTickers),
            onTickerSelected = {},
            onTargetPriceChange = {},
            onDirectionSelected = {},
            onAddAlert = {},
            onRemoveAlert = {},
            onDismissTriggeredBanner = {},
        )
    }
}
