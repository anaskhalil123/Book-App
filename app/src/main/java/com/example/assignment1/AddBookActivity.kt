package com.example.assignment1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment1.databinding.ActivityAddBookBinding
import com.example.assignment1.model.Book
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class AddBookActivity : AppCompatActivity() {
    val TAG = "AddBookActivity"
    lateinit var binding: ActivityAddBookBinding
    val db = Firebase.firestore

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.getSerializableExtra("book") != null) {
            /*in edit book case*/
            val myBook = intent.getSerializableExtra("book") as Book

            binding.textView.text = "Edit Book"
            binding.btnAdd.text = "Edit Book"

            binding.edName.setText(myBook.name)
            binding.edAuthor.setText(myBook.author)
            binding.edYear.setText((myBook.year!!.year + 1901).toString())
            binding.edPrice.setText(myBook.price.toString())
            binding.ratingBar2.rating = myBook.rates


            binding.btnAdd.setOnClickListener {

                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val year = binding.edYear.text.toString().toInt()
                val month = 0
                val day = 0
                val date = dateFormat.parse("$year-$month-$day")
                Log.d(TAG, date!!.toString())

                val data = hashMapOf(
                    "name" to binding.edName.text.toString(),
                    "author" to binding.edAuthor.text.toString(),
                    "price" to binding.edPrice.text.toString().toInt(),
                    "rates" to binding.ratingBar2.rating.toString().toFloat(),
                    "year" to Timestamp(date)
                )

                db.collection("books").document(myBook.id!!).update(data as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(this, "book updated successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, exception.message.toString())
                    }
            }

            binding.btnDelete.setOnClickListener {
                db.collection("books").document(myBook.id!!).delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "book deleted successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, exception.message.toString())
                    }
            }

        } else {
            /* in add book case*/
            binding.btnDelete.visibility = View.GONE
            binding.btnAdd.setOnClickListener {
                if (binding.edName.text.isNotEmpty()
                    && binding.edAuthor.text.isNotEmpty()
                    && binding.edYear.text.isNotEmpty()
                    && binding.edPrice.text.isNotEmpty()
                    && binding.ratingBar2.rating != 0.0f
                ) {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                    val year = binding.edYear.text.toString().toInt()
                    val month = 0
                    val day = 0
                    val date = dateFormat.parse("$year-$month-$day")
                    Log.d(TAG, date.toString())

                    val book = Book(
                        null,
                        binding.edName.text.toString(),
                        binding.edAuthor.text.toString(),
                        date,
                        binding.ratingBar2.rating.toString().toFloat(),
                        binding.edPrice.text.toString().toInt()
                    )
                    val books = db.collection("books")
                    val data = hashMapOf(
                        "name" to book.name,
                        "author" to book.author,
                        "price" to book.price,
                        "rates" to book.rates,
                        "year" to Timestamp(book.year!!)
                    )
                    books.add(data)
                        .addOnSuccessListener {
                            Toast.makeText(this, "book added successfully", Toast.LENGTH_SHORT)
                                .show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                }
            }
        }
    }
}