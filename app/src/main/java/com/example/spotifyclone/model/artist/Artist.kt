package com.example.spotifyclone.model.artist

data class Artist(
    val external_urls: ExternalUrls,
    val followers: Followers,
    val genres: List<String>,
    val href: String,
    val id: String,
    val images: List<Image>,
    val name: String,
    val popularity: Double,
    val type: String,
    val uri: String
)