package com.dggorbachev.voicenotes.features.records_screen.ui.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.dggorbachev.voicenotes.R
import com.dggorbachev.voicenotes.base.HostSelectionInterceptor
import com.dggorbachev.voicenotes.domain.AsyncState
import com.dggorbachev.voicenotes.domain.model.Note
import com.dggorbachev.voicenotes.domain.model.PlaybackMode
import com.dggorbachev.voicenotes.features.records_screen.stateholders.NotesViewModel
import com.dggorbachev.voicenotes.features.records_screen.ui.adapter.RecordsAdapter
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.sdk.api.base.dto.BaseUploadServer
import com.vk.sdk.api.docs.DocsService
import com.vk.sdk.api.docs.dto.DocsSaveResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

class SavingDocVkViewController(
    private val activity: FragmentActivity,
    private val viewModel: NotesViewModel,
    private val viewLifecycleOwner: LifecycleOwner,
    private val lifecycleScope: CoroutineScope,
    private val hostSelectionInterceptor: HostSelectionInterceptor,
    private val notesAdapter: RecordsAdapter,
) {

    fun saveInVk(file: File, position: Int) {
        val updateNote = updateNote(file, position, true)
        if (updateNote) {
            observeDocState(file, position)
            sendDocToServer(file, position)
        } else {
            showToast(activity.resources.getString(R.string.file_uploaded))
        }
    }

    private fun observeDocState(file: File, position: Int) {
        viewModel.loadedDocState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AsyncState.Loading -> {
                }
                is AsyncState.Loaded -> {
                    VK.execute(
                        DocsService().docsSave(state.data.file),
                        object : VKApiCallback<DocsSaveResponse> {
                            override fun fail(error: Exception) {
                                showToast(activity.resources.getString(R.string.save_exception))
                                updateNote(file, position, false)
                            }

                            override fun success(result: DocsSaveResponse) {
                                val clipboard: ClipboardManager =
                                    activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip: ClipData =
                                    ClipData.newPlainText("Document link", result.doc?.url)
                                clipboard.setPrimaryClip(clip)
                                showToast(activity.resources.getString(R.string.save_success))
                                updateNote(file, position, false)

                                viewModel.loadedDocState.removeObservers(viewLifecycleOwner)
                            }
                        }
                    )
                }
                is AsyncState.Error -> {
                    showToast(activity.resources.getString(R.string.save_exception))
                    updateNote(file, position, false)
                }
            }
        }
    }

    private fun sendDocToServer(file: File, position: Int) {
        val recordPath =
            activity.getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.absolutePath

        VK.execute(
            DocsService().docsGetUploadServer(), object : VKApiCallback<BaseUploadServer> {
                override fun fail(error: Exception) {
                    Toast.makeText(
                        activity,
                        activity.resources.getString(R.string.server_exception),
                        Toast.LENGTH_LONG
                    ).show()
                    updateNote(file, position, false)
                }

                override fun success(result: BaseUploadServer) {
                    lifecycleScope.launch {
                        hostSelectionInterceptor.setHostBaseUrl(result.uploadUrl)
                        viewModel.sendDoc(File("$recordPath/${file.name}"))
                    }
                }
            }
        )
    }

    private fun updateNote(file: File, position: Int, isLoading: Boolean): Boolean {
        return notesAdapter.updateNote(
            Note(
                file,
                0F,
                PlaybackMode.PLAY,
                isLoading
            ),
            position,
            true
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(
            activity,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}