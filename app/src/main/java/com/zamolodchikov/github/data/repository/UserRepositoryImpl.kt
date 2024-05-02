package com.zamolodchikov.github.data.repository

import com.zamolodchikov.github.data.model.mapper.mapToDomain
import com.zamolodchikov.github.data.network.ApiService
import com.zamolodchikov.github.domain.model.Repo
import com.zamolodchikov.github.domain.model.User
import com.zamolodchikov.github.domain.repository.UserRepository
import com.zamolodchikov.github.utils.ConnectionException
import com.zamolodchikov.github.utils.ForbiddenException
import com.zamolodchikov.github.utils.InternalException
import com.zamolodchikov.github.utils.NotFoundException
import com.zamolodchikov.github.utils.UnauthorizedException
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
) : UserRepository {

    override suspend fun fetchUsers(request: String, page: Int): List<User> = makeRequest {
        apiService.fetchUsers(request, page, USERS_PAGE_SIZE).items.map { it.mapToDomain() }
    }

    override suspend fun fetchUser(login: String): User = makeRequest {
        apiService.fetchUser(login).mapToDomain()
    }

    override suspend fun fetchUsersRepos(login: String): List<Repo> = makeRequest {
        apiService.fetchRepos(login).map { it.mapToDomain() }
    }

    private suspend fun <T> makeRequest(block: suspend () -> T): T =
        try {
            block()
        } catch (e: Exception) {
            throw when (e) {
                //TODO ("Check ConnectivityManager")
                is UnknownHostException -> ConnectionException()
                is HttpException -> when {
                    e.message?.contains("401") ?: false -> UnauthorizedException()
                    e.message?.contains("403") ?: false -> ForbiddenException()
                    e.message?.contains("404") ?: false -> NotFoundException()
                    e.message?.contains("500") ?: false -> InternalException()
                    else -> e
                }

                else -> e
            }
        }

    companion object {
        private const val USERS_PAGE_SIZE = 30
    }
}