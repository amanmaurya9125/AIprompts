package com.aman.aiprompts.RoomDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverter
import androidx.room.Update
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow

@Dao
interface PromptDao{

    @Query("SELECT id FROM Prompts")
    suspend fun getAllIds(): List<String>

    @Query("Select * from Prompts")
    fun getAllPrompts(): Flow<List<PromptEntity>>

    @Query("Select * from Prompts where isFavorite = 1")
    fun getFavouritePrompts(): Flow<List<PromptEntity>>

    @Query("""Select * from Prompts where category LIKE '%' || :category || '%' """)
    fun getCategoryPrompts(category : String): Flow<List<PromptEntity>>

    @Query("SELECT COUNT(*) FROM Prompts")
    suspend fun getCount(): Int
    @Query("SELECT * FROM Prompts WHERE id = :id")
    suspend fun getPromptById(id: String): PromptEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(prompts: List<PromptEntity>)

    @Update
    suspend fun updatePrompt(prompt: PromptEntity)
}

class Converter{
    @TypeConverter
    fun fromList(list : List<String>) : String {
       return Gson().toJson(list)
    }

    @TypeConverter
    fun tolist(value : String): List<String> {
        return Gson().fromJson(
            value,
            object : TypeToken<List<String>>() {}.type
        )
    }
}