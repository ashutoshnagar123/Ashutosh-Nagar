package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.data.ChatEntity
import com.example.network.Content
import com.example.network.GenerateContentRequest
import com.example.network.Part
import com.example.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import com.example.repository.ChatRepository

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {
    
    val messages: StateFlow<List<ChatMessage>> = chatRepository.allMessages
        .map { entities -> entities.map { ChatMessage(it.text, it.isUser) } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val pagedMessages = chatRepository.getPagedMessages()
        .map { pagingData -> pagingData.map { ChatMessage(it.text, it.isUser) } }
        .cachedIn(viewModelScope)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(text: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            // 1. Save user msg to DB
            val userMsg = ChatEntity(text = text, isUser = true)
            chatRepository.insertMessage(userMsg)
            
            try {
                // We rely on the current value in Flow or just build history directly.
                // However, since state flow is reactive, let's just pull from DB directly 
                // but building from StateFlow value might be slightly out of sync if it hasn't emitted yet.
                // Building from the updated list:
                val history = messages.value + ChatMessage(text, true)
                val contents = history.map { msg ->
                    val role = if (msg.isUser) "user" else "model"
                    Content(listOf(Part(msg.text)), role) 
                }
                
                val req = GenerateContentRequest(contents = contents)
                val response = RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, req)
                
                val aiText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "I'm sorry, I couldn't understand that."
                
                // 2. Save AI msg to DB
                chatRepository.insertMessage(ChatEntity(text = aiText, isUser = false))
                
            } catch (e: Exception) {
                chatRepository.insertMessage(ChatEntity(text = "Error: Please check your network connection.", isUser = false))
            } finally {
                _isLoading.value = false
            }
        }
    }
}

data class ChatMessage(val text: String, val isUser: Boolean)
