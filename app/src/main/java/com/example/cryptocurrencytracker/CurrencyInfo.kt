package com.example.cryptocurrencytracker

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CurrencyInfo(
    @SerializedName("id")
    val id: String,
    @SerializedName("categories")
    val categories: List<String>,
    @SerializedName("description")
    val description: Description
) {
    override fun toString(): String {
        return "CurrencyInfo(id='$id', categories='$categories', description='$description')"
    }
}