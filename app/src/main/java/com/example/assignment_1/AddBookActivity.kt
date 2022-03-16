package com.example.assignment_1

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment_1.databinding.ActivityAddBookBinding
import com.example.assignment_1.model.Book
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class AddBookActivity : AppCompatActivity() {
    private val TAG = "AddBookActivity"
    lateinit var binding: ActivityAddBookBinding
    private val db = Firebase.firestore
    private val storage = FirebaseStorage.getInstance()
    private lateinit var imageURI: Uri
    private var myImage: String? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = ProgressDialog(this)

        binding.getImage.setOnClickListener {
            selectImage()
        }

        if (intent.getSerializableExtra("book") != null) {
            /*in edit book case*/
            val myBook = intent.getSerializableExtra("book") as Book

            binding.textView.text = "Edit Book"
            binding.btnAdd.text = "Edit Book"
            binding.getImage.text = Uri.parse(myBook.image).host
            myImage = myBook.image

            binding.edName.setText(myBook.name)
            binding.edAuthor.setText(myBook.author)
            binding.edYear.setText((myBook.year!!.year + 1901).toString())
            binding.edPrice.setText(myBook.price.toString())
            binding.ratingBar2.rating = myBook.rates

            // Update book data
            binding.btnAdd.setOnClickListener {
                if (binding.edName.text.isNotEmpty()
                    && binding.edAuthor.text.isNotEmpty()
                    && binding.edYear.text.isNotEmpty()
                    && binding.edPrice.text.isNotEmpty()
                    && binding.ratingBar2.rating != 0.0f
                ) {
                    if (myImage != null) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                        val year = binding.edYear.text.toString().toInt()
                        val month = 0
                        val day = 0
                        val date = dateFormat.parse("$year-$month-$day")
                        Log.d(TAG, date!!.toString())

                        updateBook(
                            Book(
                                myBook.id!!,
                                binding.edName.text.toString(),
                                binding.edAuthor.text.toString(),
                                date,
                                binding.ratingBar2.rating.toString().toFloat(),
                                binding.edPrice.text.toString().toInt(),
                                myImage!!
                            )
                        )
                    } else {
                        Toast.makeText(this, "Wait for image uploaded", Toast.LENGTH_SHORT).show()
                    }
                }
            }


            // Delete Book
            binding.btnDelete.setOnClickListener {
                deleteBook(myBook.id!!)
            }

        }
        else {
            // Add book
            binding.btnDelete.visibility = View.GONE

            binding.btnAdd.setOnClickListener {
                if (binding.edName.text.isNotEmpty()
                    && binding.edAuthor.text.isNotEmpty()
                    && binding.edYear.text.isNotEmpty()
                    && binding.edPrice.text.isNotEmpty()
                    && binding.ratingBar2.rating != 0.0f
                ) {

                    if (myImage != null) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                        val year = binding.edYear.text.toString().toInt()
                        val month = 0
                        val day = 0
                        val date = dateFormat.parse("$year-$month-$day")
                        Log.d(TAG, date!!.toString())

                        addBook(
                            Book(
                                null,
                                binding.edName.text.toString(),
                                binding.edAuthor.text.toString(),
                                date,
                                binding.ratingBar2.rating.toString().toFloat(),
                                binding.edPrice.text.toString().toInt(),
                                myImage!!
                            )
                        )
                    } else {
                        Toast.makeText(this, "Wait for image uploaded", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Fill the Fields", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addBook(book: Book) {

        val data = hashMapOf(
            "name" to book.name,
            "author" to book.author,
            "price" to book.price,
            "rates" to book.rates,
            "year" to Timestamp(book.year!!),
            "image" to myImage
        )

        db.collection("books").add(data)
            .addOnSuccessListener {
                Toast.makeText(this, "book added successfully", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.message.toString())
            }
    }

    private fun updateBook(book: Book) {

        val data = hashMapOf(
            "id" to book.id,
            "name" to book.name,
            "author" to book.author,
            "price" to book.price,
            "rates" to book.rates,
            "year" to Timestamp(book.year!!),
            "image" to book.image
        )

        db.collection("books").document(book.id!!).update(data as Map<String, Any>)
            .addOnSuccessListener {
                Toast.makeText(this, "book updated successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.message.toString())
            }
    }

    private fun deleteBook(book_id: String) {

        db.collection("books").document(book_id).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "book deleted successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.message.toString())
            }

    }

    // select an image from gallery
    private fun selectImage() {
        binding.getImage.hint = ""
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.type = "image/*"

        // it will open the gallery, and when user finished the gallery,
        // onActivityResult() will be called.

        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // upload image to firebase storage
        fun uploadImage() {
            Log.d(TAG, ".uploadImage")
            val filename = UUID.randomUUID().toString()
            val storageRef = storage.reference.child("images").child(filename)
            storageRef.putFile(imageURI)
                .addOnSuccessListener { it ->
                    Log.d(TAG, "upload Success")

                    storageRef.downloadUrl
                        .addOnSuccessListener { it ->
                            Log.d(TAG, "uri for image is $it")
                            myImage = it.toString()
                            progressDialog?.dismiss()
                        }
                        .addOnFailureListener { it ->
                            Log.d(TAG, "uri for image is $it")
                        }
                }
                .addOnFailureListener {
                    Log.d(TAG, "upload Failed")
                }
            binding.getImage.text = filename
        }

        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageURI = data?.data!!
            Log.d(TAG, imageURI.toString())
            progressDialog?.setMessage("Uploading ...")
            progressDialog?.show()
            uploadImage()
        }
    }
}