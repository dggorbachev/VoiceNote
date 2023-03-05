package com.dggorbachev.voicenotes.features.records_screen.ui.view

import android.media.MediaPlayer
import android.os.Environment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.dggorbachev.voicenotes.base.HostSelectionInterceptor
import com.dggorbachev.voicenotes.databinding.FragmentRecordsBinding
import com.dggorbachev.voicenotes.domain.model.Note
import com.dggorbachev.voicenotes.domain.model.PlaybackMode
import com.dggorbachev.voicenotes.features.records_screen.stateholders.NotesViewModel
import com.dggorbachev.voicenotes.features.records_screen.ui.adapter.RecordsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class PlayerViewController(
    private val binding: FragmentRecordsBinding,
    private val lifecycleScope: CoroutineScope,
    private val activity: FragmentActivity,
    private val viewModel: NotesViewModel,
    private val hostSelectionInterceptor: HostSelectionInterceptor,
    private val viewLifecycleOwner: LifecycleOwner,
) {

    private var isMediaPlaying = false
    private var mediaPlayer: MediaPlayer? = null
    private var previousProgress = 0F
    private var previousPosition = 0
    private var previousFile: File? = null

    private val savingDocVkViewController: SavingDocVkViewController by lazy {
        SavingDocVkViewController(
            activity,
            viewModel,
            viewLifecycleOwner,
            lifecycleScope,
            hostSelectionInterceptor,
            notesAdapter
        )
    }

    private val notesAdapter by lazy {
        RecordsAdapter(
            listOf(),
            onPlayClick = { file, playBackMode, position ->
                when (playBackMode) {
                    PlaybackMode.PLAY -> {
                        playVoiceNote(file, position)
                    }
                    PlaybackMode.STOP -> {
                        pauseVoiceNote(file, position)
                    }
                }
            },
            onSaveCLick = { file, position ->
                savingDocVkViewController.saveInVk(file, position)
            }
        )
    }

    fun setUpPlayerView() {
        binding.rvNotes.adapter = notesAdapter
        binding.rvNotes.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }

    suspend fun bindNotes() {
        val path =
            activity.getExternalFilesDir(Environment.DIRECTORY_MUSIC)!!.absolutePath
        val files = viewModel.getFiles(previousFile, previousProgress, path)
        if (files != null) {
            notesAdapter.updateList(files)
        }
    }

    private fun playVoiceNote(file: File, position: Int) {
        lifecycleScope.launch {
            // if current audio not equals previous – clear previous
            if (file != previousFile) {
                previousFile = null
                previousPosition = 0
            }

            // reset all notes
            bindNotes()

            if (isMediaPlaying) {
                // reset all variables + stop mediaPlayer and start another file
                stopVoiceNote(file, position)
            } else {
                // start new file or seekTo previous position
                startVoiceNote(file, position)
            }
        }
    }

    private fun pauseVoiceNote(file: File, position: Int) {
        isMediaPlaying = false
        mediaPlayer?.apply {
            previousProgress = currentPosition / duration.toFloat()
            previousPosition = currentPosition
            previousFile = file

            stop()
            reset()
            release()
        }

        mediaPlayer = null

        // if not completed – show progress
        if (previousProgress != 1F) {
            notesAdapter.updateNote(
                Note(file, previousProgress, PlaybackMode.PLAY, false),
                position, false
            )
        }
    }

    private fun stopVoiceNote(file: File, position: Int) {
        isMediaPlaying = false
        mediaPlayer?.apply {
            stop()
            reset()
            release()
        }

        previousFile = null
        previousPosition = 0
        mediaPlayer = null

        startVoiceNote(file, position)
    }

    private fun startVoiceNote(file: File, position: Int) {
        isMediaPlaying = true
        mediaPlayer = MediaPlayer().apply {
            setDataSource(file.absolutePath)

            try {
                prepare()
            } catch (_: IOException) {
            }

            start()
            if (previousFile != null && previousFile == file)
                seekTo(previousPosition)

            lifecycleScope.launch {
                try {
                    while (isPlaying) {
                        delay(70)
                        previousProgress = currentPosition / duration.toFloat()
                        notesAdapter.updateNote(
                            Note(
                                file,
                                currentPosition / duration.toFloat(),
                                PlaybackMode.STOP,
                                false
                            ),
                            position, false
                        )
                    }
                    // when the audio ends – reset the note listening parameters
                    if (!isPlaying) {
                        previousFile = null
                        previousPosition = 0
                        notesAdapter.updateNote(
                            Note(file, 0F, PlaybackMode.PLAY, false),
                            position, false
                        )
                    }
                } catch (_: java.lang.Exception) {
                    // Playing stop by pause
                }
            }
        }
    }

    fun stopVoiceNote() {
        isMediaPlaying = false
        mediaPlayer?.apply {
            stop()
            reset()
            release()
        }

        previousFile = null
        previousPosition = 0
        mediaPlayer = null

        lifecycleScope.launch {
            bindNotes()
        }
    }
}