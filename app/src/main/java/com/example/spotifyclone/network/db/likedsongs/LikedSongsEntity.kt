package com.example.spotifyclone.network.db.likedsongs

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity("Liked Songs")
data class LikedSongsEntity (
    @PrimaryKey(autoGenerate = true)
    val id :Int = 0,
    val name :String ,
    val artist : String ,
    val imgUri :String ,
    val uri : String
)