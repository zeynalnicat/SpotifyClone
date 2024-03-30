package com.example.spotifyclone.domain.model.dto

import java.io.Serializable


//TODO Parcelize plugin
data class MusicItem(
    val artist: String,
    val id: String,
    val name: String,
    val trackUri: String,
    var img: String = "",
    var isPlayed: Boolean = false

) : Serializable