package com.dggorbachev.voicenotes.data.remote

import com.dggorbachev.voicenotes.base.SingleLiveEvent
import com.dggorbachev.voicenotes.data.remote.model.ResponseDocModel
import com.dggorbachev.voicenotes.domain.AsyncState
import java.io.File

interface DocsRemoteRepo {
    val sentDocState: SingleLiveEvent<AsyncState<ResponseDocModel>>

    suspend fun sendDoc(file: File)
}