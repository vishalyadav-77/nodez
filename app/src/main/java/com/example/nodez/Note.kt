package com.example.nodez
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Note(var id: Int, var content: String, var content2: String)

class NotesDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "notes.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_NAME = "notes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_CONTENT = "content"
        private const val COLUMN_CONTENT2 = "content2"
        private const val COLUMN_USER_ID = "userId" // New column
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CONTENT TEXT,
                $COLUMN_CONTENT2 TEXT,
                $COLUMN_USER_ID TEXT
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_USER_ID TEXT")
        }
    }


    // Insert a new note
    fun addNote(note: Note, userId: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CONTENT, note.content)
            put(COLUMN_CONTENT2, note.content2)
            put(COLUMN_USER_ID, userId)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    // Get all notes
    fun getAllNotesForUser(userId: String): MutableList<Note> {
        val notes = mutableListOf<Note>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_USER_ID = ?", arrayOf(userId))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
                val content2 = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT2))
                notes.add(Note(id, content, content2))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return notes
    }

    // Update a note
    fun updateNote(note: Note, userId: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CONTENT, note.content)
            put(COLUMN_CONTENT2, note.content2)
            put(COLUMN_USER_ID, userId)
        }
        return db.update(TABLE_NAME, values, "$COLUMN_ID=? AND $COLUMN_USER_ID=?", arrayOf(note.id.toString(), userId))
    }

    // Delete a note for a user
    fun deleteNote(noteId: Int,userId: String): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID=? AND $COLUMN_USER_ID=?", arrayOf(noteId.toString(), userId))
    }
}