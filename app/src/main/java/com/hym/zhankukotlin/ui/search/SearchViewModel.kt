package com.hym.zhankukotlin.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {
    private val _word = MutableLiveData<String>("")
    val word: LiveData<String> = _word

    fun setWord(word: String) {
        val word = word.trim()
        if (_word.value == word) return
        _word.value = word
    }
}