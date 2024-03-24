package com.example.spotifyclone.domain.model.deezer.search

data class Search(
    val `data`: List<com.example.spotifyclone.domain.model.deezer.search.Data>,
    val next: String,
    val total: Int
)