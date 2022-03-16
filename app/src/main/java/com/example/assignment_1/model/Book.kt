package com.example.assignment_1.model

import com.google.firebase.firestore.Exclude
import java.io.Serializable
import java.util.*


data class Book(
    @Exclude var id: String?,
    var name: String = "",
    var author: String = "",
    var year: Date? = null,
    var rates: Float = 0.0f,
    var price: Int = 0,
    var image: String = ""
) : Serializable

