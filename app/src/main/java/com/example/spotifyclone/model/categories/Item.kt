package com.example.spotifyclone.model.categories

data class Item(
    val href: String,
    val icons: List<Icon>,
    val id: String,
    val name: String
)