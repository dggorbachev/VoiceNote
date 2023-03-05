package com.dggorbachev.voicenotes.data.remote

import com.dggorbachev.voicenotes.data.remote.model.ResponseDocModel
import okhttp3.MultipartBody
import javax.inject.Inject

class DocsRemoteSource @Inject constructor(
    private val docsApi: DocsApi,
) {

    suspend fun sendDoc(filePart: MultipartBody.Part): ResponseDocModel =
        docsApi.sendDoc(filePart)
}