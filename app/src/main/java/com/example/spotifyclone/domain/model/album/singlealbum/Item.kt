package com.example.spotifyclone.domain.model.album.singlealbum

data class Item(
    val artists: List<com.example.spotifyclone.domain.model.album.singlealbum.Artist>,
    val available_markets: List<String>,
    val disc_number: Int,
    val duration_ms: Int,
    val explicit: Boolean,
    val external_urls: com.example.spotifyclone.domain.model.album.singlealbum.ExternalUrlsX,
    val href: String,
    val id: String,
    val is_local: Boolean,
    val is_playable: Boolean,
    val linked_from: com.example.spotifyclone.domain.model.album.singlealbum.LinkedFrom,
    val name: String,
    val preview_url: String,
    val restrictions: com.example.spotifyclone.domain.model.album.singlealbum.Restrictions,
    val track_number: Int,
    val type: String,
    val uri: String
)