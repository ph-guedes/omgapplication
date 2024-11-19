package com.example.omgapplication.ui.panel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddAnimalViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Adicionar animal na Ong"
    }
    val text: LiveData<String> = _text
}