package com.app.expensetracker.data.model

data class ExpenseUiState(
    val expenses: List<Expense> = emptyList(),
    val totalSpentToday: Double = 0.0,
    val lastAdded: Expense? = null,
    val error: String? = null,
    val successMessage: String? = null
)