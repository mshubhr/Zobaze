package com.app.expensetracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.expensetracker.ui.screens.ExpenseEntryScreen
import com.app.expensetracker.ui.screens.ExpenseListScreen
import com.app.expensetracker.ui.screens.ExpenseReportScreen
import com.app.expensetracker.viewmodel.MainViewModel

@Composable
fun AppNavGraph(vm: MainViewModel, modifier: Modifier) {
    val nav = rememberNavController()
    NavHost(nav, startDestination = "entry") {
        composable("entry") {
            ExpenseEntryScreen(vm, modifier) { nav.navigate("list") }
        }
        composable("list") {
            ExpenseListScreen(vm, modifier, { nav.popBackStack() }, { nav.navigate("report") })
        }
        composable("report") {
            ExpenseReportScreen(vm, modifier) { nav.popBackStack() }
        }
    }
}