package com.example.picturesque

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class ImageViewModel : ViewModel() {

    val imagesLiveData = MutableLiveData<List<FlickrPhoto>>()

    fun fetchImages(searchString: String, activity: MainActivity){
        viewModelScope.launch {
            activity.showDialog()
            val response: FlickrSearchResponse = RetrofitInstance.api.fetchImages(searchString)
            val photos = response.photos.photo
            imagesLiveData.postValue(photos)
            delay(1000)
            activity.closeDialog()
        }
    }
}

class ImageViewModelFactory : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImageViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return ImageViewModel() as T
        }
        throw IllegalArgumentException("No such view model class")
    }
}