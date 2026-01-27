package com.aman.aiprompts.Repository

import android.content.Context
import com.aman.aiprompts.DataModel.JsonLoader
import com.aman.aiprompts.RoomDatabase.AppDatabase
import com.aman.aiprompts.RoomDatabase.PromptEntity
import kotlinx.coroutines.flow.Flow

class PromptRepository(private val context: Context) {

    private val dao = AppDatabase.getDatabase(context).PromptDao()

    fun getAllPrompts(): Flow<List<PromptEntity>> = dao.getAllPrompts()
    fun getPromptsByCategory(category: String): Flow<List<PromptEntity>> {
        return dao.getCategoryPrompts(category)
    }

    fun getFavoritePrompts(): Flow<List<PromptEntity>> = dao.getFavouritePrompts()

    suspend fun getPromptById(id: String): PromptEntity =
        dao.getPromptById(id)


    suspend fun preloadPromptsIfNeeded() {
        val existingID = dao.getAllIds().toSet()
        val dtoList = JsonLoader.loadPrompts(context)


        val entities = dtoList.filter { it.id !in existingID }
            .map {
                PromptEntity(
                    id = it.id,
                    title = it.title,
                    prompt = it.prompt,
                    imageUrl = it.imageUrl,
                    category = it.category,
                    isFavorite = false
                )
            }
        if (entities.isNotEmpty()) {
            dao.insertAll(entities)

        }
    }

    suspend fun updatePrompt(prompt: PromptEntity) {
        dao.updatePrompt(prompt)
    }

}