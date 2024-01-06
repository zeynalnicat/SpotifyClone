package com.example.spotifyclone.model.album.trysomething

import com.example.spotifyclone.model.artist.Artist
import com.example.spotifyclone.model.artist.ExternalUrls

data class Item(
    val artists: List<Artist>,
    val available_markets: List<String>,
    val disc_number: Int,
    val duration_ms: Int,
    val explicit: Boolean,
    val external_urls: ExternalUrls,
    val href: String,
    val id: String,
    val is_local: Boolean,
    val name: String,
    val preview_url: String,
    val track_number: Int,
    val type: String,
    val uri: String
)