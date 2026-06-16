package com.app.expensetracker.ui.screens

import android.content.Intent
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.expensetracker.data.model.DailyPoint
import com.app.expensetracker.data.model.Expense
import com.app.expensetracker.data.model.ExpenseUiState
import com.app.expensetracker.ui.components.TopBarWithThemeToggleContent
import com.app.expensetracker.ui.ExpenseTrackerTheme
import com.app.expensetracker.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun ExpenseReportScreen(vm: MainViewModel, modifier: Modifier, onBack: () -> Unit) {
    val state by vm.uiState.collectAsState()
    val darkTheme by vm.darkTheme.collectAsState()

    ExpenseReportScreenContent(
        state = state,
        darkTheme = darkTheme,
        onToggleTheme = { vm.toggleTheme() },
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun ExpenseReportScreenContent(
    state: ExpenseUiState,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val last7 by remember(state.expenses) {
        derivedStateOf {
            val keyFmt = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

            (6 downTo 0).map { delta ->
                val dayMillis = System.currentTimeMillis() - delta * (24L * 60 * 60 * 1000)
                DailyPoint(
                    label = SimpleDateFormat("dd MMM", Locale.getDefault()).format(
                        Date(
                            dayMillis
                        )
                    ),
                    value = state.expenses.groupBy { keyFmt.format(Date(it.timestamp)) }
                        .mapValues { (_, list) -> list.sumOf { e -> e.amount } }[keyFmt.format(
                        Date(
                            dayMillis
                        )
                    )] ?: 0.0
                )
            }
        }
    }

    val totalAmount = remember(state.expenses) { state.expenses.sumOf { it.amount } }

    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        TopBarWithThemeToggleContent(
            darkTheme = darkTheme,
            onToggleTheme = onToggleTheme,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        "Expense Report",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            "Total Spending (Last 7 Days)",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            "₹${"%.2f".format(totalAmount)}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${state.expenses.size} transactions recorded",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            if (last7.all { it.value == 0.0 }) {
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No Data Available", style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(8.dp))
                            Text("Your weekly summary will appear here.")
                            Spacer(Modifier.height(24.dp))
                            Button(onClick = onBack) { Text("Return Home") }
                        }
                    }
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                alpha = 0.3f
                            )
                        )
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "Spending Trends",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(20.dp))

                            val density = androidx.compose.ui.platform.LocalDensity.current
                            val bottomPadding = with(density) { 28.dp.toPx() }

                            val primaryColor = MaterialTheme.colorScheme.primary
                            val onSurfaceColor = MaterialTheme.colorScheme.onSurface
                            val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            val axisColor = MaterialTheme.colorScheme.outlineVariant

                            val valuePaint = remember(onSurfaceColor) {
                                Paint().apply {
                                    isAntiAlias = true
                                    textAlign = Paint.Align.CENTER
                                    color = onSurfaceColor.toArgb()
                                }
                            }.also { it.textSize = with(density) { 10.sp.toPx() } }

                            val xLabelPaint = remember(labelColor) {
                                Paint().apply {
                                    isAntiAlias = true
                                    textAlign = Paint.Align.CENTER
                                    color = labelColor.toArgb()
                                }
                            }.also { it.textSize = with(density) { 10.sp.toPx() } }

                            Canvas(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            ) {
                                val chartWidth = size.width
                                val chartHeight = size.height
                                val slotWidth = chartWidth / last7.size

                                drawLine(
                                    color = axisColor,
                                    start = Offset(0f, chartHeight - bottomPadding),
                                    end = Offset(chartWidth, chartHeight - bottomPadding),
                                    strokeWidth = 1.dp.toPx()
                                )

                                last7.forEachIndexed { i, p ->
                                    val barHeight = ((p.value / max(
                                        1.0,
                                        last7.maxOfOrNull { it.value }
                                            ?: 1.0)).toFloat() * (chartHeight - with(density) { 24.dp.toPx() } - bottomPadding)).coerceAtLeast(
                                        4.dp.toPx()
                                    )
                                    val barWidth = slotWidth * 0.45f
                                    val top = chartHeight - bottomPadding - barHeight

                                    drawRoundRect(
                                        color = primaryColor,
                                        topLeft = Offset(
                                            i * slotWidth + (slotWidth - barWidth) / 2, top
                                        ),
                                        size = Size(barWidth, barHeight),
                                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                    )

                                    if (p.value > 0) {
                                        drawIntoCanvas { canvas ->
                                            canvas.nativeCanvas.drawText(
                                                "₹${p.value.roundToInt()}",
                                                i * slotWidth + slotWidth / 2f,
                                                top - 10f,
                                                valuePaint
                                            )
                                        }
                                    }

                                    drawIntoCanvas { canvas ->
                                        canvas.nativeCanvas.drawText(
                                            p.label,
                                            i * slotWidth + slotWidth / 2f,
                                            chartHeight - 6f,
                                            xLabelPaint
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Column {
                        Text(
                            "Category Analysis",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))
                        val categoryTotals = remember(state.expenses) {
                            state.expenses.groupBy { it.category }
                                .mapValues { entry -> entry.value.sumOf { it.amount } }.toList()
                                .sortedByDescending { it.second }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column {
                                categoryTotals.forEachIndexed { index, (cat, amt) ->
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                Modifier
                                                    .size(10.dp)
                                                    .background(
                                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                                        CircleShape
                                                    )
                                            )
                                            Spacer(Modifier.width(12.dp))
                                            Text(cat, style = MaterialTheme.typography.bodyLarge)
                                        }
                                        Text(
                                            "₹${amt.toInt()}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    if (index < categoryTotals.size - 1) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(
                                                alpha = 0.3f
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val categoryTotalsMap = state.expenses.groupBy { it.category }
                                    .mapValues { it.value.sumOf { e -> e.amount } }
                                val share = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "Expense Report")
                                    putExtra(Intent.EXTRA_TEXT, buildString {
                                        appendLine("--- WEEKLY EXPENSE REPORT ---")
                                        appendLine("Total: ₹${"%.2f".format(totalAmount)}")
                                        appendLine()
                                        appendLine("Daily Breakdown:")
                                        last7.forEach { appendLine("${it.label}: ₹${"%.2f".format(it.value)}") }
                                        appendLine()
                                        appendLine("Category Analysis:")
                                        categoryTotalsMap.forEach { (cat, amt) ->
                                            appendLine("$cat: ₹${"%.2f".format(amt)}")
                                        }
                                    })
                                }
                                context.startActivity(Intent.createChooser(share, "Share Report"))
                            }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Share")
                        }

                        Button(
                            onClick = onBack,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Done")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseReportScreenPreview() {
    ExpenseTrackerTheme {
        ExpenseReportScreenContent(
            state = ExpenseUiState(
                expenses = listOf(
                    Expense(
                        title = "Lunch",
                        amount = 150.0,
                        category = "Food",
                        timestamp = System.currentTimeMillis()
                    ), Expense(
                        title = "Petrol",
                        amount = 500.0,
                        category = "Transport",
                        timestamp = System.currentTimeMillis() - 86400000
                    ), Expense(
                        title = "Groceries",
                        amount = 1200.0,
                        category = "Food",
                        timestamp = System.currentTimeMillis() - 86400000 * 2
                    ), Expense(
                        title = "Internet",
                        amount = 999.0,
                        category = "Bills",
                        timestamp = System.currentTimeMillis() - 86400000 * 3
                    ), Expense(
                        title = "Gym",
                        amount = 1500.0,
                        category = "Health",
                        timestamp = System.currentTimeMillis() - 86400000 * 4
                    ), Expense(
                        title = "Coffee",
                        amount = 50.0,
                        category = "Food",
                        timestamp = System.currentTimeMillis() - 86400000 * 5
                    ), Expense(
                        title = "Movie",
                        amount = 300.0,
                        category = "Entertainment",
                        timestamp = System.currentTimeMillis() - 86400000 * 6
                    )
                )
            ), darkTheme = false, onToggleTheme = {}, onBack = {})
    }
}