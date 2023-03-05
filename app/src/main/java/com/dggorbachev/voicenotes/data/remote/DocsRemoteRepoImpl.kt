package com.dggorbachev.voicenotes.data.remote

import com.dggorbachev.voicenotes.base.SingleLiveEvent
import com.dggorbachev.voicenotes.data.remote.model.ResponseDocModel
import com.dggorbachev.voicenotes.domain.AsyncState
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject


class DocsRemoteRepoImpl @Inject constructor(
    private val source: DocsRemoteSource,
) : DocsRemoteRepo {
    override val sentDocState: SingleLiveEvent<AsyncState<ResponseDocModel>>
        get() = mutableSentDocState

    private val mutableSentDocState = SingleLiveEvent<AsyncState<ResponseDocModel>>()

    override suspend fun sendDoc(file: File) {
        mutableSentDocState.postValue(AsyncState.Loading)
        try {
            val filePart = MultipartBody.Part.createFormData(
                "file", file.name, file
                    .asRequestBody("audio/*".toMediaTypeOrNull())
            )
            mutableSentDocState.postValue(AsyncState.Loaded(source.sendDoc(filePart)))
        } catch (e: Exception) {
            mutableSentDocState.postValue(AsyncState.Error(e))
        }
    }
}