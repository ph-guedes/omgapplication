package com.example.omgapplication.entities

data class Animal(
    val id: String? = null,
    val name: String = "",
    val age: String = "",
    val gender: String = "",
    val type: String = "",
    var photo: String = "",
    val descriptions: List<String> = emptyList()
)
