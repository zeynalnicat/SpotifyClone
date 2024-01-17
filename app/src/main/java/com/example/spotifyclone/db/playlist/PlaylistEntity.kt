package com.example.spotifyclone.db.playlist

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity("Playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val playlistName:String ,
    val playListImg:String = ""
)
