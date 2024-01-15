package com.example.spotifyclone.sp

import android.content.Context
import android.content.SharedPreferences
import com.example.spotifyclone.ui.activity.MainActivity

class SharedPreference(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("Music", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveValue(key: String, value: String) {
        if(sharedPreferences.contains(key)){
            updateValue(key,value)
        }else{
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

    fun saveIsPlaying(isPlaying:Boolean){
        editor.putBoolean("isPlaying",isPlaying)
        editor.apply()
    }

    fun getValue(key: String,defaultValue: Boolean):Boolean{
        return sharedPreferences.getBoolean(key,defaultValue)?:defaultValue
    }

}