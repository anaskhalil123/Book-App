package com.example.assignment1.model

import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class Book(
    @Exclude var id : String?,
    var name: String,
    var author: String,
    var year: Int,
    var rates: Float,
    var price: Int
) : Serializable

