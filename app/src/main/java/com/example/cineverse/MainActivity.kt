package com.example.cineverse

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.OkHttpClient
import okhttp3.Request
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import androidx.lifecycle.lifecycleScope

class MainActivity : AppCompatActivity() {
    val API_KEY = BuildConfig.API_KEY;
    val client = OkHttpClient()

    private lateinit var movieList: MutableList<JSONObject>
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

        movieList = mutableListOf()
        rvMovie = findViewById(R.id.movieList)


        // Theme Button
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

        // Top Button
        val topButton = findViewById<Button>(R.id.topRated)
        topButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    clearMovies()
                    getTopRatedMovies()
                    withContext(Dispatchers.Main) {
                        updateMovies()
                    }
                } catch (e: Exception) {
                    Log.e("COROUTINE_ERROR", "Exception: ${e.localizedMessage}", e)
                }
            }
        }

        // Popular Button
        val popularButton = findViewById<Button>(R.id.popular)
        popularButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    clearMovies()
                    getPopularMovies()
                    withContext(Dispatchers.Main) {
                        updateMovies()
                    }
                } catch (e: Exception) {
                    Log.e("COROUTINE_ERROR", "Exception: ${e.localizedMessage}", e)
                }
            }
        }

        // Search Functionality
        val searchIcon = findViewById<ImageView>(R.id.seach_icon)
        searchIcon.setOnClickListener {
            val searchQuery = extractSearchQuery()

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    clearMovies()
                    searchMovie(searchQuery)
                    withContext(Dispatchers.Main) {
                        updateMovies()
                    }
                } catch (e: Exception) {
                    Log.e("COROUTINE_ERROR", "Exception: ${e.localizedMessage}", e)
                }
            }
        }

        // Fetch movies initially
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                clearMovies()
                getTopRatedMovies()
                withContext(Dispatchers.Main) {
                    updateMovies()
                }
            } catch (e: Exception) {
                Log.e("COROUTINE_ERROR", "Exception: ${e.localizedMessage}", e)
            }
        }
    }

    private fun clearMovies() {
        this.movieList.clear()
    }

    private fun updateMovies() {
        rvMovie.adapter = MovieAdapter(movieList)
        rvMovie.layoutManager = GridLayoutManager(this@MainActivity, 2)
    }

    private fun getMovie(movieUrl: String) {
        val movieDetailsRequest = Request.Builder()
            .url(movieUrl)
            .addHeader("Authorization", "Bearer $API_KEY")
            .build()

        client.newCall(movieDetailsRequest).execute().use { responseMovie ->
            if (responseMovie.isSuccessful) {
                val movieObject = JSONObject(responseMovie.body?.string())
                movieList.add(movieObject)
            } else {
                Log.e("HTTP_ERROR", "Error: ${responseMovie.code} - ${responseMovie.message}")
                null
            }
        }
    }

    private fun getTopRatedMovies() {
        val url = "https://api.themoviedb.org/3/movie/top_rated?append_to_response=revenue&language=en-US&page=1"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $API_KEY")
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val jsonObject = JSONObject(response.body?.string())
                val movies = jsonObject.getJSONArray("results")

                for (index in 0 until movies.length()) {
                    val movieUrl = "https://api.themoviedb.org/3/movie/${movies.getJSONObject(index).get("id")}"
                    getMovie(movieUrl)
                }
            } else {
                Log.e("HTTP_ERROR", "Error: ${response.code} - ${response.message}")
                null
            }
        }
    }

    private fun getPopularMovies() {
        val url = "https://api.themoviedb.org/3/movie/popular?language=en-US&page=1"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $API_KEY")
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val jsonObject = JSONObject(response.body?.string())
                val movies = jsonObject.getJSONArray("results")

                for (index in 0 until movies.length()) {
                    val movieUrl = "https://api.themoviedb.org/3/movie/${movies.getJSONObject(index).get("id")}"
                    getMovie(movieUrl)
                }
            } else {
                Log.e("HTTP_ERROR", "Error: ${response.code} - ${response.message}")
                null
            }
        }
    }

    private fun searchMovie(query: String) {
        val url = "https://api.themoviedb.org/3/search/movie?language=en-US&page=1&sort_by=popularity.desc&query=$query"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $API_KEY")
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val jsonObject = JSONObject(response.body?.string())
                val movies = jsonObject.getJSONArray("results")

                for (index in 0 until movies.length()) {
                    val movieUrl = "https://api.themoviedb.org/3/movie/${movies.getJSONObject(index).get("id")}"
                    getMovie(movieUrl)
                }
            } else {
                Log.e("HTTP_ERROR", "Error: ${response.code} - ${response.message}")
                null
            }
        }
    }

    private fun extractSearchQuery(): String {
        val searchBox = findViewById<EditText>(R.id.search_bar)

        val searchQuery = searchBox.text
        val formattedQuery = searchQuery.split(" ").joinToString("+")

        return formattedQuery
    }
}