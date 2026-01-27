package com.aman.aiprompts.DataModel

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object JsonLoader {
    fun loadPrompts(context: Context): List<PromptDTO>{
        return try {
            val json = context.assets.open("promptsJson.json")
                .bufferedReader()
                .use { it.readText() }

            Gson().fromJson(
                json,
                object : TypeToken<List<PromptDTO>>() {}.type
            )
        } catch (e: Exception) {
            emptyList()
        }
    }
}