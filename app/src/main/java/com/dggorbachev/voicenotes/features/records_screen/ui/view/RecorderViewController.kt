package com.dggorbachev.voicenotes.features.records_screen.ui.view

import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import com.dggorbachev.voicenotes.R
import com.dggorbachev.voicenotes.databinding.FragmentRecordsBinding
import com.dggorbachev.voicenotes.features.records_screen.MicrophonePermission
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RecorderViewController(
    private val activity: FragmentActivity,
    private val binding: FragmentRecordsBinding,
    private val playerViewController: PlayerViewController,
) {

    private var mediaRecorder: MediaRecorder? = null
    private var isRecording: Boolean = false
    private var fileName = ""

    fun setUpRecorderView() {
        bindVoiceButton()
    }

    private fun bindVoiceButton() {
        binding.fabRecord.setOnClickListener {
            if (isRecording) {
                // Stop
                stopRecording()

                binding.fabRecord.setImageDrawable(
                    AppCompatResources.getDrawable(
                        activity,
                        R.drawable.ic_record
                    )
                )

                isRecording = false

            } else {
                // Start
                if (MicrophonePermission.checkMicrophonePermission(activity)) {
                    playerViewController.stopVoiceNote()
                    startRecording()

                    binding.fabRecord.setImageDrawable(
                        AppCompatResources.getDrawable(
                            activity,
                            R.drawable.ic_stop
                        )
                    )

                    isRecording = true
                }
            }
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            try {
                stop()
                reset()
                release()
            } catch (_: java.lang.Exception) {
            }
        }
        mediaRecorder = null


        binding.root.findNavController().navigate(
            RecordsFragmentDirections.moveToSaveNoteDialog(fileName)
        )
        fileName = ""
    }

    private fun startRecording() {
        // Create new MediaRecorder every time user press on start button
        mediaRecorder = createMediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(getRecordingSavePath())
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (_: IOException) {
            }

            start()
        }
    }

    private fun createMediaRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            MediaRecorder(activity)
        else {
            // MediaRecorder with context in constructor requires API >= 31
            MediaRecorder()
        }
    }

    private fun getRecordingSavePath(): String {
        val sdf = SimpleDateFormat("dd_MM_yyyy_hh_mm_ss", Locale("ru"))

        val recordPath =
            activity.getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.absolutePath
        val now = Date()
        val recordFile = "recording_" + sdf.format(now) + ".3gp"
        fileName = recordFile

        return "$recordPath/$recordFile"
    }
}