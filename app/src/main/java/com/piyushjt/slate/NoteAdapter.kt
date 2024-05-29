package com.piyushjt.slate

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.piyushjt.slate.databinding.NotesItemBinding

class NoteAdapter(
    private val notes: List<NoteItem>,
    private val itemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    interface OnItemClickListener {

        // Initializing function to set click listener
        fun onClick(noteTitle: String, note: String, noteKey: String, noteColor: String)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {

        val binding = NotesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return NoteViewHolder(binding)

    }


    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note)


        // Click listener on a note
        holder.binding.background.setOnClickListener{

            itemClickListener.onClick(note.noteTitle, note.note, note.noteKey, note.noteColor)

        }

    }

    override fun getItemCount(): Int {
        return notes.size
    }

    class NoteViewHolder(val binding: NotesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: NoteItem) {


            // Setting random bg colors for notes
            if(note.noteColor.isEmpty()) {

                binding.background.setBackgroundTintList(
                    ColorStateList.valueOf(
                        Color.parseColor(Utils.getRandomColor())
                    )
                )
            }
            // Setting bg colors for notes
            else {
                binding.background.setBackgroundTintList(
                    ColorStateList.valueOf(
                        Color.parseColor(note.noteColor)
                    )
                )
            }



            // Showing actual note when note title is not available
            if (note.noteTitle.isNotEmpty()) {

                binding.noteTitleHm.text = note.noteTitle

            } else {

                // Showing first 90 characters of note
                val noteText = if (note.note.length > 90) {
                    note.note.substring(0, 97) + "..."
                } else {
                    note.note
                }
                binding.noteTitleHm.text = noteText
            }


        }

    }
}