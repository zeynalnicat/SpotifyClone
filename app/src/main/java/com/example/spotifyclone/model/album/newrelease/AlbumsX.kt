package com.example.spotifyclone.model.album.newrelease

import java.io.Serializable

data class AlbumsX(
    val href: String,
    val items: List<Item>,
    val limit: Int,
    val next: String,
    val offset: Int,
    val previous: Any,
    val total: Int
):Serializable