package com.example.picturesque

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picturesque.Constants.Companion.IMAGE_TITLE
import com.example.picturesque.Constants.Companion.IMAGE_URI
import com.example.picturesque.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val imageViewModel: ImageViewModel by viewModels {
        ImageViewModelFactory()
    }
    private lateinit var binding : ActivityMainBinding

    private var searchString : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ImageAdapter(this)
        adapter.setImageClickListener(object : ImageClickListener {
            override fun onClick(photo: FlickrPhoto) {
                val intent = Intent(this@MainActivity, ImageDetailActivity::class.java)
                intent.putExtra(IMAGE_TITLE, photo.title)
                intent.putExtra(IMAGE_URI, photo.url)
                startActivity(intent)
            }
        })
        binding.rvImages.adapter = adapter
        val layoutManager = GridLayoutManager(this, 3)
        binding.rvImages.layoutManager = layoutManager

        imageViewModel.imagesLiveData.observe(this) {
            if (it.isNotEmpty()) {
                adapter.setImages(it)
            }
        }

        binding.rvImages.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    if (layoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount.minus(1)) {
                        imageViewModel.fetchImages(searchString ?: "")
                    }
                }
            }
        })

        val search: Button = findViewById(R.id.btn_search)
        val etText: EditText = findViewById(R.id.et_search_text)
        etText.setOnEditorActionListener { _, actionId, _ ->
            if (binding.etSearchText.text.isNotEmpty() && actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchString = etText.text.toString()
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(etText.windowToken, 0)
                etText.clearFocus()
                imageViewModel.fetchImages(etText.text.toString())
                return@setOnEditorActionListener true
            }
            Toast.makeText(this, getString(R.string.no_text), Toast.LENGTH_SHORT).show()
            return@setOnEditorActionListener false
        }

        search.setOnClickListener {
            if (etText.text.isNotEmpty()) {
                searchString = etText.text.toString()
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(etText.windowToken, 0)
                etText.clearFocus()
                imageViewModel.fetchImages(etText.text.toString())
            } else {
                Toast.makeText(this, getString(R.string.no_text), Toast.LENGTH_SHORT).show()
            }
        }
    }
}