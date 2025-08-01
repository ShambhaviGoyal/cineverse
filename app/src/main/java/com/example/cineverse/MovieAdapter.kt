package com.example.cineverse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MovieAdapter(private val posterList: List<String>, private val titleList: List<String>)
    :  RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val poster: ImageView
        val title: TextView

        init {
            // Define click listener for the ViewHolder's View
            poster = view.findViewById(R.id.moviePoster)
            title = view.findViewById(R.id.movieTitle)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieAdapter.ViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load("http://via.placeholder.com/300.png")
            .into(holder.poster)

        holder.title.text = titleList[position]
    }

    override fun getItemCount(): Int {
        return titleList.size
    }
}