package com.example.spotifyclone.model.album.popularalbums

import com.example.spotifyclone.model.artist.ExternalUrls

data class Artist(
    val external_urls: ExternalUrls,
    val href: String,
    val id: String,
    val name: String,
    val type: String,
    val uri: String
)