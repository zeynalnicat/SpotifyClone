package com.example.spotifyclone.db.album

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface AlbumDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(albumEntity: AlbumEntity):Long

    @Query("SELECT COUNT(*) FROM album WHERE albumId = :id")
    suspend fun checkInDB(id: String): Int

    @Query("Delete from album where albumId=:id")
    suspend fun delete(id:String)

    @Query("SELECT albumId FROM album")
    suspend fun getAll():List<String>
}