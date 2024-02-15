package com.example.spotifyclone.network.db.artist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.spotifyclone.network.db.artist.ArtistsEntity


@Dao
interface ArtistDao {
    @Insert
    suspend fun insert(artistsEntity: ArtistsEntity): Long

    @Query("Select * from artists")
    suspend fun getAll(): List<ArtistsEntity>

    @Query("Select artistId from artists")
    suspend fun getArtistId():List<String>
}