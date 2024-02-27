package com.example.spotifyclone.resource

sealed class Resource<out zireddin: Any> {
    data class Success<out zireddin: Any>(val data: zireddin): Resource<zireddin>()
    data class Error(val exception: Throwable): Resource<Nothing>()
    object Loading: Resource<Nothing>()
}