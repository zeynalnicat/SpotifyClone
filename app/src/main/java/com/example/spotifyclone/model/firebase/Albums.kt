package com.example.spotifyclone.model.firebase

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Albums(
    val coverImg: String? = "",
    val id: String? = "",
    val name: String? = "",
    val tracks: List<Tracks>? = emptyList()
)