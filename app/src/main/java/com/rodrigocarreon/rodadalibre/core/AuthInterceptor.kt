package com.rodrigocarreon.rodadalibre.core

import com.rodrigocarreon.rodadalibre.data.local.TokenDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenDataStore: TokenDataStore
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            tokenDataStore.getToken().firstOrNull()
        }

        // Without it, Laravel answers validation and auth errors with a redirect that OkHttp follows to a 200
        val builder = chain.request().newBuilder()
            .header("Accept", "application/json")

        if(!token.isNullOrEmpty()){
            builder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(builder.build())
    }
}