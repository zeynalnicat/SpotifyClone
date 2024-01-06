package com.example.spotifyclone.model.album.singlealbum

data class Item(
    val artists: List<Artist>,
    val available_markets: List<String>,
    val disc_number: Int,
    val duration_ms: Int,
    val explicit: Boolean,
    val external_urls: ExternalUrlsX,
    val href: String,
    val id: String,
    val is_local: Boolean,
    val is_playable: Boolean,
    val linked_from: LinkedFrom,
    val name: String,
    val preview_url: String,
    val restrictions: Restrictions,
    val track_number: Int,
    val type: String,
    val uri: String
)