package com.example

import android.content.Intent
import androidx.core.content.FileProvider
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceScreen(viewModel: InvoiceViewModel = hiltViewModel()) {
    var clientName by remember { mutableStateOf("") }
    var serviceDescription by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var hasAttemptedSave by remember { mutableStateOf(false) }
    
    val currentAmount = price.toDoubleOrNull() ?: 0.0
    val isClientValid = clientName.isNotBlank()
    val isServiceValid = serviceDescription.isNotBlank()
    val isPriceValid = currentAmount > 0
    val isValid = isClientValid && isServiceValid && isPriceValid

    val taxRate = 0.05 // Example 5% tax
    val totalAmount = currentAmount + (currentAmount * taxRate)
    
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        topBar = { TopAppBar(title = { Text("Invoice Builder") }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExtendedFloatingActionButton(
                    onClick = {
                        hasAttemptedSave = true
                        if (isValid) {
                            viewModel.saveInvoice(clientName, serviceDescription, totalAmount)
                            scope.launch {
                                snackbarHostState.showSnackbar("Invoice Saved successfully")
                                clientName = ""
                                serviceDescription = ""
                                price = ""
                                hasAttemptedSave = false
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Please fill in all fields correctly")
                            }
                        }
                    },
                    icon = { Icon(Icons.Default.Save, contentDescription = "Save") },
                    text = { Text("Save Invoice") }
                )
                ExtendedFloatingActionButton(
                    onClick = {
                        hasAttemptedSave = true
                        if (isValid) {
                            val result = InvoiceExporter.exportToPdf(
                                context = context,
                                clientName = clientName,
                                serviceDescription = serviceDescription,
                                subtotal = currentAmount,
                                taxAmount = currentAmount * taxRate,
                                total = totalAmount
                            )
                            scope.launch {
                                if (result.isSuccess) {
                                    val file = result.getOrThrow()
                                    snackbarHostState.showSnackbar("PDF Exported: ${file.name}")
                                    
                                    try {
                                        val uri = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.provider",
                                            file
                                        )
                                        
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "application/pdf"
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        val chooser = Intent.createChooser(intent, "Share or Open Invoice")
                                        context.startActivity(chooser)
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar("Could not open PDF: ${e.message}")
                                    }
                                    
                                } else {
                                    snackbarHostState.showSnackbar("Export Failed: ${result.exceptionOrNull()?.message}")
                                }
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Please complete the invoice fields to export")
                            }
                        }
                    },
                    icon = { Icon(Icons.Default.Share, contentDescription = "Share") },
                    text = { Text("Export & Share PDF") },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Client Details", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("Client Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = hasAttemptedSave && !isClientValid,
                    supportingText = { if (hasAttemptedSave && !isClientValid) Text("Client name is required") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Services", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = serviceDescription,
                    onValueChange = { serviceDescription = it },
                    label = { Text("Service Description") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = hasAttemptedSave && !isServiceValid,
                    supportingText = { if (hasAttemptedSave && !isServiceValid) Text("Description is required") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Amount ($)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = hasAttemptedSave && !isPriceValid,
                    supportingText = { if (hasAttemptedSave && !isPriceValid) Text("Invalid amount") }
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal:")
                            Text("$${String.format("%.2f", currentAmount)}")
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Estimated Tax (5%):")
                            Text("$${String.format("%.2f", currentAmount * taxRate)}")
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total:", style = MaterialTheme.typography.titleLarge)
                            Text("$${String.format("%.2f", totalAmount)}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
