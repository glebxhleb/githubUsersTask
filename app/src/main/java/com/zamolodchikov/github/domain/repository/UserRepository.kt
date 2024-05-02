package com.zamolodchikov.github.domain.repository

import com.zamolodchikov.github.domain.model.Repo
import com.zamolodchikov.github.domain.model.User

interface UserRepository {

    suspend fun fetchUsers(request: String, page: Int): List<User>

    suspend fun fetchUser(login: String): User

    suspend fun fetchUsersRepos(login: String): List<Repo>
}