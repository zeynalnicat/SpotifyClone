package com.example.spotifyclone.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity("Artists")
data class ArtistsEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0 ,
    val name : String ,
    val artistId :String
)
