package com.example.moviesapp.data.model

import com.google.gson.annotations.SerializedName

data class CastResponse(
    @SerializedName("cast")
    val cast: List<CastItem>

)

data class CastItem(
    val id: Int,
    val name: String,

    @SerializedName("profile_path")
    val profilePath: String?,

    @SerializedName("character")
    val character: String?
) {
    fun getProfileUrl(): String {
        return if (!profilePath.isNullOrEmpty()) {
            "https://image.tmdb.org/t/p/w500$profilePath"
        } else {
            ""
        }
    }
}
