package com.example.spotifyclone.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.spotifyclone.db.album.AlbumDao
import com.example.spotifyclone.db.album.AlbumEntity
import com.example.spotifyclone.db.artist.ArtistDao
import com.example.spotifyclone.db.artist.ArtistsEntity
import com.example.spotifyclone.db.likedsongs.LikedSongsDao
import com.example.spotifyclone.db.likedsongs.LikedSongsEntity
import com.example.spotifyclone.db.playlist.PlaylistDao
import com.example.spotifyclone.db.playlist.PlaylistEntity


@Database(entities = [ArtistsEntity::class,AlbumEntity::class,PlaylistEntity::class,LikedSongsEntity::class], version = 1)
abstract class RoomDB : RoomDatabase() {

    abstract fun artistDao(): ArtistDao
    abstract fun albumDao():AlbumDao
    abstract fun playlistDao():PlaylistDao
    abstract fun likedSongsDao():LikedSongsDao

    companion object {
        private var INSTANCE: RoomDB? = null

        fun accessDb(context: Context): RoomDB? {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        RoomDB::class.java,
                        "Spotify"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}