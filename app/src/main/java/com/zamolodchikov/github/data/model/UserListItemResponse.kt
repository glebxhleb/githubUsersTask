package com.zamolodchikov.github.data.model

data class UserListResponse (
    val total_count: Int,
    val incomplete_results: Boolean,
    val items: List<UserResponse>,
    val errors: List<Error>? = null,
    val message: String? = null,
)

data class Error(
    val message: String,
    val resource: String,
    val field: String,
    val code: String
)