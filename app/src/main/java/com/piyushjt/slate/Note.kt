package com.piyushjt.slate

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.piyushjt.slate.databinding.ActivityNoteBinding

class Note : AppCompatActivity() {

    private lateinit var binding: ActivityNoteBinding
    private lateinit var auth: FirebaseAuth
    private var noteID = "null"
    private lateinit var noteColor: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Changing the theme color
        val themeViews = listOf(binding.saveNoteBtn, binding.header, binding.colorBtn)


        binding.colorBtn.setOnClickListener {
            runColorPicker(themeViews)

        }


        auth = FirebaseAuth.getInstance()



        // Limiting length of note Title
        Utils.textLimiter(binding.noteTitle, binding.noteTitleRule, 100)



        // Getting data about the note
        val intent = intent
        noteID = intent.getStringExtra("noteId").toString()
        val noteTitle = intent.getStringExtra("title")
        val note = intent.getStringExtra("note")
        val noteColorTemp = intent.getStringExtra("noteColor")


        // Setting the note color
        noteColor = if (noteColorTemp.isNullOrEmpty()) {
            Utils.getRandomColor()

        } else {
            noteColorTemp
        }

        changeBgTint(themeViews, noteColor)


        // Setting the data to views
        binding.noteTitle.setText(noteTitle)
        binding.note.setText(note)



        // Deleting the note
        binding.deleteNoteBtn.setOnClickListener {

            // If note is saved
            if (noteID != "null") {

                val head = getString(R.string.conf_delete)
                val deleteTxt = getString(R.string.delete)

                Utils.showDialog(this, head, deleteTxt) { deleteNote(noteID) }
            }

            // If note is not saved
            else {
                finish()
            }

        }



        // Saving the note
        binding.saveNoteBtn.setOnClickListener {
            saveNote()
        }
    }



    // Function to save the note
    private fun saveNote() {
        val viewNoteTitle = binding.noteTitle.text.toString()
        val viewNote = binding.note.text.toString()



        // Only save if both are filled
        if (viewNoteTitle.isNotEmpty() || viewNote.isNotEmpty()) {


            // If the note is newly created -> upload
            if (noteID == "null") {

                uploadNote(viewNoteTitle, viewNote)

            }

            // If the note is already created -> update
            else {

                updateNote(viewNoteTitle, viewNote, noteID)

            }


        }
        else {
            Toast.makeText(
                this, getString(R.string.fild_cnnt_empty),
                Toast.LENGTH_LONG
            ).show()
        }

    }



    // Function to Upload a new note
    private fun uploadNote(noteTitle: String, note: String) {

        // Initializing Realtime Database
        val database = Firebase.database(getString(R.string.database_url))
        val userID = auth.currentUser?.uid
        val noteRef = database.getReference(userID.toString()).child("allNotes")



        // Getting the note key
        val noteKey = noteRef.push()
            .key


        // Defining the whole note
        val noteItem = NoteItem(noteTitle, note, noteKey!!, noteColor)


        // Uploading the note
        noteRef.child(noteKey).setValue(noteItem)


        // Saving the note ID for updating note in same activity
        noteID = noteKey

    }



    // Function to update an existing note
    private fun updateNote(noteTitle: String, note: String, noteID: String) {

        // Initializing Realtime Database
        val database = Firebase.database(getString(R.string.database_url))
        val userID = auth.currentUser?.uid
        val noteRef = database.getReference(userID.toString())
            .child("allNotes").child(noteID)



        // Updating the note
        val newNote = NoteItem(noteTitle, note, noteID, noteColor)

        noteRef.setValue(newNote)
            .addOnSuccessListener {
                Toast.makeText(
                    this, getString(R.string.succ_updtd_note),
                    Toast.LENGTH_SHORT
                ).show()
            }

    }



    // Function to delete the note
    private fun deleteNote(noteID: String) {

        // Initializing Realtime Database
        val database = Firebase.database(getString(R.string.database_url))
        val userID = auth.currentUser?.uid
        val noteRef = database.getReference(userID.toString())
            .child("allNotes").child(noteID)


        // Deleting the note
        noteRef.removeValue()
        finish()

    }



    // Function to run the color picker
    private fun runColorPicker(themeViews: List<View>) {

        // Generate dialog box
        val dialog = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(true)
            setContentView(R.layout.color_picker)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }


        // Get buttons
        val buttons = listOf<Button>(
            dialog.findViewById(R.id.aqua),
            dialog.findViewById(R.id.yellow),
            dialog.findViewById(R.id.green),
            dialog.findViewById(R.id.purple),
            dialog.findViewById(R.id.pink)
        )

        // Set button click listeners
        buttons.forEach { button ->
            button.setOnClickListener {
                // Set note color
                noteColor = when (button.id) {
                    R.id.aqua -> "#9EFFFF"
                    R.id.yellow -> "#FFF599"
                    R.id.green -> "#91F48F"
                    R.id.purple -> "#B69CFF"
                    R.id.pink -> "#FF9E9E"
                    else -> throw IllegalArgumentException("Invalid button ID")
                }

                // Change background tint
                changeBgTint(themeViews, noteColor)

                // Save note
                saveNote()

                // Dismiss dialog
                dialog.dismiss()
            }
        }

        // Show dialog
        dialog.show()
    }



    // Function to change the background tint
    private fun changeBgTint(views: List<View>, noteColor: String) {

        // Change background tint
        Utils.changeBgTint(views, noteColor)


        // Change status bar color
        val colors = mapOf(
            "#FF9E9E" to R.color.notePink,
            "#91F48F" to R.color.noteGreen,
            "#FFF599" to R.color.noteYellow,
            "#9EFFFF" to R.color.noteAqua,
            "#B69CFF" to R.color.notePurple
        )
        window.statusBarColor = ContextCompat.getColor(this, colors[noteColor]!!)
    }


}