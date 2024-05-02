package com.zamolodchikov.github.screens.user_details.viewmodel

import com.zamolodchikov.github.domain.model.Repo
import com.zamolodchikov.github.domain.model.User
import javax.annotation.concurrent.Immutable

@Immutable
data class UserDetailsState(
    val title: String,
    val user: User? = null,
    val stars: Int = 0,
    val repos: List<Repo> = emptyList(),
    val errorMessage: String? = null,
    val showProgress: Boolean = false,
)