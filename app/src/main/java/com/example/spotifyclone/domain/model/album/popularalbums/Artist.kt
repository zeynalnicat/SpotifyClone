package com.example.spotifyclone.domain.model.album.popularalbums

import com.example.spotifyclone.domain.model.artist.ExternalUrls

data class Artist(
    val external_urls: com.example.spotifyclone.domain.model.artist.ExternalUrls,
    val href: String,
    val id: String,
    val name: String,
    val type: String,
    val uri: String
)