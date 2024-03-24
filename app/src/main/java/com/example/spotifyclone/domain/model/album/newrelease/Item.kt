package com.example.spotifyclone.domain.model.album.newrelease

import com.example.spotifyclone.domain.model.artist.Artist
import com.example.spotifyclone.domain.model.artist.ExternalUrls
import java.io.Serializable

data class Item(
    val album_type: String,
    val artists: List<com.example.spotifyclone.domain.model.artist.Artist>,
    val available_markets: List<String>,
    val external_urls: com.example.spotifyclone.domain.model.artist.ExternalUrls,
    val href: String,
    val id: String,
    val images: List<com.example.spotifyclone.domain.model.album.newrelease.Image>,
    val name: String,
    val release_date: String,
    val release_date_precision: String,
    val total_tracks: Int,
    val type: String,
    val uri: String
):Serializable