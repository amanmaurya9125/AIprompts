package com.aman.aiprompts.RoomDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Prompts")
data class PromptEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val prompt: String,
    val imageUrl: String,
    val category: List<String>,
    val isFavorite : Boolean = false
)
