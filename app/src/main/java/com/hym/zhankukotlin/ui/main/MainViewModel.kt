package com.hym.zhankukotlin.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val _word = MutableLiveData<String>("")
    val word: LiveData<String> = _word

    fun setSearchWord(word: String) {
        val word = word.trim()
        if (_word.value == word) return
        _word.value = word
    }
}