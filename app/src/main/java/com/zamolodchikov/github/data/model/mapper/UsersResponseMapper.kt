package com.zamolodchikov.github.data.model.mapper

import com.zamolodchikov.github.data.model.UserResponse
import com.zamolodchikov.github.domain.model.User

fun UserResponse.mapToDomain(): User =
    User(
        id = id,
        login = login,
        avatarUrl = avatar_url,
        type = type,
    )