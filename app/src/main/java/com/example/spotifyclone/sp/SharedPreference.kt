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

    fun saveValue(key: String, value: Int) {
        if (sharedPreferences.contains(key)) {
            updateValue(key, value)
        } else {
            editor.putInt(key, value)
            editor.apply()
        }
    }
    fun updateValue(key: String, newValue: String) {
        editor.putString(key, newValue)
        editor.apply()
    }
    fun updateValue(key: String, newValue: Int) {
        editor.putInt(key, newValue)
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
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun getValue(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun removeCurrent(){
        editor.remove("PlayingMusic")
        editor.remove("PlayingMusicUri")
        editor.remove("PlayingMusicArtist")

    }

}