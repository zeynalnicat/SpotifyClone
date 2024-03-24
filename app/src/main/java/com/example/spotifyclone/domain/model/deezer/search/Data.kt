package com.example.spotifyclone.domain.model.deezer.search

data class Data(
    val album: com.example.spotifyclone.domain.model.deezer.search.Album,
    val artist: com.example.spotifyclone.domain.model.deezer.search.Artist,
    val duration: Int,
    val explicit_content_cover: Int,
    val explicit_content_lyrics: Int,
    val explicit_lyrics: Boolean,
    val id: Long,
    val link: String,
    val md5_image: String,
    val preview: String,
    val rank: Int,
    val readable: Boolean,
    val title: String,
    val title_short: String,
    val title_version: String,
    val type: String
)