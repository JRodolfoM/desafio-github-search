package br.com.jrmantovani.githubsearch.data


import br.com.jrmantovani.githubsearch.domain.Repository

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubService {
    @GET("users/{user}/repos")
    suspend fun getAllRepositoriesByUser(
        @Path("user") user: String
    ): Response<List<Repository>>


}