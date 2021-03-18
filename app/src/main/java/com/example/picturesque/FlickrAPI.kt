package com.example.picturesque

import com.example.picturesque.Constants.Companion.API_KEY
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface FlickrAPI {
    @GET("?method=flickr.photos.search&format=json&nojsoncallback=1&api_key=$API_KEY&per_page=10")
    suspend fun fetchImages(@Query("text") searchString: String): FlickrSearchResponse

}