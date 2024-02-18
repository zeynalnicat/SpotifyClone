package com.example.spotifyclone.sp

import android.content.Context
import android.content.SharedPreferences
import com.example.spotifyclone.model.dto.MusicItem
import com.example.spotifyclone.ui.activity.MainActivity
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class SharedPreference(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("Music", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    private val PREFS_NAME = "MyAppPrefs"
    private val SONGS_KEY = "songs_key"
    fun saveValue(key: String, value: String) {
        if (sharedPreferences.contains(key)) {
            updateValue(key, value)
        } else {
            editor.putString(key, value)
            editor.apply()
        }
    }

    fun updateValue(key: String, newValue: String) {
        editor.putString(key, newValue)
        editor.apply()
    }

    fun containsValue(value: String): Boolean {
        val allEntries: Map<String, *> = sharedPreferences.all
        for ((_, entryValue) in allEntries) {
            if (entryValue is String && entryValue == value) {
                return true
            }
        }
        return false
    }

    fun getValue(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun saveIsPlaying(isPlaying: Boolean) {
        editor.putBoolean("isPlaying", isPlaying)
        editor.apply()
    }

    fun getValue(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue) ?: defaultValue
    }

    fun saveSongsList(songsList: List<MusicItem>) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(songsList)
        editor.putString(SONGS_KEY, json)
        editor.apply()
    }

    fun getSongsList(): List<MusicItem> {
        val gson = Gson()
        val json = sharedPreferences.getString(SONGS_KEY, null)
        val type = object : TypeToken<List<MusicItem>>() {}.type

        return gson.fromJson(json, type) ?: emptyList()
    }

}