package com.example.spotifyclone.data.network.consts


data class ConstValues(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
    var accessToken: String,
    val tokenType: String,
    val accessTokenUrl: String,
    val expiresIn: Long,
    val scope: String,
    var refreshToken: String,
    var timestamp: Long
)
