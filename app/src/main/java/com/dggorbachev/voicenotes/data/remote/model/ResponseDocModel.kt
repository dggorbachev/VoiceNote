package com.dggorbachev.voicenotes.data.remote.model

import com.google.gson.annotations.SerializedName

data class ResponseDocModel(
    @SerializedName("file")
    val file: String,
)