package com.zamolodchikov.github.screens.users_list.viewmodel

sealed class UsersListEffect {

    data class NavigateToUser(val login: String): UsersListEffect()
}