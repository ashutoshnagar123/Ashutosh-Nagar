package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.LoadState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel = hiltViewModel()) {
    var text by remember { mutableStateOf("") }
    val pagedMessages = viewModel.pagedMessages.collectAsLazyPagingItems()
    val isLoading by viewModel.isLoading.collectAsState()
    
    Scaffold(
        topBar = { 
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primary, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Face, contentDescription = "AI", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Freelance Assistant", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Online  •  AI Active", color = Color(0xFF10B981), fontSize = 12.sp)
                        }
                    }
                },
                actions = {
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.padding(end = 16.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    ) {
                        Icon(Icons.Default.Language, contentDescription = "Language", modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("English", fontSize = 12.sp)
                    }
                }
            ) 
        },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, shadowElevation = 4.dp) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding()) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ask your assistant anything... (e.g. 'Draft a proposal for...', 'Analyze my earnings...')", fontSize = 14.sp) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        minLines = 2,
                        maxLines = 5,
                        leadingIcon = {
                            IconButton(onClick = {}) { Icon(Icons.Default.AttachFile, "Attach") }
                        },
                        trailingIcon = {
                            Row {
                                IconButton(onClick = {}) { Icon(Icons.Default.Mic, "Voice") }
                                Button(
                                    onClick = { 
                                        if (text.isNotBlank()) {
                                            viewModel.sendMessage(text)
                                            text = ""
                                        }
                                    },
                                    modifier = Modifier.padding(end = 8.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    enabled = text.isNotBlank() && !isLoading
                                ) {
                                    Text("Send", fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.width(4.dp))
                                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "AI can make mistakes. Consider verifying important information.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues).fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Text(
                    "Today, 10:24 AM",
                    modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (pagedMessages.itemCount == 0) {
                 item {
                     Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                         Text("How can I help you today?", color = MaterialTheme.colorScheme.onSurfaceVariant)
                     }
                 }
            }
            items(pagedMessages.itemCount) { index ->
                val msg = pagedMessages[index]
                if (msg != null) {
                    val isUser = msg.isUser
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
                        if (!isUser) {
                            Box(modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Face, contentDescription = "AI", tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.width(8.dp))
                        }
                        
                        val bkg = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        val textCol = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        
                        Surface(
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = if (isUser) 16.dp else 4.dp, bottomEnd = if (isUser) 4.dp else 16.dp),
                            color = bkg,
                            shadowElevation = if (isUser) 0.dp else 2.dp,
                            modifier = Modifier.widthIn(max = 300.dp)
                        ) {
                            Text(
                                msg.text,
                                modifier = Modifier.padding(16.dp),
                                color = textCol,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
            
            pagedMessages.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item { CircularProgressIndicator(modifier = Modifier.padding(16.dp).fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)) }
                    }
                    loadState.append is LoadState.Loading -> {
                        item { CircularProgressIndicator(modifier = Modifier.padding(16.dp).fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)) }
                    }
                    loadState.refresh is LoadState.Error -> {
                        item { Text("Failed to load messages", color = MaterialTheme.colorScheme.error) }
                    }
                    loadState.append is LoadState.Error -> {
                        item { Text("Failed to load more messages", color = MaterialTheme.colorScheme.error) }
                    }
                }
            }

            if (isLoading) {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                        Box(modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Face, contentDescription = "AI", tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Surface(shape = RoundedCornerShape(16.dp), shadowElevation = 2.dp) {
                            Box(modifier = Modifier.padding(16.dp)) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}
