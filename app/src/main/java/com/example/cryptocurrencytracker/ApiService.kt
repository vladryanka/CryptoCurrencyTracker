package com.example.cryptocurrencytracker

import retrofit2.http.GET

public interface ApiService {
    @GET("coins/markets?vs_currency=usd")
    suspend fun loadUSDResponse(): List<Currency>

    @GET("coins/markets?vs_currency=rub")
    suspend fun loadRUBResponse(): List<Currency>

}