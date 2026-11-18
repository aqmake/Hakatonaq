package com.example.hakaton.data

import  com.example.hakaton.Config
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.jvm.java

object ApiClient {
    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
        .create()

    val api: Api = Retrofit.Builder()
        .baseUrl(Config.BASE_URL )
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(Api::class.java)
}
