package com.zamolodchikov.github.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.zamolodchikov.github.data.network.ApiService
import com.zamolodchikov.github.data.network.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    internal fun provideOkHttpClient(
        authenticator: TokenAuthenticator,
    ): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
            .authenticator(authenticator)
        return httpClient.build()
    }

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    internal fun provideApiService(gsonBuilder: Gson, okHttpClient: OkHttpClient): ApiService {
        return Retrofit
            .Builder()
            .baseUrl("https://api.github.com")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder))
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
    }
}