package com.example.spotifyclone.domain.model.album.singlealbum

data class Artist(
    val external_urls: com.example.spotifyclone.domain.model.album.singlealbum.ExternalUrlsX,
    val href: String,
    val id: String,
    val name: String,
    val type: String,
    val uri: String
)