package io.freetrade.exampleapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import io.freetrade.exampleapplication.ui.PriceAlertsScreen
import io.freetrade.exampleapplication.ui.PriceAlertsViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: PriceAlertsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val state by viewModel.state.collectAsState()
                    PriceAlertsScreen(
                        state = state,
                        onTickerSelected = viewModel::onTickerSelected,
                        onTargetPriceChange = viewModel::onTargetPriceChange,
                        onDirectionSelected = viewModel::onDirectionSelected,
                        onAddAlert = viewModel::onAddAlert,
                        onRemoveAlert = viewModel::onRemoveAlert,
                        onDismissTriggeredBanner = viewModel::onDismissTriggeredBanner,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
