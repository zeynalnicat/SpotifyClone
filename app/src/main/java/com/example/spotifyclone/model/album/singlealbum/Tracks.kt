package com.example.spotifyclone.model.album.singlealbum

data class Tracks(
    val href: String,
    val items: List<Item>,
    val limit: Int,
    val next: String,
    val offset: Int,
    val previous: String,
    val total: Int
)