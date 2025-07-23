package com.example.codepathand101project5

import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class NasaApodClient {
    private val client = AsyncHttpClient()
    private val apiKey = "DEMO_KEY" // Replace with your own API key

    fun fetchMultipleApod(count: Int, callback: ApodListCallback, date: String? = null) {
        val baseUrl = "https://api.nasa.gov/planetary/apod"
        val params = RequestParams().apply {
            put("api_key", apiKey)
            put("count", count.toString()) // Get multiple APODs
            put("thumbs", "true")
            date?.let { put("date", it) }

        }

        client.get(baseUrl, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                try {
                    val apodList = mutableListOf<ApodData>()
                    for (i in 0 until json.jsonArray.length()) {
                        val jsonObject = json.jsonArray.getJSONObject(i)
                        apodList.add(ApodData(
                            title = jsonObject.getString("title"),
                            date = jsonObject.getString("date"),
                            explanation = jsonObject.getString("explanation"),
                            imageUrl = if (jsonObject.getString("media_type") == "video") {
                                jsonObject.optString("thumbnail_url", "")
                            } else {
                                jsonObject.getString("url")
                            },
                            mediaType = jsonObject.getString("media_type")
                        ))
                    }
                    callback.onSuccess(apodList)
                } catch (e: Exception) {
                    callback.onFailure(e)
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                callback.onFailure(throwable ?: Exception(errorResponse))
            }
        })
    }

    interface ApodListCallback {
        fun onSuccess(apodList: List<ApodData>)
        fun onFailure(error: Throwable)
    }
}