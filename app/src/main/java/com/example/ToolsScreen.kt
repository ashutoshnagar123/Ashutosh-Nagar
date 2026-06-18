package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen() {
    var projectDescription by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var generatedText by remember { mutableStateOf("") }
    
    val aiGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF1DE9B6), Color(0xFF24389C))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Proposal Writer", style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues).fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text("Configure your proposal parameters to generate AI-assisted pitches.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            item {
                // Form Card
                ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        
                        Column {
                            Text("Project Description", style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = projectDescription,
                                onValueChange = { projectDescription = it },
                                placeholder = { Text("Describe the client's needs and requirements...") },
                                modifier = Modifier.fillMaxWidth().height(120.dp)
                            )
                        }
                        
                        Column {
                            Text("Skills (Tags)", style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = true, 
                                    onClick = {}, 
                                    label = { Text("UI Design") }, 
                                    trailingIcon = { Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp)) }
                                )
                                FilterChip(
                                    selected = true, 
                                    onClick = {}, 
                                    label = { Text("Tailwind") }, 
                                    trailingIcon = { Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp)) }
                                )
                            }
                        }
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Budget ($)", style = MaterialTheme.typography.labelMedium)
                                Spacer(Modifier.height(8.dp))
                                OutlinedTextField(value = budget, onValueChange = { budget = it }, placeholder = { Text("0.00") })
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Experience Level", style = MaterialTheme.typography.labelMedium)
                                Spacer(Modifier.height(8.dp))
                                // Simulate dropdown with a basic text field
                                OutlinedTextField(
                                    value = "Expert", 
                                    onValueChange = { }, 
                                    readOnly = true, 
                                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                                )
                            }
                        }
                        
                        Button(
                            onClick = {
                                generatedText = "Dear Hiring Manager,\n\nI am writing to express my strong interest in the UI/UX Designer position you have posted. With a solid foundation in modern design principles and extensive experience utilizing Tailwind CSS, I am confident in my ability to deliver high-quality, responsive interfaces that meet your project's unique requirements.\n\nMy approach combines aesthetic refinement with functional clarity, ensuring that end-users enjoy an intuitive and engaging experience.\n\nThank you for your time and consideration."
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues()
                        ) {
                            Box(modifier = Modifier.fillMaxSize().background(aiGradient), contentAlignment = Alignment.Center) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                                    Text("Generate Proposal", color = Color.White, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }
            }
            
            if (generatedText.isNotEmpty()) {
                item {
                    ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Column {
                            ScrollableTabRow(selectedTabIndex = 0, edgePadding = 16.dp) {
                                Tab(selected = true, onClick = {}, text = { Text("Professional") })
                                Tab(selected = false, onClick = {}, text = { Text("Short") })
                                Tab(selected = false, onClick = {}, text = { Text("Detailed") })
                            }
                            
                            Text(generatedText, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                            
                            HorizontalDivider()
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                                OutlinedButton(onClick = {}) { 
                                    Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Copy") 
                                }
                                Spacer(Modifier.width(8.dp))
                                Button(onClick = {}) { 
                                    // Use standard arrow down as fallback for download
                                    Icon(Icons.Default.KeyboardArrowDown, null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Download PDF") 
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}
