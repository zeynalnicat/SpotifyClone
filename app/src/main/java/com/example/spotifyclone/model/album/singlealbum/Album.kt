package com.example.spotifyclone.model.album.singlealbum

data class Album(
    val album_type: String,
    val artists: List<Artist>,
    val available_markets: List<String>,
    val copyrights: List<Copyright>,
    val external_ids: ExternalIds,
    val external_urls: ExternalUrlsX,
    val genres: List<String>,
    val href: String,
    val id: String,
    val images: List<Image>,
    val label: String,
    val name: String,
    val popularity: Int,
    val release_date: String,
    val release_date_precision: String,
    val restrictions: Restrictions,
    val total_tracks: Int,
    val tracks: Tracks,
    val type: String,
    val uri: String
)