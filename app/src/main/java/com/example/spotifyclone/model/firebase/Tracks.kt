package com.example.spotifyclone.model.firebase

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Tracks(
    val artist:String?="",
    val id:String ? = "",
    val name:String?= "",
    val trackUri: String? = ""
)
