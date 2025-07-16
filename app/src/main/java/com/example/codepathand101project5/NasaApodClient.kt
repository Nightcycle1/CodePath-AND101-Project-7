package com.example.codepathand101project5

import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers


class NasaApodClient
{

    private val client = AsyncHttpClient()
    private val apiKey = ""


    fun fetchApod(date: String? =null, callback: ApodCallback)
    {
        val baseUrl = "https://api.nasa.gov/planetary/apod"
        val params = RequestParams().apply {
            put("api_key", apiKey)
            put("count", "1") // Get random APOD
            put("thumbs", "true")
        }

        client.get(baseUrl, params, object : JsonHttpResponseHandler()
        {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON)
            {
                try
                {
                    val jsonObject = json.jsonArray.getJSONObject(0)
                    val apodData = ApodData(
                        title = jsonObject.getString("title"),
                        date = jsonObject.getString("date"),

                        imageUrl = if(jsonObject.getString("media_type") == "video")
                        {
                            jsonObject.optString("thumbnail_url")

                        }
                        else
                        {
                            jsonObject.getString("url")
                        },
                        hdImageUrl = jsonObject.optString("hdurl", ""),
                        explanation = jsonObject.getString("explanation"),
                        mediaType = jsonObject.getString("media_type")
                    )
                    callback.onSuccess(apodData)
                }
                catch (e: Exception)
                {
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

    interface ApodCallback
    {
        fun onSuccess(apodData: ApodData)
        fun onFailure(error: Throwable)
    }

    data class ApodData(
        val title: String,
        val date: String,
        val imageUrl: String,
        val hdImageUrl: String,
        val explanation: String,
        val mediaType: String
    )


}