package com.example.cryptocurrencytracker

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Currency (
    @SerializedName("id")
    val id: String,
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("current_price")
    val currentPrice: Float,
    @SerializedName("price_change_percentage_24h")
    val priceChangePercentage24h: Float

) {
    override fun toString(): String {
        return "USDResponse(id='$id', symbol='$symbol', name='$name', image='$image', currentPrice=$currentPrice, priceChangePercentage24h=$priceChangePercentage24h)"
    }
}