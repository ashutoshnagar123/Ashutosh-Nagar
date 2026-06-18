package com.example.data

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getPagedMessages(): PagingSource<Int, ChatEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatEntity)
    
    @Query("DELETE FROM chat_messages")
    suspend fun clearHistory()
}

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getPagedProjects(): PagingSource<Int, ProjectEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)
}

@Dao
interface InvoiceDao {
    @Query("SELECT * FROM invoices ORDER BY date DESC")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices ORDER BY date DESC")
    fun getPagedInvoices(): PagingSource<Int, InvoiceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: InvoiceEntity)

    @Delete
    suspend fun deleteInvoice(invoice: InvoiceEntity)
}

@Dao
interface ToolDao {
    @Query("SELECT * FROM tool_results WHERE toolName = :toolName ORDER BY timestamp DESC")
    fun getResultsForTool(toolName: String): Flow<List<ToolResultEntity>>

    @Query("SELECT * FROM tool_results WHERE toolName = :toolName ORDER BY timestamp DESC")
    fun getPagedResultsForTool(toolName: String): PagingSource<Int, ToolResultEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: ToolResultEntity)
}

