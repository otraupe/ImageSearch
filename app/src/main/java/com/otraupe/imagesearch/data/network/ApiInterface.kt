package com.otraupe.imagesearch.data.network

import com.otraupe.imagesearch.data.model.ImageResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET(".")
    fun fetchImageHits(
        @Query("key") apiKey: String,
        @Query("lang") lang: String,
        @Query("q") query: String,
        @Query("page") page: Int = DEFAULT_PAGE_NUMBER,
        @Query("per_page") perPage: Int = DEFAULT_PAGE_SIZE,
    ): Call<ImageResponse>
}