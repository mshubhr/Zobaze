package com.app.expensetracker.ui.screens

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.widget.DatePicker
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.expensetracker.data.model.Expense
import com.app.expensetracker.data.model.ExpenseUiState
import com.app.expensetracker.ui.components.TopBarWithThemeToggleContent
import com.app.expensetracker.ui.theme.ExpenseTrackerTheme
import com.app.expensetracker.viewmodel.MainViewModel
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ExpenseListScreen(
    vm: MainViewModel, modifier: Modifier, onBack: () -> Unit, onNavigateToReport: () -> Unit
) {
    val state by vm.uiState.collectAsState()
    val darkTheme by vm.darkTheme.collectAsState()

    ExpenseListScreenContent(
        uiState = state,
        darkTheme = darkTheme,
        onSyncNow = { vm.syncNow() },
        onToggleTheme = { vm.toggleTheme() },
        modifier = modifier,
        onBack = onBack,
        onNavigateToReport = onNavigateToReport
    )
}

@Composable
fun ExpenseListScreenContent(
    uiState: ExpenseUiState,
    darkTheme: Boolean,
    onSyncNow: () -> Unit,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onNavigateToReport: () -> Unit
) {
    val context = LocalContext.current

    var selectedDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var groupByCategory by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val filterFormatter = remember { SimpleDateFormat("yyyyMMdd", Locale.getDefault()) }

    val expenses = remember(uiState.expenses, selectedDate) {
        val selectedKey = filterFormatter.format(Date(selectedDate))
        uiState.expenses.filter {
            filterFormatter.format(Date(it.timestamp)) == selectedKey
        }
    }

    Scaffold(topBar = {
        TopBarWithThemeToggleContent(
            darkTheme = darkTheme, onToggleTheme = onToggleTheme, modifier = modifier
        )
    }, bottomBar = {
        Surface(tonalElevation = 8.dp) {
            Column(
                modifier
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onBack,
                        modifier = modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Back")
                    }

                    Button(
                        onClick = onNavigateToReport,
                        modifier = modifier.weight(1.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.PieChart,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("View Report")
                    }
                }
                Spacer(modifier.height(12.dp))
                Button(
                    onClick = onSyncNow,
                    modifier = modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(
                        Icons.Default.Sync,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Sync All Expenses")
                }
            }
        }
    }) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Selected Date",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                text = dateFormatter.format(Date(selectedDate)),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        IconButton(
                            onClick = {
                                DatePickerDialog(
                                    context,
                                    { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                                        calendar.set(year, month, dayOfMonth)
                                        selectedDate = calendar.timeInMillis
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }, modifier = Modifier.background(
                                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f),
                                CircleShape
                            )
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    if (expenses.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                alpha = 0.1f
                            )
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "₹${"%.2f".format(expenses.sumOf { it.amount })}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(Modifier.width(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${expenses.size} Items",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterFormatter.format(Date(selectedDate)) == filterFormatter.format(
                    Date()
                ),
                    onClick = { selectedDate = System.currentTimeMillis() },
                    label = { Text("Today") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Today, null, modifier = Modifier.size(16.dp)
                        )
                    })
                FilterChip(
                    selected = groupByCategory,
                    onClick = { groupByCategory = !groupByCategory },
                    label = { Text(if (groupByCategory) "Grouped by Category" else "Group by Category") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.History, null, modifier = Modifier.size(16.dp)
                        )
                    })
            }

            Spacer(Modifier.height(8.dp))

            if (expenses.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No expenses for this date",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp, top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (groupByCategory) {
                        val grouped = expenses.groupBy { it.category }
                        grouped.forEach { (cat, list) ->
                            item {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                                )
                            }
                            items(list) { e ->
                                ExpenseCard(e)
                            }
                        }
                    } else {
                        items(expenses.sortedByDescending { it.timestamp }) { e ->
                            ExpenseCard(e)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseCard(e: Expense) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        e.category.take(1).uppercase(),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        e.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        e.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    if (!e.notes.isNullOrBlank()) {
                        Text(
                            e.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            maxLines = 1
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "₹${"%.2f".format(e.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!e.isSynced) {
                        Icon(
                            Icons.Default.CloudSync,
                            contentDescription = "Pending Sync",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                    Text(
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(e.timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseListScreenPreview() {
    val sampleExpenses = listOf(
        Expense(
            title = "Lunch",
            amount = 150.0,
            category = "Food",
            timestamp = System.currentTimeMillis()
        ), Expense(
            title = "Taxi Ride to Office",
            amount = 200.0,
            category = "Transport",
            timestamp = System.currentTimeMillis() - 3600000,
            isSynced = true,
            notes = "Uber"
        ), Expense(
            title = "Monthly Groceries",
            amount = 5400.0,
            category = "Shopping",
            timestamp = System.currentTimeMillis() - 7200000
        )
    )
    ExpenseTrackerTheme {
        ExpenseListScreenContent(
            uiState = ExpenseUiState(expenses = sampleExpenses),
            darkTheme = false,
            onSyncNow = {},
            onToggleTheme = {},
            onBack = {},
            onNavigateToReport = {})
    }
}