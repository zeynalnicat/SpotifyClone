package com.example.spotifyclone.domain.model.album.newrelease

import java.io.Serializable

data class AlbumsX(
    val href: String,
    val items: List<com.example.spotifyclone.domain.model.album.newrelease.Item>,
    val limit: Int,
    val next: String,
    val offset: Int,
    val previous: Any,
    val total: Int
):Serializable