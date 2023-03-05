package com.dggorbachev.voicenotes.data.remote

import com.dggorbachev.voicenotes.data.remote.model.ResponseDocModel
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface DocsApi {
    @Multipart
    @POST(".")
    suspend fun sendDoc(
        @Part filePart: MultipartBody.Part,
    ): ResponseDocModel
}