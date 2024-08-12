package com.example.cryptocurrencytracker

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("coins/markets?vs_currency=usd")
    suspend fun loadUSDResponse(): List<Currency>

    @GET("coins/markets?vs_currency=rub")
    suspend fun loadRUBResponse(): List<Currency>

    @GET("coins/{id}")
    suspend fun loadCurrencyInfo(@Path("id") currencyId: String): CurrencyInfo

}