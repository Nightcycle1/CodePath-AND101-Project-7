package com.example.codepathand101project5

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity(), NasaApodClient.ApodCallback {
    private lateinit var titleTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var apodImageView: ImageView
    private lateinit var actionButton: Button
    private lateinit var explanationTextView: TextView

    private val apodClient = NasaApodClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //Initialize views
        titleTextView = findViewById(R.id.titleTextView)
        dateTextView = findViewById(R.id.dateTextView)
        apodImageView = findViewById(R.id.apodImageView)
        actionButton = findViewById(R.id.actionButton)
        explanationTextView = findViewById(R.id.explanationTextView)


        // First fetch APOD
        apodClient.fetchApod(callback = this)

        //Button click listener
        actionButton.setOnClickListener{
            apodClient.fetchApod(callback = this)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    override fun onSuccess(apodData: NasaApodClient.ApodData)
    {
        runOnUiThread{
            Log.d("APOD", "Image URL: ${apodData.imageUrl}")
            titleTextView.text = apodData.title
            dateTextView.text = apodData.date
            explanationTextView.text = apodData.explanation

            Glide.with(this)
                .load(apodData.imageUrl)
                .into(apodImageView)
        }
    }

    override fun onFailure(error: Throwable)
    {
        runOnUiThread{
            Toast.makeText(this, "Failed to fetch APOD: ${error.message}", Toast.LENGTH_SHORT).show()
        }
    }
}