package com.example.spotifyclone.model.deezer.search

data class Search(
    val `data`: List<Data>,
    val next: String,
    val total: Int
)