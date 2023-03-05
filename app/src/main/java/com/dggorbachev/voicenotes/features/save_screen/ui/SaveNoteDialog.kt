package com.dggorbachev.voicenotes.features.save_screen.ui

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dggorbachev.voicenotes.App
import com.dggorbachev.voicenotes.R
import com.dggorbachev.voicenotes.databinding.DialogSaveNoteBinding
import java.io.File

class SaveNoteDialog :
    DialogFragment() {

    private var _binding: DialogSaveNoteBinding? = null
    private val binding get() = _binding!!
    private val args: SaveNoteDialogArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as App).component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogSaveNoteBinding.inflate(inflater, container, false)

        // Set transparent background and no title
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            tvNote.text = args.notePath
            tvCancel.setOnClickListener {
                findNavController().navigate(R.id.moveToRecordsFragment)
            }
            tvSave.setOnClickListener {
                val fileName = textInput.text.toString()
                if (fileName != "" && compareExistingFiles(fileName)) {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.file_exists),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    saveFile(fileName)
                    findNavController().navigate(R.id.moveToRecordsFragment)
                }
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        findNavController().navigate(R.id.moveToRecordsFragment)
    }

    private fun saveFile(fileName: String) {
        if (fileName != "") {
            File(getRecordingSavePath(args.notePath)).let { source ->
                source.copyTo(File(getRecordingSavePath("$fileName.3gp")))
                source.delete()
            }
        }
    }

    private fun compareExistingFiles(fileName: String): Boolean {
        // find 1 file – filename.3gp
        // if user wrote filename.3gp – search for filename.3gp.3gp
        if (File(getRecordingSavePath("$fileName.3gp")).exists() && File(
                getRecordingSavePath(
                    "$fileName.3gp"
                )
            ).isFile
        ) {
            return true
        }
        return false
    }

    private fun getRecordingSavePath(fileName: String): String {
        val recordPath =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.absolutePath
        return "$recordPath/$fileName"
    }
}