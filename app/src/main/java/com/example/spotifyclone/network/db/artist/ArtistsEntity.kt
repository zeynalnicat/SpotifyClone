package com.example.spotifyclone.network.db.artist

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity("Artists")
data class ArtistsEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0 ,
    val name : String ,
    val artistId :String
)
