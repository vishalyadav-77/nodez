package com.example.nodez

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment

class AddEditNoteFragment: Fragment() {
    private lateinit var etNote: EditText
    private lateinit var etNoteContent: EditText
    private lateinit var btnSave: Button
    private var note: Note? = null

    companion object {
        fun newInstance(note: Note? = null): AddEditNoteFragment {
            val fragment = AddEditNoteFragment()
            fragment.note = note
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.frag_add_edit_note, container, false)
        etNote = view.findViewById(R.id.etNote)
        etNoteContent = view.findViewById(R.id.etNoteContent)
        btnSave = view.findViewById(R.id.btnSave)

        note?.let {
            etNote.setText(it.content)
            etNoteContent.setText(it.content2)
        }

        btnSave.setOnClickListener {
            val content = etNote.text.toString()
            val content2 = etNoteContent.text.toString()
            if (content.isNotBlank()) {
                val newNote = note ?: Note(0, content, content2)
                newNote.content = content
                newNote.content2 = content2
                (activity as MainActivity).saveNoteToList(newNote)
            }
        }
        return view
    }
}