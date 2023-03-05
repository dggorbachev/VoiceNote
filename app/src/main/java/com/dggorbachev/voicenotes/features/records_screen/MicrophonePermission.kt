package com.dggorbachev.voicenotes.features.records_screen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dggorbachev.voicenotes.features.records_screen.ui.view.RecordsFragment

object MicrophonePermission {
    private fun isMicrophonePresent(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
    }

    fun checkMicrophonePermission(activity: Activity): Boolean {
        if (isMicrophonePresent(activity)) {
            if (ContextCompat.checkSelfPermission(
                    activity, Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_DENIED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    listOf(Manifest.permission.RECORD_AUDIO).toTypedArray(),
                    RecordsFragment.PERMISSION_CODE
                )
                return false
            }
            return true
        }
        return false
    }
}