package com.example.spotifyclone.model.pseudo_models

data class PlaylistModel(
    val id:Int,
    val name:String ,
    val img : String = "",
    var countTrack :Int = 0
)
