package com.example

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Supercharge Your Business", style = MaterialTheme.typography.headlineMedium) }) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues).fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text("Choose the plan that fits your workflow. Scale your capabilities with AI-driven insights.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("FREE", style = MaterialTheme.typography.labelMedium)
                        Row {
                            Text("$0", style = MaterialTheme.typography.headlineLarge)
                            Text("/mo", modifier = Modifier.padding(top = 16.dp), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("For getting started.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                        PlanFeature("10 AI generations / day")
                        PlanFeature("Basic project tracking")
                        PlanFeature("Community support")
                        Spacer(modifier = Modifier.height(24.dp))
                        OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                            Text("Current Plan")
                        }
                    }
                }
            }
            
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth().border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)), 
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("PRO", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        Row {
                            Text("$19", style = MaterialTheme.typography.headlineLarge)
                            Text("/mo", modifier = Modifier.padding(top = 16.dp), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("For active freelancers.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                        PlanFeature("Unlimited AI generations")
                        PlanFeature("PDF exports & templates")
                        PlanFeature("Smart proposal drafting")
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                            Text("Upgrade to Pro")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun PlanFeature(text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp).padding(top = 2.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}
