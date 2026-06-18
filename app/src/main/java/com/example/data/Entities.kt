package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val deadline: String,
    val status: String,
    val client: String? = null,
    val progress: Float = 0f,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientName: String,
    val serviceDescription: String,
    val amount: Double,
    val date: Long = System.currentTimeMillis()
)

@Entity(tableName = "tool_results")
data class ToolResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val toolName: String,
    val query: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)
