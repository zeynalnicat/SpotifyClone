package com.example.spotifyclone.util

import android.content.Context
import com.example.spotifyclone.model.dto.MusicItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object GsonHelper {
    private const val FILE_NAME = "tracks.json"

    fun serializeTracks(context: Context, tracks: List<MusicItem>) {
        val gson = Gson()
        val json = gson.toJson(tracks)
        File(context.filesDir, FILE_NAME).writeText(json)
    }

    fun deserializeTracks(context: Context): List<MusicItem> {
        val gson = Gson()
        val file = File(context.filesDir, FILE_NAME)

        return if (file.exists()) {
            val json = file.readText()
            val type = object : TypeToken<List<MusicItem>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun hasTracks(context: Context): Boolean {
        val file = File(context.filesDir, FILE_NAME)
        return file.exists()
    }
}
