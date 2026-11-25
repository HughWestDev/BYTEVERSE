package dev.west.byteverse.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApiService {

    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ): Response<GitHubUserDto>

    @GET("users/{username}/repos")
    suspend fun getUserRepos(
        @Path("username") username: String,
        @Query("per_page") perPage: Int = 100,
        @Query("page") page: Int = 1,
        @Query("sort") sort: String = "updated"
    ): Response<List<GitHubRepoDto>>
}
