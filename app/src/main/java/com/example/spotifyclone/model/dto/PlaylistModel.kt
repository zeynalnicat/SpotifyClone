package com.example.spotifyclone.model.dto

data class PlaylistModel(
    val id:Int,
    val name:String ,
    val img : String = "",
    var countTrack :Int = 0,
    var isSelected: Boolean = false
)
