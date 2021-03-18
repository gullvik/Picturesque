package com.example.picturesque

data class FlickrSearchResponse(
    val photos: PhotosMetaData
)

data class PhotosMetaData(
    val page: Int,
    val pages: Int,
    val perpage: Int,
    val total: String,
    val photo: List<FlickrPhoto>
)

data class FlickrPhoto(
    val id: String,
    val owner: String,
    val secret: String,
    val server: String,
    val farm: Int,
    val title: String,
    val ispublic: Int,
    val isfriend: Int,
    val isfamily: Int,
    var url: String = ""
) {

}