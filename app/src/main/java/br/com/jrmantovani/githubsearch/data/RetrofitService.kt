package br.com.jrmantovani.githubsearch.data


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    private const val BASE_URL = "https://api.github.com/"


    fun <T> getAPI(classe: Class<T>): T {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(classe)
    }


}