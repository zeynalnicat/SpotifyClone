package com.example.spotifyclone.model.pseudo_models


import com.example.spotifyclone.model.album.singlealbum.Item


data class MusicItem(
    val item: Item ,
    var isPlayed : Boolean = false
)