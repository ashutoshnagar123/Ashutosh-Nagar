package com.example.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.data.ToolDao
import com.example.data.ToolResultEntity
import kotlinx.coroutines.flow.Flow

class ToolRepository(private val toolDao: ToolDao) {
    fun getResultsForTool(toolName: String): Flow<List<ToolResultEntity>> {
        return toolDao.getResultsForTool(toolName)
    }

    fun getPagedResultsForTool(toolName: String): Flow<PagingData<ToolResultEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { toolDao.getPagedResultsForTool(toolName) }
        ).flow
    }

    suspend fun insertResult(result: ToolResultEntity) {
        toolDao.insertResult(result)
    }
}
