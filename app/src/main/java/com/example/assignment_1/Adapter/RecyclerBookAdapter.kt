package com.example.assignment1.Adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment1.AddBookActivity
import com.example.assignment1.R
import com.example.assignment1.model.Book

class RecyclerBookAdapter(val activity: Activity, val books: ArrayList<Book>) :
    RecyclerView.Adapter<RecyclerBookAdapter.MyHolder>() {

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name = itemView.findViewById<TextView>(R.id.book_name)
        var author = itemView.findViewById<TextView>(R.id.book_author)
        var year = itemView.findViewById<TextView>(R.id.book_year)
        var price = itemView.findViewById<TextView>(R.id.book_price)
        var rating = itemView.findViewById<TextView>(R.id.book_rating)
        val ratingBar = itemView.findViewById<RatingBar>(R.id.ratingBar)
        val editButton = itemView.findViewById<Button>(R.id.editBookButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val root = LayoutInflater.from(activity).inflate(R.layout.book_item, parent, false)
        return MyHolder(root)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text = books[position].name
        holder.author.text = books[position].author
        holder.year.text = (books[position].year!!.year + 1901).toString()
        holder.price.text = books[position].price.toString()
        holder.rating.text = books[position].rates.toString()

        holder.ratingBar.setOnRatingBarChangeListener { ratingBar, fl, b ->
            if (b) {
                holder.rating.text = fl.toString()
            }
        }
        holder.ratingBar.rating = books[position].rates
        holder.editButton.setOnClickListener {
            activity.startActivity(
                Intent(activity, AddBookActivity::class.java).putExtra("book", books[position])
            )
        }
    }

    override fun getItemCount(): Int {
        return books.size
    }

}