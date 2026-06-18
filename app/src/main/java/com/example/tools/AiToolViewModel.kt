package com.example.tools

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.ToolResultEntity
import com.example.network.Content
import com.example.network.GenerateContentRequest
import com.example.network.Part
import com.example.network.RetrofitClient
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.repository.ToolRepository

@HiltViewModel
class AiToolViewModel @Inject constructor(
    private val toolRepository: ToolRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val toolName: String = savedStateHandle["toolId"] ?: ""
    private val systemPrompt: String = getSystemPromptForTool(toolName)

    private fun getSystemPromptForTool(toolId: String): String {
        return when (toolId) {
            "proposal" -> "You are an expert freelance proposal writer. Write a compelling proposal to win the described project."
            "content" -> "You are a professional content writer. Write high-quality, engaging content based on the user's topic."
            "email" -> "You are a polite, professional email assistant. Draft an email based on the prompt."
            "resume" -> "You are an expert career coach. Generate a professional resume summary and bullet points based on the input."
            "code" -> "You are an expert software engineer. Generate clean, efficient code for the exact requirements."
            else -> "You are a helpful AI assistant."
        }
    }

    val pagedHistory: Flow<PagingData<ToolResultEntity>> = toolRepository.getPagedResultsForTool(toolName)
        .cachedIn(viewModelScope)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun generate(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // We use system prompt by just prepending it or using the systemInstruction field if it's supported
                // According to GeminiApiService, systemInstruction is supported
                val systemContent = Content(listOf(Part(systemPrompt)))
                val req = GenerateContentRequest(
                    contents = listOf(Content(listOf(Part(query)))),
                    systemInstruction = systemContent
                )
                
                val response = RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, req)
                val aiText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No response generated."
                
                toolRepository.insertResult(
                    ToolResultEntity(
                        toolName = toolName,
                        query = query,
                        result = aiText
                    )
                )
                
            } catch (e: Exception) {
                 toolRepository.insertResult(
                    ToolResultEntity(
                        toolName = toolName,
                        query = query,
                        result = "Network Error: Please check your connection or try again later. If the issue persists, the AI service might be temporarily unavailable."
                    )
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
}
