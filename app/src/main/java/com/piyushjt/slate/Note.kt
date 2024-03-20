package com.piyushjt.slate

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.piyushjt.slate.databinding.ActivityNoteBinding

class Note : AppCompatActivity() {

    private lateinit var binding: ActivityNoteBinding
    private lateinit var auth: FirebaseAuth
    private var noteID = "null"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()



        // Limiting length of note Title
        Utils.textLimiter(binding.noteTitle, binding.noteTitleRule, 100)

        binding.note.setOnClickListener {
            binding.noteTitleRule.visibility = View.INVISIBLE
        }



        // Getting data about the note
        val intent = intent
        noteID = intent.getStringExtra("noteId").toString()
        val noteTitle= intent.getStringExtra("title")
        val note= intent.getStringExtra("note")


        // Setting the data to views
        binding.noteTitle.setText(noteTitle)
        binding.note.setText(note)




        // Saving the note
        binding.saveNoteBtn.setOnClickListener {

            val viewNoteTitle = binding.noteTitle.text.toString()
            val viewNote = binding.note.text.toString()



            // Only save if both are filled
            if (viewNoteTitle.isNotEmpty() || viewNote.isNotEmpty()) {


                // If the note is newly created -> upload
                if (noteID == "null") {

                    uploadNote(viewNoteTitle, viewNote)

                }

                // If the note is already created -> update
                else{

                    updateNote(viewNoteTitle, viewNote, noteID)

                }


            }
            else {
                Toast.makeText(this, getString(R.string.fild_cnnt_empty),
                    Toast.LENGTH_LONG).show()
            }

        }

    }


    // Function to Upload a new note
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


        // Saving the note ID for updating note in same activity
        noteID = noteKey

    }



    // Function to update an existing note
    private fun updateNote(noteTitle: String, note: String, noteID: String){

        // Initializing Realtime Database
        val database = Firebase.database(getString(R.string.database_url))
        val userID = auth.currentUser?.uid
        val noteRef = database.getReference(userID.toString())
            .child("allNotes").child(noteID)



        // Updating the note
        val newNote = NoteItem(noteTitle, note, noteID)

        noteRef.setValue(newNote)
            .addOnSuccessListener {
                Toast.makeText(
                    this, getString(R.string.succ_updtd_note),
                    Toast.LENGTH_SHORT
                ).show()
            }

    }


}