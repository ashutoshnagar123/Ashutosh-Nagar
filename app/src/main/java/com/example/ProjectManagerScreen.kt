package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.filled.AutoAwesome
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
import com.example.data.ProjectEntity
import kotlinx.coroutines.launch

import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.LoadState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectManagerScreen(viewModel: ProjectViewModel = hiltViewModel()) {
    val pagedProjects = viewModel.pagedProjects.collectAsLazyPagingItems()
    var showAddDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("Projects", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            ) 
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 300.dp),
            modifier = Modifier.padding(paddingValues).fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Text("Active Projects", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Manage your ongoing client work and track tasks.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(16.dp))
                    
                    TextButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "New Task", modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("New Project")
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
            
            if (pagedProjects.itemCount == 0) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No projects yet. Click + to create one.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(pagedProjects.itemCount) { index ->
                    val project = pagedProjects[index]
                    if (project != null) {
                        ProjectCard(project) { 
                            viewModel.deleteProject(project)
                            scope.launch {
                                snackbarHostState.showSnackbar("Project Deleted")
                            }
                        }
                    }
                }
            }
            
            pagedProjects.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item(span = { GridItemSpan(maxLineSpan) }) { CircularProgressIndicator(modifier = Modifier.padding(16.dp).fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)) }
                    }
                    loadState.append is LoadState.Loading -> {
                        item(span = { GridItemSpan(maxLineSpan) }) { CircularProgressIndicator(modifier = Modifier.padding(16.dp).fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)) }
                    }
                }
            }
        }
        
        if (showAddDialog) {
            AddProjectDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { title, client, deadline ->
                    viewModel.addProject(title, deadline, client)
                    showAddDialog = false
                    scope.launch {
                        snackbarHostState.showSnackbar("Project Added successfully")
                    }
                }
            )
        }
    }
}

@Composable
fun ProjectCard(project: ProjectEntity, onDelete: () -> Unit) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Surface(color = if (project.status == "Pending") MaterialTheme.colorScheme.surfaceVariant else Color(0xFF6EE7B7), shape = RoundedCornerShape(16.dp)) {
                    Text(project.status.uppercase(), modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (project.status == "Pending") MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF064E3B))
                }
                IconButton(onClick = onDelete) { Icon(Icons.Default.CheckCircle, "Complete/Delete") }
            }
            
            Spacer(Modifier.height(8.dp))
            Text(project.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            if (project.client != null) {
                Text(project.client, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Progress", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${(project.progress * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { project.progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
            )
            
            Spacer(Modifier.height(16.dp))
            Text("Deadline: ${project.deadline}", fontSize = 12.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProjectDialog(onDismiss: () -> Unit, onAdd: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var client by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var hasAttemptedAdd by remember { mutableStateOf(false) }
    
    val isTitleValid = title.isNotBlank()
    val isClientValid = client.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Project") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title, 
                    onValueChange = { title = it }, 
                    label = { Text("Project Title") }, 
                    singleLine = true,
                    isError = hasAttemptedAdd && !isTitleValid,
                    supportingText = { if (hasAttemptedAdd && !isTitleValid) Text("Required field") }
                )
                OutlinedTextField(
                    value = client, 
                    onValueChange = { client = it }, 
                    label = { Text("Client Name") }, 
                    singleLine = true,
                    isError = hasAttemptedAdd && !isClientValid,
                    supportingText = { if (hasAttemptedAdd && !isClientValid) Text("Required field") }
                )
                OutlinedTextField(
                    value = deadline, 
                    onValueChange = { deadline = it }, 
                    label = { Text("Deadline (e.g. Oct 15)") }, 
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = { 
                hasAttemptedAdd = true
                if (isTitleValid && isClientValid) {
                    onAdd(title, client, deadline.ifBlank { "Unscheduled" }) 
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
