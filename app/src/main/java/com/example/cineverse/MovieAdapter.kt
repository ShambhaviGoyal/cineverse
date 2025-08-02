package com.example.cineverse

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONObject

class MovieAdapter(private val movieList: List<JSONObject>)
    :  RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val poster: ImageView
        val title: TextView
        val pTitle: TextView
        val pOverview: TextView

        init {
            // Define click listener for the ViewHolder's View
            poster = view.findViewById(R.id.moviePoster)
            title = view.findViewById(R.id.movieTitle)
            pTitle = view.findViewById(R.id.pTitle)
            pOverview = view.findViewById(R.id.pOverview)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieAdapter.ViewHolder, position: Int) {
        val movie = movieList[position]

        Glide.with(holder.itemView)
            .load("https://image.tmdb.org/t/p/original" + movie.get("poster_path"))
            .override(300, 600)
            .into(holder.poster)

        holder.title.text = movie.get("original_title").toString()

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val popupView = LayoutInflater.from(context).inflate(R.layout.activity_movie_pop_up, null)

            val popupTitle = popupView.findViewById<TextView>(R.id.pTitle)
            val popupOverview = popupView.findViewById<TextView>(R.id.pOverview)
            val popupGenre = popupView.findViewById<TextView>(R.id.pGenre)
            val popupRelease = popupView.findViewById<TextView>(R.id.pRelease)
            val popupRuntime = popupView.findViewById<TextView>(R.id.pRuntime)
            val popupRating = popupView.findViewById<TextView>(R.id.pRating)
            val popupProduction = popupView.findViewById<TextView>(R.id.pProduction)
            val popupRevenue = popupView.findViewById<TextView>(R.id.pRevenue)
            val popupLanguage = popupView.findViewById<TextView>(R.id.pLanguage)
            val popupPoster = popupView.findViewById<ImageView>(R.id.pPoster)

            popupTitle.text = movie.getString("original_title")
            popupOverview.text = movie.getString("overview")
            popupGenre.text = "Genres: ${getGenres(movie.getJSONArray("genres"))}"
            popupRelease.text = "Release Date: ${movie.getString("release_date")}"
            popupRuntime.text = "Runtime: ${movie.get("runtime")}"
            popupRating.text = "Rating: ${movie.get("vote_average")}"
            popupProduction.text = "Budget: ${movie.get("budget")}"
            popupRevenue.text = "Revenue: ${movie.get("revenue")}"
            popupLanguage.text = "Language: ${movie.get("original_language")}"
            Glide.with(holder.itemView)
                .load("https://image.tmdb.org/t/p/original" + movie.get("poster_path"))
                .override(150, 200)
                .into(popupPoster)

            val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

            popupWindow.showAtLocation(holder.itemView, Gravity.CENTER, 0, 0)

            popupView.findViewById<ImageView>(R.id.pClose)?.setOnClickListener {
                popupWindow.dismiss()
            }
        }
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    private fun getGenres(genresArray: JSONArray): String {
        var genres = ""

        for (index in 0 until genresArray.length()) {
            genres += genresArray.getJSONObject(index).get("name").toString() + ", "
        }

        return genres.substringBeforeLast(',')
    }
}