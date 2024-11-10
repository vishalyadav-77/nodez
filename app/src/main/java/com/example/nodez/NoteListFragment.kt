package com.example.nodez

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn

class NoteListFragment : Fragment(){
    private lateinit var rvNotes: RecyclerView
    private lateinit var btnAddNote: Button
    private lateinit var profileName: TextView
    private lateinit var emptyNote: ImageView
    private val notes = mutableListOf<Note>()
    private lateinit var adapter: NotesAdapter
    private lateinit var dbHelper: NotesDatabaseHelper
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notes_list, container, false)
        rvNotes = view.findViewById(R.id.rvNotes)
        btnAddNote = view.findViewById(R.id.btnAddNote)
        profileName= view.findViewById(R.id.profileName)
        emptyNote= view.findViewById(R.id.floatingImage)


        dbHelper = NotesDatabaseHelper(requireContext())

        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
        val sharedPref = activity?.getSharedPreferences("UserPrefs", AppCompatActivity.MODE_PRIVATE)
        userId = sharedPref?.getString("USER_ID", "default_user_id") ?: "default_user_id"
        Log.d("NoteListFragment", "User ID: $userId")

        profileName.setOnLongClickListener {
            (activity as? MainActivity)?.logOutUser()
            Toast.makeText(requireContext(),"Logged out successfully!", Toast.LENGTH_SHORT).show()// Safely casting to MainActivity
            true
        }

        // Initialize the adapter with click and long-click listeners
        adapter = NotesAdapter(
            notes,
            onItemClick = { note ->
                // Edit the note
                (activity as MainActivity).replaceFragment(AddEditNoteFragment.newInstance(note))
            },
            onItemLongClick = { note ->
                // Delete the note
                deleteNoteFromList(note)
            }
        )

        rvNotes.layoutManager = LinearLayoutManager(requireContext())
        rvNotes.adapter = adapter

        // Fetch and set the profile name from SharedPreferences
        loadProfileName()

        loadNotesFromDatabase(userId)


        btnAddNote.setOnClickListener {
            (activity as MainActivity).replaceFragment(AddEditNoteFragment())
        }

        return view
    }
    private fun loadProfileName() {
        var userName = activity?.intent?.getStringExtra("USER_NAME")
        // If username is not found in Intent, fall back to SharedPreferences
        if (userName.isNullOrEmpty()) {
            val sharedPref = activity?.getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE)
            userName = sharedPref?.getString("USER_NAME", null)
        }
        // Set the profileName if the username is found
        profileName.text = userName ?: "Default User"
    }
    private fun loadNotesFromDatabase(userId: String) {
        notes.clear()
        val savedNotes = dbHelper.getAllNotesForUser(userId)
        notes.addAll(savedNotes)
        adapter.notifyDataSetChanged()
        toggleEmptyNoteVisibility()
    }
    private fun toggleEmptyNoteVisibility() {
        if (notes.isEmpty()) {
            emptyNote.visibility = View.VISIBLE  // Show the floating ImageView
        } else {
            emptyNote.visibility = View.GONE  // Hide the floating ImageView
        }
    }

    fun addOrUpdateNote(note: Note) {
        val existingNote = notes.find { it.id == note.id }
        if (existingNote != null) {
            existingNote.content = note.content
            existingNote.content2 = note.content2
            dbHelper.updateNote(note, userId)
        } else {
            val newNoteId = dbHelper.addNote(note, userId).toInt()
            note.id = newNoteId
            Log.d("NoteList", "Adding new note for user: $userId")
            notes.add(note)
        }
        adapter.notifyDataSetChanged()
        toggleEmptyNoteVisibility()
    }
    private fun deleteNoteFromList(note: Note) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Yes"){ _, _ ->
                notes.remove(note)
                dbHelper.deleteNote(note.id, userId)
                adapter.notifyDataSetChanged()
                toggleEmptyNoteVisibility()
                Toast.makeText(requireContext(), "Note deleted", Toast.LENGTH_SHORT).show()

            }
            .setNegativeButton("Cancel"){ dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    companion object {
        fun newInstance(): NoteListFragment {
            return NoteListFragment()
        }
    }
}