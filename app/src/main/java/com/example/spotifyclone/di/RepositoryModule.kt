package com.example.spotifyclone.di

import android.content.Context
import com.example.spotifyclone.domain.MusicRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideMusicRepo(@ApplicationContext context: Context):MusicRepository{
          return MusicRepository(context)
    }
}