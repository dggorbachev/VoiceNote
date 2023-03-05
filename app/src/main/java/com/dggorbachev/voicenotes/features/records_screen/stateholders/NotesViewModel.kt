package com.dggorbachev.voicenotes.features.records_screen.stateholders

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.dggorbachev.voicenotes.data.remote.DocsRemoteRepo
import com.dggorbachev.voicenotes.domain.model.Note
import com.dggorbachev.voicenotes.domain.model.PlaybackMode
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class NotesViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val docsRemoteRepo: DocsRemoteRepo,
) : ViewModel() {

    val loadedDocState = docsRemoteRepo.sentDocState

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle): NotesViewModel
    }

    suspend fun getFiles(
        previousFile: File?,
        previousProgress: Float,
        path: String,
    ): ArrayList<Note>? =
        withContext(Dispatchers.Default) {
            val file = File(path)
            val allFiles = file.listFiles()
            if (allFiles != null) {
                val audioFiles = allFiles.filter { it.name.endsWith(".3gp") }
                    .sortedByDescending { it.lastModified() }.toTypedArray()

                val noteList = ArrayList<Note>()
                for (audioFile in audioFiles) {
                    // when reset – no reset previous
                    if (previousFile == audioFile)
                        noteList.add(Note(audioFile, previousProgress, PlaybackMode.PLAY, false))
                    else
                        noteList.add(Note(audioFile, 0F, PlaybackMode.PLAY, false))
                }
                return@withContext noteList
            }
            return@withContext null
        }

    suspend fun sendDoc(file: File) {
        docsRemoteRepo.sendDoc(file)
    }
}