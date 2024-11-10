package com.example.nodez

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter(private val notes: List<Note>,
                   private val onItemClick: (Note) -> Unit,
                   private val onItemLongClick: (Note) -> Unit
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {inner class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val textView: TextView = view.findViewById(R.id.tvNote)
    private val noteContent: TextView = view.findViewById(R.id.tvNoteContent)

    fun bind(note: Note) {
        textView.text = note.content
        noteContent.text = note.content2
        // Set up the click listener for editing
        itemView.setOnClickListener { onItemClick(note) }
        // Set up the long-click listener for deleting
        itemView.setOnLongClickListener {
            onItemLongClick(note)
            true
        }
    }
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int = notes.size
}