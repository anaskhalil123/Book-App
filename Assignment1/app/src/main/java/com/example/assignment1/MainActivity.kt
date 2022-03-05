package com.example.assignment1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment1.Adapter.RecyclerBookAdapter
import com.example.assignment1.databinding.ActivityMainBinding
import com.example.assignment1.model.Book
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var myBooks: ArrayList<Book> = ArrayList()
    private val TAG = "MainActivity"
    private val db = Firebase.firestore
    var isChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        myBooks.clear()

        binding.rcyBooks.adapter = RecyclerBookAdapter(this, myBooks)
        binding.rcyBooks.layoutManager = LinearLayoutManager(this)
        binding.rcyBooks.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        /*retrieve data from firestore*/
        getBooks()

        /*floating action button*/
        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this, AddBookActivity::class.java))
        }
    }

    private fun getBooks() {
        myBooks.clear()
        db.collection("books")
            .get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    for (document in result) {
                        Log.d(TAG, "document id is ${document.id} with data ${document.data}")
                        myBooks.add(
                            Book(
                                document.id,
                                document["name"].toString(),
                                document["author"].toString(),
                                document["year"].toString().toInt(),
                                document["rates"].toString().toFloat(),
                                document["price"].toString().toInt(),
                            )
                        )
                    }

                    binding.rcyBooks.adapter!!.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "error with Exception : $exception")
            }
    }

    override fun onResume() {
        if (isChanged) {
            getBooks()
        } else {
            isChanged = true
        }
        super.onResume()
    }
}