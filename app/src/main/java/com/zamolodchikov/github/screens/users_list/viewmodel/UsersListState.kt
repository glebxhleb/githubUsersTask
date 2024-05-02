package com.zamolodchikov.github.screens.users_list.viewmodel

import androidx.compose.runtime.Immutable
import com.zamolodchikov.github.domain.model.User

@Immutable
data class UsersListState(
    val items: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val query: String = "",
)