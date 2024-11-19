package com.example.omgapplication.ui.panel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewReportViewModel : ViewModel() {


    private val _text = MutableLiveData<String>().apply {
        value = "Ver Relatos da Ong"
    }
    val text: LiveData<String> = _text
}