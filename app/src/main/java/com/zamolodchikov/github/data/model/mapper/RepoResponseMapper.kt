package com.zamolodchikov.github.data.model.mapper

import com.zamolodchikov.github.data.model.RepoResponse
import com.zamolodchikov.github.domain.model.Repo

fun RepoResponse.mapToDomain(): Repo =
    Repo(
        id = id,
        name = name,
        stars = stargazers_count,
    )