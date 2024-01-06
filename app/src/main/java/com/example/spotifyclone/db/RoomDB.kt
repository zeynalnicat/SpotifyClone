package com.example.spotifyclone.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [ArtistsEntity::class], version = 1)
abstract class RoomDB : RoomDatabase() {

    abstract fun artistDao():ArtistDao

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