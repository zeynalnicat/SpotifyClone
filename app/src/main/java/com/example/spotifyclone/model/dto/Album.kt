package com.example.spotifyclone.model.dto

import com.example.spotifyclone.model.firebase.Tracks
import java.io.Serializable

data class Album (
    val coverImg: String,
    val id: String,
    val name: String,
    val tracks: List<Tracks>,
    var isFirebase : Boolean = false,
    var isLibrary : Boolean = false
):Serializable