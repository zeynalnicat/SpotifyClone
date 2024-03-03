package com.example.spotifyclone.model.dto

import java.io.Serializable

data class Category (
    val name :String,
    val color : String,
    val img : String,
    val tracks : List<String>
):Serializable