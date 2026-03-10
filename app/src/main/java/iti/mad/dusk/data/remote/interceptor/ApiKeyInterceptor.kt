package iti.mad.dusk.data.remote.interceptor

import iti.mad.dusk.core.env.EnvProvider
import iti.mad.dusk.core.network.ApiConstants
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ApiKeyInterceptor @Inject constructor( private val envProvider: EnvProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val url = original.url.newBuilder()
            .addQueryParameter(ApiConstants.Params.API_KEY, envProvider.owmApiKey)
            .build()

        return chain.proceed(original.newBuilder().url(url).build())
    }
}