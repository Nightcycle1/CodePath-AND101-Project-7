package com.example.codepathand101project5

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity(), NasaApodClient.ApodListCallback {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ApodAdapter
    private val apodClient = NasaApodClient()
    private var currentPage = 1
    private val pageSize = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ApodAdapter(mutableListOf()) { apod ->
            showExplanationDialog(apod)
        }
        recyclerView.adapter = adapter

        // Fetch multiple APODs
        apodClient.fetchMultipleApod(7, callback = this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && (visibleItemCount + firstVisibleItem) >= totalItemCount - 5) {
                    currentPage++
                    loadMoreItems()
                }
            }
        })
    }




    private var isLoading = false

    private fun loadMoreItems() {
        if (isLoading) return

        isLoading = true
        adapter.addLoadingFooter()

        apodClient.fetchMultipleApod(pageSize, callback = object : NasaApodClient.ApodListCallback {
            override fun onSuccess(newApods: List<ApodData>) {
                runOnUiThread {
                    adapter.removeLoadingFooter()
                    adapter.addAll(newApods)
                    isLoading = false
                }
            }

            override fun onFailure(error: Throwable) {
                runOnUiThread {
                    adapter.removeLoadingFooter()
                    isLoading = false
                    Toast.makeText(this@MainActivity, "Error loading more", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun showExplanationDialog(apod: ApodData) {
        AlertDialog.Builder(this)
            .setTitle(apod.title)
            .setMessage(apod.explanation)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    override fun onSuccess(apodList: List<ApodData>) {
        runOnUiThread {
            adapter.addAll(apodList)
           // recyclerView.adapter = adapter
        }
    }

    override fun onFailure(error: Throwable) {
        runOnUiThread {
            Toast.makeText(this, "Failed to fetch APODs: ${error.message}", Toast.LENGTH_SHORT).show()
        }
    }


}