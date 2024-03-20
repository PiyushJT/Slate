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
        fun onClick(noteTitle: String, note: String, noteKey: String)

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

            itemClickListener.onClick(note.noteTitle, note.note, note.noteKey)

        }

    }

    override fun getItemCount(): Int {
        return notes.size
    }

    class NoteViewHolder(val binding: NotesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: NoteItem) {


            // Setting random bg colors for notes
            val colors = mapOf(
                1 to "#FF9E9E", // Pink
                2 to "#91F48F", // Green
                3 to "#FFF599", // Yellow
                4 to "#9EFFFF", // Aqua
                5 to "#B69CFF"  // Purple
            )

            val randomInt = (1..5).random()

            binding.background.setBackgroundTintList(
                ColorStateList.valueOf(
                    Color.parseColor(colors[randomInt])
                )
            )



            // Showing actual note when note title is not available
            if (note.noteTitle.isNotEmpty()) {

                binding.noteTitleHm.text = note.noteTitle

            } else {

                binding.noteTitleHm.text = note.note

            }


        }

    }
}