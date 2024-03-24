package com.example.spotifyclone.domain.model.firebase

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Albums(
    val coverImg: String? = "",
    val id: String? = "",
    val name: String? = "",
    val tracks: List<com.example.spotifyclone.domain.model.firebase.Tracks>? = emptyList()
)