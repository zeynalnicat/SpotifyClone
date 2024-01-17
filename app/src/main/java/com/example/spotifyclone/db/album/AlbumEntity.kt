package com.example.spotifyclone.db.album

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity("Album", indices = [Index(value = ["albumId"], unique = true)])
data class AlbumEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "albumId")
    val albumId: String,
)
