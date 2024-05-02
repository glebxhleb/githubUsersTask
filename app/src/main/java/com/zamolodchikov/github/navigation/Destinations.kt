package com.zamolodchikov.github.navigation

sealed class Screen {
    abstract val destination: String

    object UsersList: Screen() {
        override val destination = "users_list"
    }

    object UserDetails: Screen() {
        const val KEY_USER_LOGIN = "user_login"
        override val destination = "user_details/{$KEY_USER_LOGIN}"
        fun navigate(login: String) = "user_details/$login"
    }
}