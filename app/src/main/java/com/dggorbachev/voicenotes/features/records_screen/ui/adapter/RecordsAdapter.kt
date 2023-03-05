package com.dggorbachev.voicenotes.features.records_screen.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dggorbachev.voicenotes.R
import com.dggorbachev.voicenotes.base.Utils
import com.dggorbachev.voicenotes.base.Utils.bytesEqualTo
import com.dggorbachev.voicenotes.domain.model.Note
import com.dggorbachev.voicenotes.domain.model.PlaybackMode
import java.io.File
import java.util.*


class RecordsAdapter(
    private var notes: List<Note>,
    private val onPlayClick: (file: File, playBackMode: PlaybackMode, position: Int) -> Unit,
    private val onSaveCLick: (file: File, position: Int) -> Unit,
) :
    RecyclerView.Adapter<RecordsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val desc: TextView
        val time: TextView
        val bPlay: ImageButton
        val progressBar: SeekBar
        val bLoad: ImageButton
        val progressBarLoad: ProgressBar

        init {
            this.title = view.findViewById(R.id.tvTitle)
            this.desc = view.findViewById(R.id.tvDesc)
            this.time = view.findViewById(R.id.tvTime)
            this.bPlay = view.findViewById(R.id.bPlay)
            this.progressBar = view.findViewById(R.id.progressBar)
            this.bLoad = view.findViewById(R.id.bLoad)
            this.progressBarLoad = view.findViewById(R.id.progressBarLoad)
        }
    }

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notes[position]
        // Show without .3gp
        holder.title.text = note.file.name.substring(0, note.file.name.length - 4)
        holder.desc.text = Utils.getStringByDate(holder.bPlay.resources, note.file.lastModified())
        bindPlayButton(holder.bPlay.context, note.playbackMode, holder, note.file, position)
        bindLoad(note, holder, position)

        holder.progressBar.isEnabled = false
        if (note.progress > 0F) {
            holder.progressBar.visibility = View.VISIBLE
            bindVoiceTime(note.progress, holder.bPlay.context, holder, note.file)
        } else {
            bindVoiceTime(holder.bPlay.context, holder, note.file)
        }
        holder.progressBar.progress = (note.progress * 100).toInt()
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(notes: List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    fun updateNote(note: Note, position: Int, isLoadingOnServer: Boolean): Boolean {
        // If the user starts to upload a note before the old note is uploaded
        if (note.isLoading && notes.find { it.isLoading } != null) {
            return false
        }
        val new = notes.toMutableList()

        if (isLoadingOnServer) {
            new[position] =
                Note(note.file, new[position].progress, new[position].playbackMode, note.isLoading)
        } else {
            new[position] = note
        }

        notes = new
        notifyItemChanged(position)
        return true
    }

    private fun bindVoiceTime(progress: Float, context: Context, holder: ViewHolder, file: File) {
        val path =
            context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)!!.absolutePath + "/" + file.name
        val uri: Uri = Uri.parse(path)
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context, uri)
        val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val millis = durationStr!!.toInt()

        val calDuration = Calendar.getInstance()
        calDuration.timeInMillis = millis.toLong()

        val calProgress = Calendar.getInstance()
        calProgress.timeInMillis = (calDuration.timeInMillis * progress).toLong()

        holder.time.text = holder.bPlay.resources.getString(
            R.string.time_progress_duration,
            calProgress.get(Calendar.MINUTE),
            calProgress.get(Calendar.SECOND),
            calDuration.get(Calendar.MINUTE),
            calDuration.get(Calendar.SECOND)
        )
    }

    private fun bindVoiceTime(context: Context, holder: ViewHolder, file: File) {
        val path =
            context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)!!.absolutePath + "/" + file.name

        val uri: Uri = Uri.parse(path)
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context, uri)
        val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        if (durationStr != null) {
            val millis = durationStr.toInt()

            val cal = Calendar.getInstance()
            cal.timeInMillis = millis.toLong()
            holder.time.text = holder.bPlay.resources.getString(
                R.string.time_clock,
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND)
            )
        }
    }

    private fun bindLoad(note: Note, holder: ViewHolder, position: Int) {
        if (note.isLoading) {
            holder.bLoad.visibility = View.GONE
            holder.progressBarLoad.visibility = View.VISIBLE
        } else {
            holder.bLoad.visibility = View.VISIBLE
            holder.progressBarLoad.visibility = View.GONE
            holder.bLoad.setOnClickListener {
                onSaveCLick(note.file, position)
            }
        }
    }

    private fun bindPlayButton(
        context: Context,
        playBackMode: PlaybackMode,
        holder: ViewHolder,
        file: File,
        position: Int,
    ) {
        if (playBackMode == PlaybackMode.PLAY) {
            holder.bPlay.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_play
                )
            )
        } else {
            holder.bPlay.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_pause
                )
            )
        }

        holder.bPlay.setOnClickListener {
            if (holder.bPlay.drawable.bytesEqualTo(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_play
                    )
                )
            ) {
                onPlayClick(file, PlaybackMode.PLAY, position)
            } else if (holder.bPlay.drawable.bytesEqualTo(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_pause
                    )
                )
            ) {
                onPlayClick(file, PlaybackMode.STOP, position)
            }
        }
    }
}