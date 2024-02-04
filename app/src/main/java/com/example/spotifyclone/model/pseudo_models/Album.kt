package com.example.spotifyclone.model.pseudo_models

import com.example.spotifyclone.model.firebase.Tracks
import java.io.Serializable

data class Album (
    val coverImg: String,
    val id: String,
    val name: String,
    val tracks: List<Tracks>,
    var isFirebase : Boolean = false
):Serializable