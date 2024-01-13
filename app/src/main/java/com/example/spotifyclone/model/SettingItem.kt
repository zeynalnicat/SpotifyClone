package com.example.spotifyclone.model

data class SettingItem(
    val name : String,
    val canNavigate :Boolean = false,
    val navTo : Int? = null
)
