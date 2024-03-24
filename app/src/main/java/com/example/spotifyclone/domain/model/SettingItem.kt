package com.example.spotifyclone.domain.model

data class SettingItem(
    val name : Int,
    val canNavigate :Boolean = false,
    val navTo : Int? = null,
    val isLogout : Boolean = false
)
