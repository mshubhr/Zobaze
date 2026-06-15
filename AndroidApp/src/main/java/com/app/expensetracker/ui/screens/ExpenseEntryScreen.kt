package com.app.expensetracker.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.expensetracker.data.model.ExpenseUiState
import com.app.expensetracker.ui.components.TopBarWithThemeToggleContent
import com.app.expensetracker.ui.theme.ExpenseTrackerTheme
import com.app.expensetracker.viewmodel.MainViewModel

@Composable
fun ExpenseEntryScreen(vm: MainViewModel, modifier: Modifier, onNavigateToList: () -> Unit) {
    val state by vm.uiState.collectAsState()
    val darkTheme by vm.darkTheme.collectAsState()
    ExpenseEntryContent(
        state = state,
        darkTheme = darkTheme,
        onToggleTheme = { vm.toggleTheme() },
        onAddExpense = vm::onAddExpense,
        clearMessages = vm::clearMessages,
        modifier = modifier,
        onNavigateToList = onNavigateToList
    )
}

@Composable
fun ExpenseEntryContent(
    state: ExpenseUiState,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onAddExpense: (String, String, String, String?, String?) -> Unit,
    clearMessages: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToList: () -> Unit
) {
    val context = LocalContext.current
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

    Scaffold(topBar = {
        TopBarWithThemeToggleContent(
            darkTheme = darkTheme, onToggleTheme = onToggleTheme, modifier = modifier
        )
    }, bottomBar = {
        Surface(tonalElevation = 8.dp) {
            Button(
                onClick = onNavigateToList,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.List, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("View Expenses History")
            }
        }
    }) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "Spent Today",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        "₹${"%.2f".format(state.totalSpentToday)}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Add New Expense",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    StyledTextField(
                        value = title.value,
                        onValueChange = { title.value = it },
                        label = "What did you buy?",
                        icon = Icons.Default.Title
                    )

                    StyledTextField(
                        value = amount.value,
                        onValueChange = { amount.value = it },
                        label = "How much? (₹)",
                        icon = Icons.Default.AttachMoney,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Category,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Category",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        DropdownMenuWithItems(
                            items = listOf("Staff", "Travel", "Food", "Utility"),
                            selected = category.value,
                            onSelected = { category.value = it })
                    }

                    StyledTextField(
                        value = notes.value,
                        onValueChange = { if (it.length <= 100) notes.value = it },
                        label = "Notes (optional)",
                        icon = Icons.Default.Description
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(8.dp)
                    ) {
                        if (receiptImage.value == null) {
                            TextButton(
                                onClick = { imagePicker.launch("image/*") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Attach Receipt Image")
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = receiptImage.value,
                                        contentDescription = "Receipt Image",
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        "Receipt Attached",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                IconButton(onClick = { receiptImage.value = null }) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Remove",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            onAddExpense(
                                title.value,
                                amount.value,
                                category.value,
                                notes.value.ifBlank { null },
                                null
                            )
                            showAnim.value = true
                            title.value = ""
                            amount.value = ""
                            notes.value = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Save Expense", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            AnimatedVisibility(
                visible = state.lastAdded != null && showAnim.value,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)),
                    border = CardDefaults.outlinedCardBorder()
                        .copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF4CAF50))),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Successfully added: ${state.lastAdded?.title}",
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            state.successMessage?.let {
                LaunchedEffect(it) {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    clearMessages()
                    showAnim.value = false
                }
            }
            state.error?.let {
                LaunchedEffect(it) {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show(); clearMessages()
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            focusedLabelColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun DropdownMenuWithItems(
    items: List<String>, selected: String, onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()
        ) {
            Text(selected)
        }

        DropdownMenu(
            expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { item ->
                DropdownMenuItem(text = { Text(item) }, onClick = {
                    onSelected(item)
                    expanded = false
                })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseEntryScreenPreview() {
    ExpenseTrackerTheme {
        ExpenseEntryContent(
            state = ExpenseUiState(totalSpentToday = 1250.0),
            darkTheme = false,
            onToggleTheme = {},
            onAddExpense = { _, _, _, _, _ -> },
            clearMessages = {},
            onNavigateToList = {})
    }
}