package com.piyushjt.slate

data class NoteItem(
                    val noteTitle : String,
                    val note : String,
                    val noteKey : String,
                    val noteColor: String) {
    constructor() : this("", "", "", "")
}