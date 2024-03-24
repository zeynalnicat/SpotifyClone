package com.example.spotifyclone.domain.model.dto

data class LikedSongs(
    val name: String,
    val artist: String,
    val imgUri: String,
    val uri: String,
    var isPlayed: Boolean = false,
    var isTopTracks : Boolean = false,
    var isFromGenre : Boolean = false
)
