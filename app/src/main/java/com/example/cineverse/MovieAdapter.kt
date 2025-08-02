package com.example.cineverse

import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
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

        view.setOnClickListener {
            val v = View.inflate(parent.context, R.layout.activity_movie_pop_up, null)

            val popupWindow = PopupWindow(v, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
            popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0)

            v.findViewById<ImageView>(R.id.pClose).setOnClickListener {
                popupWindow.dismiss()
            }
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieAdapter.ViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(posterList[position])
            .override(300, 600)
            .into(holder.poster)

        holder.title.text = titleList[position]
    }

    override fun getItemCount(): Int {
        return titleList.size
    }
}