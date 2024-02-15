package com.example.spotifyclone.network.db.playlist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface PlaylistDao {


    @Insert
    suspend fun insert(playlistEntity: PlaylistEntity):Long


    @Query("SELECT * FROM Playlists")
    suspend fun getAll():List<PlaylistEntity>

}