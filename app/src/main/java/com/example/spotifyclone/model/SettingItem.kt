package com.example.spotifyclone.model

data class SettingItem(
    val name : Int,
    val canNavigate :Boolean = false,
    val navTo : Int? = null,
    val isLogout : Boolean = false
)
