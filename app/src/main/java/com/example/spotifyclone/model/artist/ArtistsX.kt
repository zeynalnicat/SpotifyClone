package com.example.spotifyclone.model.artist

data class ArtistsX(
    val href: String,
    val items: List<Artist>,
    val limit: Int,
    val next: String,
    val offset: Int,
    val previous: Any,
    val total: Int
)