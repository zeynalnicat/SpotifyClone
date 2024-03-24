package com.example.spotifyclone.domain.model.dto

import com.example.spotifyclone.domain.model.firebase.Tracks
import java.io.Serializable

data class Album (
    val coverImg: String,
    val id: String,
    val name: String,
    val tracks: List<com.example.spotifyclone.domain.model.firebase.Tracks>,
    var isFirebase : Boolean = false,
    var isLibrary : Boolean = false,
    var isDeezer: Boolean = false
):Serializable