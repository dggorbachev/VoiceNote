package com.dggorbachev.voicenotes.domain.model

import java.io.File

data class Note(
    val file: File,
    val progress: Float,
    val playbackMode: PlaybackMode,
    val isLoading : Boolean,
)