package com.example.cineverse

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    val API_KEY = BuildConfig.API_KEY;
    val client = OkHttpClient()

    private lateinit var posterList: MutableList<String>
    private lateinit var titleList: MutableList<String>
    private lateinit var rvMovie: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        posterList = mutableListOf()
        titleList = mutableListOf()
        rvMovie = findViewById(R.id.movieList)

        val themeToggle = findViewById<ImageView>(R.id.themeToggle)
        val isNight = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        themeToggle.setImageResource(if (isNight) R.drawable.sun else R.drawable.moon)

        themeToggle.setOnClickListener {
            val isCurrentlyNight = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
            val newMode = if (isCurrentlyNight) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES
            AppCompatDelegate.setDefaultNightMode(newMode)

            // Update icon
            themeToggle.setImageResource(if (!isCurrentlyNight) R.drawable.sun else R.drawable.moon)
        }

        // Fetch movies initially
        CoroutineScope(Dispatchers.IO).launch {
            getTopRatedMovies()
            withContext(Dispatchers.Main) {
                updateMovies()
            }
        }
    }

    private fun updateMovies() {
        rvMovie.adapter = MovieAdapter(posterList, titleList)
        rvMovie.layoutManager = GridLayoutManager(this@MainActivity, 2)
    }

    private fun getTopRatedMovies() {
        val url = "https://api.themoviedb.org/3/movie/top_rated?language=en-US&page=1&append_to_response=images"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $API_KEY")
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val jsonObject = JSONObject(response.body?.string())
                val movies = jsonObject.getJSONArray("results")

                for (index in 0 until movies.length()) {
                    this.titleList.add(movies.getJSONObject(index).get("original_title").toString())
                    this.posterList.add("https://image.tmdb.org/t/p/original" + movies.getJSONObject(index).get("poster_path"))
                }
            } else {
                Log.e("HTTP_ERROR", "Error: ${response.code} - ${response.message}")
                null
            }
        }
    }
}