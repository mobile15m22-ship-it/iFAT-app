package com.ifat.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ifat.app.data.TransactionDao
import com.ifat.app.data.TransactionEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(dao: TransactionDao) {
    val transactions by dao.getAll().collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    val totalIncome = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

    Scaffold(
        topBar = { TopAppBar(title = { Text("iFAT Personal Finance") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // কার্ড: মোট ব্যালেন্স
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Balance: ৳$balance", style = MaterialTheme.typography.titleLarge)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Income: ৳$totalIncome", color = Color(0xFF2E7D32))
                        Text("Expense: ৳$totalExpense", color = Color(0xFFC62828))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ইনপুট ফিল্ড
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("Note (e.g. Salary, Grocery)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val amt = amountText.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            coroutineScope.launch {
                                dao.insert(TransactionEntity(amount = amt, type = "INCOME", note = noteText))
                                amountText = ""
                                noteText = ""
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Add Income")
                }

                Button(
                    onClick = {
                        val amt = amountText.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            coroutineScope.launch {
                                dao.insert(TransactionEntity(amount = amt, type = "EXPENSE", note = noteText))
                                amountText = ""
                                noteText = ""
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Add Expense")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Recent Transactions:", style = MaterialTheme.typography.titleMedium)

            LazyColumn {
                items(transactions) { item ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if (item.note.isEmpty()) item.type else item.note)
                            Text(
                                text = "${if (item.type == "INCOME") "+" else "-"}৳${item.amount}",
                                color = if (item.type == "INCOME") Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                        }
                    }
                }
            }
        }
    }
}
