package com.example.spotifyclone.network.retrofit

import android.util.Base64
import android.util.Log
import com.example.spotifyclone.network.retrofit.consts.ConstValues
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject

class TokenRefresher @Inject constructor(
    private val constValues: ConstValues,
    private val okHttpClient: OkHttpClient,

    ) {
    fun getSpotifyAuthorizationUrl(): String {
        return "https://accounts.spotify.com/authorize" +
                "?client_id=${constValues.clientId}" +
                "&redirect_uri=${constValues.redirectUri}" +
                "&response_type=code" +
                "&scope=${constValues.scope}"
    }


    fun refreshAccessToken(): String {
        val refreshRequestBody = FormBody.Builder()
            .add("grant_type", "refresh_token")
            .add("refresh_token", constValues.refreshToken)
            .build()

        val authHeader = "Basic ${generateBase64AuthHeader()}"
        val refreshRequest = Request.Builder()
            .url(constValues.accessTokenUrl)
            .post(refreshRequestBody)
            .header("Authorization", authHeader)
            .build()


        try {
            val refreshResponse = okHttpClient.newCall(refreshRequest).execute()
            val refreshedJsonResponse = refreshResponse.body()?.string()

            Log.d("Token", "Refresh response: $refreshResponse")

            if (refreshResponse.isSuccessful) {
                constValues.accessToken = parseAccessTokenFromResponse(refreshedJsonResponse)
                constValues.timestamp = System.currentTimeMillis() + (constValues.expiresIn * 1000)
                Log.d("Token", "Access token successfully refreshed: ${constValues.accessToken}")
            } else {
                Log.d("Token", "Token refresh failed. Response: $refreshedJsonResponse")
            }
        } catch (e: Exception) {
            Log.d("Token", "Token refresh exception: ${e.message}")
        }

        return ""
    }

    private fun parseAccessTokenFromResponse(response: String?): String {
        return try {
            val jsonObject = JSONObject(response)
            jsonObject.getString("access_token")
        } catch (e: Exception) {

            ""
        }
    }

    fun isAccessTokenExpired(): Boolean {
        return System.currentTimeMillis() >= constValues.timestamp
    }

    private fun generateBase64AuthHeader(): String {
        val credentials = "${constValues.clientId}:${constValues.clientSecret}"
        return Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
    }
}
