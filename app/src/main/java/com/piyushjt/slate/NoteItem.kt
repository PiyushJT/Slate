package com.piyushjt.slate

data class NoteItem(
                    val noteTitle : String,
                    val note : String,
                    val noteKey : String) {
    constructor() : this("", "", "")
}