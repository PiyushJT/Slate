package com.piyushjt.slate

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.piyushjt.slate.databinding.ActivityNoteBinding

class Note : AppCompatActivity() {

    private lateinit var binding: ActivityNoteBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()



        // Saving the note
        binding.saveNoteBtn.setOnClickListener {

            val noteTitle = binding.noteTitle.text.toString()
            val note = binding.note.text.toString()



            // Only upload if both are filled
            if (noteTitle.isNotEmpty() || note.isNotEmpty()) {
                uploadNote(noteTitle, note)
            }
            else {
                Toast.makeText(this, getString(R.string.fild_cnnt_empty),
                    Toast.LENGTH_LONG).show()
            }

        }

    }


    // Uploading a new note
    private fun uploadNote(noteTitle: String, note: String){

        // Initializing Realtime Database
        val database = Firebase.database(getString(R.string.database_url))
        val userID = auth.currentUser?.uid
        val noteRef = database.getReference(userID.toString()).child("allNotes")



        // Getting the note key
        val noteKey = noteRef.push()
            .key


        // Defining the whole note
        val noteItem = NoteItem(noteTitle, note, noteKey!!)


        // Uploading the note
        noteRef.child(noteKey).setValue(noteItem)

    }


}