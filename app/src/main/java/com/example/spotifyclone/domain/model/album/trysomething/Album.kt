package com.example.spotifyclone.domain.model.album.trysomething

import com.example.spotifyclone.domain.model.album.newrelease.Image
import com.example.spotifyclone.domain.model.artist.Artist
import com.example.spotifyclone.domain.model.artist.ExternalUrls

data class Album(
    val album_type: String,
    val artists: List<com.example.spotifyclone.domain.model.artist.Artist>,
    val available_markets: List<String>,
    val copyrights: List<com.example.spotifyclone.domain.model.album.trysomething.Copyright>,
    val external_ids: com.example.spotifyclone.domain.model.album.trysomething.ExternalIds,
    val external_urls: com.example.spotifyclone.domain.model.artist.ExternalUrls,
    val genres: List<Any>,
    val href: String,
    val id: String,
    val images: List<com.example.spotifyclone.domain.model.album.newrelease.Image>,
    val label: String,
    val name: String,
    val popularity: Int,
    val release_date: String,
    val release_date_precision: String,
    val total_tracks: Int,
    val tracks: com.example.spotifyclone.domain.model.album.trysomething.Tracks,
    val type: String,
    val uri: String
)