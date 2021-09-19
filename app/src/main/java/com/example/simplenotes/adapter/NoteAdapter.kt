package com.example.simplenotes.adapter

import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.simplenotes.databinding.NotesDisplayStaggeredBinding
import androidx.recyclerview.widget.DiffUtil
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.simplenotes.model.Note

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(val itemBinding: NotesDisplayStaggeredBinding) :
        RecyclerView.ViewHolder(itemBinding.root)


    private val diffCallBack = object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.noteTitle == newItem.noteTitle &&
                    oldItem.noteDescription == newItem.noteDescription
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            NotesDisplayStaggeredBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currNote = differ.currentList[position]

        holder.itemBinding.displayNoteTitle.text = currNote.noteTitle
        holder.itemBinding.displayNoteDescription.text = currNote.noteDescription
        holder.itemBinding.displayImageView.load(currNote.imageFilePath) {
            transformations(RoundedCornersTransformation(32f))
        }
        holder.itemBinding.timeStampTextView.text = currNote.timeStamp

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}