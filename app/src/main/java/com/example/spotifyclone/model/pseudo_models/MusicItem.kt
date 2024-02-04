package com.example.spotifyclone.model.pseudo_models



data class MusicItem(
    val artist: String,
    val id: String,
    val name: String,
    val trackUri: String,
    var isPlayed: Boolean = false
)