package io.freetrade.exampleapplication.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.freetrade.exampleapplication.R

@Composable
fun ListRow(
    title: String,
    subtitle: String,
    value: String,
    valueCaption: String,
    actionIcon: Painter,
    actionContentDescription: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = valueCaption,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = onActionClick) {
            Icon(
                painter = actionIcon,
                contentDescription = actionContentDescription,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ListRowPreview() {
    MaterialTheme {
        ListRow(
            title = "TSLA",
            subtitle = "Above 250.00",
            value = "243.17",
            valueCaption = "Last price",
            actionIcon = painterResource(R.drawable.ic_delete),
            actionContentDescription = "Remove TSLA alert",
            onActionClick = {},
        )
    }
}
