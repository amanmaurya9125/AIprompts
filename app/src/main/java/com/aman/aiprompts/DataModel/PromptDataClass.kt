package com.aman.aiprompts.DataModel


data class PromptDTO(
    val id: String,
    val title: String,
    val prompt: String,
    val imageUrl: String,
    val category: List<String>,
    val isFavorite : Boolean = false
)
