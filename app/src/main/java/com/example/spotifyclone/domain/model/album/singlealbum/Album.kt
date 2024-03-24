package com.example.spotifyclone.domain.model.album.singlealbum

data class Album(
    val album_type: String,
    val artists: List<com.example.spotifyclone.domain.model.album.singlealbum.Artist>,
    val available_markets: List<String>,
    val copyrights: List<com.example.spotifyclone.domain.model.album.singlealbum.Copyright>,
    val external_ids: com.example.spotifyclone.domain.model.album.singlealbum.ExternalIds,
    val external_urls: com.example.spotifyclone.domain.model.album.singlealbum.ExternalUrlsX,
    val genres: List<String>,
    val href: String,
    val id: String,
    val images: List<com.example.spotifyclone.domain.model.album.singlealbum.Image>,
    val label: String,
    val name: String,
    val popularity: Int,
    val release_date: String,
    val release_date_precision: String,
    val restrictions: com.example.spotifyclone.domain.model.album.singlealbum.Restrictions,
    val total_tracks: Int,
    val tracks: com.example.spotifyclone.domain.model.album.singlealbum.Tracks,
    val type: String,
    val uri: String
)