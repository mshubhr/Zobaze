package com.app.expensetracker.ui.screens

import android.content.Intent
import android.graphics.Paint
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.expensetracker.data.model.Expense
import com.app.expensetracker.ui.components.TopBarWithThemeToggle
import com.app.expensetracker.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

data class DailyPoint(val label: String, val value: Double)

@Composable
fun ExpenseReportScreen(vm: MainViewModel, onBack: () -> Unit) {
    val state by vm.uiState.collectAsState()
    val context = LocalContext.current

    // --- Aggregate last 7 days from whatever is in state.expenses ---
    val last7: List<DailyPoint> = remember(state.expenses) {
        computeLast7DailyTotals(state.expenses)
    }

    val totalCount = state.expenses.size
    val totalAmount = state.expenses.sumOf { it.amount }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        TopBarWithThemeToggle(vm)

        Text("Expense Report (Last 7 days)", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        // Totals row
        Text(
            text = "Total: $totalCount • ₹${"%.2f".format(totalAmount)}",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(12.dp))

        if (last7.all { it.value == 0.0 }) {
            // Empty state
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No expenses yet!", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Text("Add expenses to see your 7-day report.")
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onBack) { Text("Back") }
                }
            }
            return
        }

        // Chart with value labels + x-axis labels
        BarChartWithLabels(points = last7)

        Spacer(Modifier.height(20.dp))

        // Daily totals list (readable numbers under the chart)
        Text("Daily totals", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(last7) { p ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(p.label)
                    Text("₹${"%.2f".format(p.value)}")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Category-wise totals (from current list)
        Text("Category totals", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        val categoryTotals = remember(state.expenses) {
            state.expenses.groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }
                .toList()
                .sortedByDescending { it.second }
        }
        if (categoryTotals.isEmpty()) {
            Text("No categories yet")
        } else {
            categoryTotals.forEach { (cat, amt) ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(cat)
                    Text("₹${"%.2f".format(amt)}")
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Export CSV (simulation via share intent) with daily & category sections
        Button(
            onClick = {
                val csv = buildString {
                    appendLine("Section,Label,Amount")
                    appendLine("Daily Totals,,")
                    last7.forEach { appendLine(",${it.label},${"%.2f".format(it.value)}") }
                    appendLine()
                    appendLine("Category Totals,,")
                    categoryTotals.forEach { (cat, amt) ->
                        appendLine(",$cat,${"%.2f".format(amt)}")
                    }
                }
                val share = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Expense Report (CSV)")
                    putExtra(Intent.EXTRA_TEXT, csv)
                }
                context.startActivity(Intent.createChooser(share, "Export Report"))
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Export CSV") }

        Spacer(Modifier.height(8.dp))

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}

private fun computeLast7DailyTotals(expenses: List<Expense>): List<DailyPoint> {
    val now = System.currentTimeMillis()
    val dayMs = 24L * 60 * 60 * 1000
    val keyFmt = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    val labelFmt = SimpleDateFormat("dd MMM", Locale.getDefault())

    // Pre-aggregate by key
    val totalsByKey = expenses.groupBy { keyFmt.format(Date(it.timestamp)) }
        .mapValues { (_, list) -> list.sumOf { e -> e.amount } }

    // Oldest to newest over last 7 days
    return (6 downTo 0).map { delta ->
        val dayMillis = now - delta * dayMs
        val key = keyFmt.format(Date(dayMillis))
        val label = labelFmt.format(Date(dayMillis))
        DailyPoint(label = label, value = totalsByKey[key] ?: 0.0)
    }
}

/** Bar chart with value labels above bars and date labels on the X-axis. */
@Composable
fun BarChartWithLabels(points: List<DailyPoint>, modifier: Modifier = Modifier) {
    val density = androidx.compose.ui.platform.LocalDensity.current
    val valueTextSizePx = with(density) { 11.sp.toPx() }
    val xLabelTextSizePx = with(density) { 10.sp.toPx() }
    val topPadding = with(density) { 12.dp.toPx() }      // space for value labels
    val bottomPadding = with(density) { 18.dp.toPx() }   // space for x-axis labels

    // Paints for text
    val valuePaint = remember {
        Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
    }.also { it.textSize = valueTextSizePx }

    val xLabelPaint = remember {
        Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            color = android.graphics.Color.DKGRAY
        }
    }.also { it.textSize = xLabelTextSizePx }

    val barColor = Color(0xFF4CAF50)
    val axisColor = Color(0xFFBDBDBD)

    val maxVal = max(1.0, points.maxOfOrNull { it.value } ?: 1.0)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp) // a bit taller to accommodate labels
    ) {
        val chartWidth = size.width
        val chartHeight = size.height
        val availableHeight = chartHeight - topPadding - bottomPadding
        val barCount = points.size
        val slotWidth = chartWidth / barCount

        // X-axis
        drawLine(
            color = axisColor,
            start = Offset(0f, chartHeight - bottomPadding),
            end = Offset(chartWidth, chartHeight - bottomPadding),
            strokeWidth = 2f
        )

        points.forEachIndexed { i, p ->
            val barHeight = ((p.value / maxVal).toFloat() * availableHeight).coerceAtLeast(0f)
            val left = i * slotWidth + slotWidth * 0.18f
            val right = i * slotWidth + slotWidth * 0.82f
            val top = chartHeight - bottomPadding - barHeight
            val bottom = chartHeight - bottomPadding

            // Bar
            drawRect(
                color = barColor,
                topLeft = Offset(left, top),
                size = Size(max(4f, right - left), barHeight)
            )

            // Value label above bar
            val valueLabel = if (p.value < 1) "0" else "₹${p.value.roundToInt()}"
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    valueLabel,
                    (left + right) / 2f,
                    (top - 6f).coerceAtLeast(valueTextSizePx), // avoid clipping at very top
                    valuePaint
                )
            }

            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    p.label, // e.g., "21 Aug"
                    (left + right) / 2f,
                    chartHeight - 4f,
                    xLabelPaint
                )
            }
        }
    }
}