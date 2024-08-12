package com.example.cryptocurrencytracker

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Description (
    @SerializedName("en")
    val englishDescription: String
)