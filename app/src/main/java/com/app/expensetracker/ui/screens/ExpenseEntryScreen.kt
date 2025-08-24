package com.app.expensetracker.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.expensetracker.ui.components.DropdownMenuWithItems
import com.app.expensetracker.ui.components.TopBarWithThemeToggle
import com.app.expensetracker.viewmodel.MainViewModel

@Composable
fun ExpenseEntryScreen(vm: MainViewModel, onNavigateToList: () -> Unit) {
    val context = LocalContext.current
    val state by vm.uiState.collectAsState()
    val title = remember { mutableStateOf("") }
    val amount = remember { mutableStateOf("") }
    val notes = remember { mutableStateOf("") }
    val category = remember { mutableStateOf("Staff") }
    val showAnim = remember { mutableStateOf(false) }

    val receiptImage = remember { mutableStateOf<Uri?>(null) }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        receiptImage.value = uri
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//        TopBarWithThemeToggle(vm)

        Text("Total Spent Today: ₹${"%.2f".format(state.totalSpentToday)}", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = title.value, onValueChange = { title.value = it }, label = { Text("Title") })
        OutlinedTextField(value = amount.value, onValueChange = { amount.value = it }, label = { Text("Amount (₹)") }, keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
        )
        DropdownMenuWithItems(
            items = listOf("Staff","Travel","Food","Utility"),
            selected = category.value,
            onSelected = { category.value = it }
        )
        OutlinedTextField(value = notes.value, onValueChange = { if (it.length <= 100) notes.value = it }, label = { Text("Notes (optional)") })

        Spacer(Modifier.height(12.dp))

        if (receiptImage.value == null) {
            OutlinedButton(onClick = { imagePicker.launch("image/*") }) {
                Text("Upload Receipt (Optional)")
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = receiptImage.value,
                    contentDescription = "Receipt Image",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                TextButton(onClick = { receiptImage.value = null }) {
                    Text("Remove Receipt")
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            vm.onAddExpense(title.value, amount.value, category.value, notes.value.ifBlank { null }, null)
            showAnim.value = true
            title.value = ""
            amount.value = ""
            notes.value = ""
        }) {
            Text("Submit")
        }

        AnimatedVisibility(visible = state.lastAdded != null && showAnim.value, enter = fadeIn(), exit = fadeOut()) {
            Text("Added: ${state.lastAdded?.title}", modifier = Modifier.padding(top = 8.dp))
        }

        state.successMessage?.let {
            LaunchedEffect(it) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                vm.clearMessages()
                showAnim.value = false
            }
        }
        state.error?.let { LaunchedEffect(it) { Toast.makeText(context, it, Toast.LENGTH_SHORT).show(); vm.clearMessages() } }

        Spacer(Modifier.weight(1f))
        OutlinedButton(onClick = onNavigateToList) { Text("View expenses") }
    }
}