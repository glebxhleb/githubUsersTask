package com.zamolodchikov.github.data.network


import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor() : Authenticator {
    // Insert your token to authenticate
    private var token: String? = null

    override fun authenticate(route: Route?, response: Response): Request? {
        if (token == null) return null
        return response.request.newBuilder().header("Authorization", "Bearer " + token).build()
    }
}