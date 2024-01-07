package com.example.spotifyclone.model.album.popularalbums

import com.example.spotifyclone.model.album.newrelease.Image
import com.example.spotifyclone.model.artist.Artist
import com.example.spotifyclone.model.artist.ExternalUrls

data class Album(
    val album_type: String,
    val artists: List<Artist>,
    val available_markets: List<String>,
    val copyrights: List<Copyright>,
    val external_ids: ExternalIds,
    val external_urls: ExternalUrls,
    val genres: List<Any>,
    val href: String,
    val id: String,
    val images: List<Image>,
    val label: String,
    val name: String,
    val popularity: Int,
    val release_date: String,
    val release_date_precision: String,
    val total_tracks: Int,
    val tracks: Tracks,
    val type: String,
    val uri: String
)