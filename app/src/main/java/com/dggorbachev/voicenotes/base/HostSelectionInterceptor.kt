package com.dggorbachev.voicenotes.base

import com.dggorbachev.voicenotes.base.common.Constants.BASE_URL
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject


class HostSelectionInterceptor @Inject constructor() :
    Interceptor {

    @Volatile
    private var host: HttpUrl? = BASE_URL.toHttpUrlOrNull()

    fun setHostBaseUrl(url: String) {
        host = url.toHttpUrlOrNull()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        if (host != null) {
            request = request.newBuilder()
                .url(host!!)
                .build()
        }
        return chain.proceed(request)
    }
}