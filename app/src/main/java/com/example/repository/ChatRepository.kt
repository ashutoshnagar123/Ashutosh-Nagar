package com.example.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.data.ChatDao
import com.example.data.ChatEntity
import kotlinx.coroutines.flow.Flow

class ChatRepository(private val chatDao: ChatDao) {
    val allMessages: Flow<List<ChatEntity>> = chatDao.getAllMessages()
    
    fun getPagedMessages(): Flow<PagingData<ChatEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { chatDao.getPagedMessages() }
        ).flow
    }

    suspend fun insertMessage(message: ChatEntity) {
        chatDao.insertMessage(message)
    }

    suspend fun clearHistory() {
        chatDao.clearHistory()
    }
}
