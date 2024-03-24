package com.example.spotifyclone.domain.model.album.trysomething

data class Tracks(
    val href: String,
    val items: List<com.example.spotifyclone.domain.model.album.trysomething.Item>,
    val limit: Int,
    val next: Any,
    val offset: Int,
    val previous: Any,
    val total: Int
)