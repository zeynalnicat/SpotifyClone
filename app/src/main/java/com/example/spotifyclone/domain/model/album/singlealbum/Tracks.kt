package com.example.spotifyclone.domain.model.album.singlealbum

data class Tracks(
    val href: String,
    val items: List<com.example.spotifyclone.domain.model.album.singlealbum.Item>,
    val limit: Int,
    val next: String,
    val offset: Int,
    val previous: String,
    val total: Int
)