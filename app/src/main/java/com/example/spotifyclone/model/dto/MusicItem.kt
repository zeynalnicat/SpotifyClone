package com.example.spotifyclone.model.dto



data class MusicItem(
    val artist: String,
    val id: String,
    val name: String,
    val trackUri: String,
    var isPlayed: Boolean = false
)