package com.example.spotifyclone.domain.model.artist

data class ArtistsX(
    val href: String,
    val items: List<com.example.spotifyclone.domain.model.artist.Artist>,
    val limit: Int,
    val next: String,
    val offset: Int,
    val previous: Any,
    val total: Int
)