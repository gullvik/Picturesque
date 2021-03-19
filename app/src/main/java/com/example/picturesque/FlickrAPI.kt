package com.example.picturesque

import com.example.picturesque.Constants.Companion.API_KEY
import retrofit2.http.*

interface FlickrAPI {
    @GET("?method=flickr.photos.search&format=json&nojsoncallback=1&api_key=$API_KEY&per_page=20")
    suspend fun fetchImages(@Query("text") searchString: String,
                            @Query("page") page : String = "1"): FlickrSearchResponse

}