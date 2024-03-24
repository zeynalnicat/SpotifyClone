package com.example.spotifyclone.domain.model.artist

data class Artist(
    val external_urls: com.example.spotifyclone.domain.model.artist.ExternalUrls,
    val followers: com.example.spotifyclone.domain.model.artist.Followers?,
    val genres: List<String>?,
    val href: String,
    val id: String,
    val images: List<com.example.spotifyclone.domain.model.artist.Image>?,
    val name: String,
    val popularity: Double?,
    val type: String,
    val uri: String
)