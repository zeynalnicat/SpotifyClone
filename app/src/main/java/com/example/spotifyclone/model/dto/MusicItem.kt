package com.example.spotifyclone.model.dto

import java.io.Serializable


data class MusicItem(
    val artist: String,
    val id: String,
    val name: String,
    val trackUri: String,
    var img: String = "",
    var isPlayed: Boolean = false

) : Serializable