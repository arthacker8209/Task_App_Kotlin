package com.example.my_task_app.util

import androidx.appcompat.widget.SearchView

inline fun SearchView.OnQueryTextChanged(crossinline listner: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            listner(newText.orEmpty())
            return true
        }
    })
}