package com.example.spotifyclone.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface ArtistDao {
    @Insert
    suspend fun insert(artistsEntity: ArtistsEntity): Long

    @Query("Select * from artists")
    suspend fun getAll(): List<ArtistsEntity>

    @Query("Select artistId from artists")
    suspend fun getArtistId():List<String>
}