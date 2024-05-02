package com.zamolodchikov.github.di

import com.zamolodchikov.github.data.repository.UserRepositoryImpl
import com.zamolodchikov.github.domain.Loader
import com.zamolodchikov.github.domain.LoaderState
import com.zamolodchikov.github.domain.UsersLoader
import com.zamolodchikov.github.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    @Binds
    fun mapLoader(loader: UsersLoader): Loader<LoaderState>

    @Binds
    fun mapUserRepository(repository: UserRepositoryImpl): UserRepository
}