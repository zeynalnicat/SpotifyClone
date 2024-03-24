package com.example.spotifyclone.domain.model.dto

data class PlaylistModel(
    val id:String,
    val name:String ,
    val img : String = "",
    var countTrack :Int = 0,
    var isSelected: Boolean = false,
    var isLibrary :Boolean = false
)
