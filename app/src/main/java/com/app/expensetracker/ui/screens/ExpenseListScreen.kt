package com.app.expensetracker.ui.screens

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.app.expensetracker.data.model.Expense
import com.app.expensetracker.ui.components.TopBarWithThemeToggle
import com.app.expensetracker.viewmodel.MainViewModel
import java.util.*

@Composable
fun ExpenseListScreen(
    vm: MainViewModel,
    onBack: () -> Unit,
    onNavigateToReport: () -> Unit
) {
    val state by vm.uiState.collectAsState()
    val context = LocalContext.current

    var selectedDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var groupByCategory by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val filterFormatter = remember { SimpleDateFormat("yyyyMMdd", Locale.getDefault()) }

    val expenses = remember(state.expenses, selectedDate) {
        val selectedKey = filterFormatter.format(Date(selectedDate))
        state.expenses.filter {
            filterFormatter.format(Date(it.timestamp)) == selectedKey
        }
    }

    Scaffold(
        topBar = { TopBarWithThemeToggle(vm) },
        bottomBar = {
            Column(Modifier.padding(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = onBack, modifier = Modifier.weight(1f)) {
                        Text("Back")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = onNavigateToReport, modifier = Modifier.weight(1f)) {
                        Text("Go to Report")
                    }
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { vm.syncNow() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sync")
                }
            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Expenses for ${dateFormatter.format(Date(selectedDate))}",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(8.dp))

            // Controls
            Row {
                Button(onClick = {
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
                }) { Text("Pick Date") }

                Spacer(Modifier.width(8.dp))

                Button(onClick = { selectedDate = System.currentTimeMillis() }) {
                    Text("Today")
                }

                Spacer(Modifier.width(8.dp))

                Button(onClick = { groupByCategory = !groupByCategory }) {
                    Text(if (groupByCategory) "Group by Time" else "Group by Category")
                }
            }

            Spacer(Modifier.height(16.dp))

            // Totals
            if (expenses.isNotEmpty()) {
                Text(
                    text = "Total: ${expenses.size} • ₹${"%.2f".format(expenses.sumOf { it.amount })}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(12.dp))
            }

            // List / Empty state
            if (expenses.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No expenses for this date")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp) // leave space for bottom bar
                ) {
                    if (groupByCategory) {
                        val grouped = expenses.groupBy { it.category }
                        grouped.forEach { (cat, list) ->
                            item {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                                )
                            }
                            items(list) { e ->
                                ExpenseCard(e)
                                if (!e.isSynced) {
                                    Text(
                                        "Pending",
                                        color = Color.Red,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        items(expenses.sortedBy { it.timestamp }) { e ->
                            ExpenseCard(e)
                            if (!e.isSynced) {
                                Text(
                                    "Pending",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(e.title, style = MaterialTheme.typography.titleMedium)
                Text("₹${e.amount}")
                Text(e.category, style = MaterialTheme.typography.bodyMedium)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(e.timestamp)))
                e.notes?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
            }
        }
    }
}