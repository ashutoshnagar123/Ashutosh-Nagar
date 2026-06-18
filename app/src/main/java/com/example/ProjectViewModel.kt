package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.data.ProjectEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import com.example.repository.ProjectRepository

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    val pagedProjects = projectRepository.getPagedProjects().cachedIn(viewModelScope)

    val projects: StateFlow<List<ProjectEntity>> = projectRepository.allProjects
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addProject(title: String, deadline: String, client: String) {
        viewModelScope.launch {
            projectRepository.insertProject(
                ProjectEntity(
                    title = title,
                    deadline = deadline,
                    status = "Pending",
                    client = client,
                    progress = 0f
                )
            )
        }
    }

    fun updateProgress(project: ProjectEntity, newProgress: Float, newStatus: String) {
        viewModelScope.launch {
            projectRepository.updateProject(project.copy(progress = newProgress, status = newStatus))
        }
    }
    
    fun deleteProject(project: ProjectEntity) {
         viewModelScope.launch {
             projectRepository.deleteProject(project)
         }
    }
}
