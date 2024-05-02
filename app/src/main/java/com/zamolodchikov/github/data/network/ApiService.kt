package com.zamolodchikov.github.data.network

import com.zamolodchikov.github.data.model.RepoResponse
import com.zamolodchikov.github.data.model.UserListResponse
import com.zamolodchikov.github.data.model.UserResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("search/users")
    suspend fun fetchUsers(
        @Query("q") request: String,
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int,
    ): UserListResponse

    @GET("users/{login}")
    suspend fun fetchUser(
        @Path("login") login: String,
    ): UserResponse

    @GET("users/{login}/repos")
    suspend fun fetchRepos(
        @Path("login") login: String,
    ): List<RepoResponse>
}