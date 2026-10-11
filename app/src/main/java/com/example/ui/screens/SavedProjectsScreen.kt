package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectEntity
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.TealSuccess
import com.example.util.AppLanguage
import com.example.util.LanguageManager
import com.example.viewmodel.AppScreen

@Composable
fun SavedProjectsScreen(
    language: AppLanguage,
    projects: List<ProjectEntity>,
    selectedProjectId: Long?,
    onSelectProject: (Long) -> Unit,
    onDuplicateProject: (Long) -> Unit,
    onDeleteProject: (Long) -> Unit,
    onUpdateProjectStatus: (Long, String) -> Unit,
    onNewProjectClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var projectToDelete by remember { mutableStateOf<ProjectEntity?>(null) }

    val filterOptions = listOf("All", "Draft", "Sent", "Accepted", "Completed", "Rejected")

    val filteredProjects = remember(projects, searchQuery, selectedFilter) {
        projects.filter { p ->
            val matchQuery = searchQuery.isBlank() ||
                p.customerName.contains(searchQuery, ignoreCase = true) ||
                p.mobileNumber.contains(searchQuery, ignoreCase = true) ||
                p.siteName.contains(searchQuery, ignoreCase = true) ||
                p.estimateNumber.contains(searchQuery, ignoreCase = true)

            val matchFilter = selectedFilter == "All" || p.status.equals(selectedFilter, ignoreCase = true)
            matchQuery && matchFilter
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Search field
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(LanguageManager.get("search_placeholder", language)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate500) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_search_projects"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
            }

            // Filter chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filterOptions) { opt ->
                        val isSel = selectedFilter == opt
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSel) PrimaryNavy else Color.White,
                            shadowElevation = if (isSel) 2.dp else 1.dp,
                            modifier = Modifier.clickable { selectedFilter = opt }
                        ) {
                            Text(
                                text = opt,
                                color = if (isSel) Color.White else Slate700,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Projects List
            if (filteredProjects.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Folder, contentDescription = null, tint = Slate500, modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "No projects found", fontWeight = FontWeight.Bold, color = Slate700)
                            Text(text = "Tap '+ New Project' to create one", fontSize = 12.sp, color = Slate500)
                        }
                    }
                }
            } else {
                items(filteredProjects) { project ->
                    val isSelected = selectedProjectId == project.id
                    ProjectCardItem(
                        project = project,
                        isSelected = isSelected,
                        onSelect = { onSelectProject(project.id) },
                        onDuplicate = { onDuplicateProject(project.id) },
                        onDelete = { projectToDelete = project },
                        onStatusChange = { newSt -> onUpdateProjectStatus(project.id, newSt) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        FloatingActionButton(
            onClick = onNewProjectClick,
            containerColor = PrimaryNavy,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_new_project")
        ) {
            Row(modifier = Modifier.padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "New Estimate", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (projectToDelete != null) {
        AlertDialog(
            onDismissRequest = { projectToDelete = null },
            title = { Text("Delete Project") },
            text = { Text("Are you sure you want to delete '${projectToDelete?.siteName}' for '${projectToDelete?.customerName}'? All room measurements and estimates will be removed.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        projectToDelete?.let { onDeleteProject(it.id) }
                        projectToDelete = null
                    }
                ) {
                    Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { projectToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun ProjectCardItem(
    project: ProjectEntity,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onStatusChange: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFEFF6FF) else Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = project.siteName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp,
                            color = PrimaryNavy
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = PrimaryBlue
                            ) {
                                Text(
                                    text = "Active",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "${project.customerName} • ${project.mobileNumber} • ${project.flatType}",
                        fontSize = 12.sp,
                        color = Slate700
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (project.status) {
                        "Accepted" -> Color(0xFFD1FAE5)
                        "Sent" -> Color(0xFFFEF3C7)
                        "Completed" -> Color(0xFFDBEAFE)
                        "Rejected" -> Color(0xFFFEE2E2)
                        else -> Color(0xFFF1F5F9)
                    }
                ) {
                    Text(
                        text = project.status,
                        color = when (project.status) {
                            "Accepted" -> TealSuccess
                            "Sent" -> Color(0xFFB45309)
                            "Completed" -> PrimaryNavy
                            "Rejected" -> Color(0xFFDC2626)
                            else -> Slate700
                        },
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${project.estimateNumber} • ${project.estimateDate}",
                    fontSize = 11.sp,
                    color = Slate500
                )

                Row {
                    IconButton(onClick = onDuplicate, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = Slate500, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
