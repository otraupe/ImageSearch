package com.otraupe.imagesearch.data.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://pixabay.com/api/"
const val DEFAULT_SEARCH_LANG = "en"
const val DEFAULT_PAGE_SIZE = 20
const val DEFAULT_PAGE_NUMBER = 1
const val DEFAULT_SEARCH_TERM: String = "fruits"
const val API_HOME_URL = "https://pixabay.com"

class ApiClient {
    companion object{
        private var retrofit: Retrofit?=null
        fun getApiClient(): Retrofit {
            val gson = GsonBuilder()
                .setLenient()
                .create()
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build()
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }
            return retrofit!!
        }
    }
}