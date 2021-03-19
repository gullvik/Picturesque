package com.example.picturesque

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class ImageViewModel : ViewModel() {

    val imagesLiveData = MutableLiveData<List<FlickrPhoto>>()
    private var searchString: String? = null
    private var pageCounter: Int = 1

    fun fetchImages(searchString: String) {
        //new search
        if (searchString.isNotBlank() && searchString != this.searchString) {
            this.searchString = searchString
            pageCounter = 1
                viewModelScope.launch {
                    val response: FlickrSearchResponse =
                        RetrofitInstance.api.fetchImages(searchString, pageCounter.toString())
                    val photos = response.photos.photo
                    imagesLiveData.postValue(photos)
                }
            //load more images
        } else if (searchString.isNotBlank() && searchString == this.searchString) {
            val oldAndNewPhotos = ArrayList<FlickrPhoto>()
            pageCounter++
            imagesLiveData.value!!.forEach { oldPhoto ->
                oldAndNewPhotos.add(oldPhoto)
            }
            viewModelScope.launch {
                val response: FlickrSearchResponse = RetrofitInstance.api.fetchImages(searchString, pageCounter.toString())
                val photos = response.photos.photo
                photos.forEach { newPhoto ->
                    oldAndNewPhotos.add(newPhoto)
                }
            }
            imagesLiveData.postValue(oldAndNewPhotos)
        }
    }
}

class ImageViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ImageViewModel() as T
        }
        throw IllegalArgumentException("No such view model class")
    }
}