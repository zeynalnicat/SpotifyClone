package com.example.spotifyclone.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {

    @Singleton
    @Provides
    fun provideFirebase(): Firebase {
        return Firebase
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(firebase: Firebase): FirebaseAuth {
        return firebase.auth
    }

    @Singleton
    @Provides
    fun provideFireStore(firebase: Firebase): FirebaseFirestore {
        return firebase.firestore
    }
}