package com.example.picturesque

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picturesque.Constants.IMAGE_TITLE
import com.example.picturesque.Constants.IMAGE_URI

class MainActivity : AppCompatActivity() {
    private val imageViewModel: ImageViewModel by viewModels {
        ImageViewModelFactory()
    }

    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        alertDialog = AlertDialog.Builder(this).setView(R.layout.loading_dialog).create()

        val rv: RecyclerView = findViewById(R.id.rv_images)
        val adapter = ImageAdapter(this)
        adapter.setImageClickListener(object : ImageClickListener {
            override fun onClick(photo: FlickrPhoto) {
                val intent = Intent(this@MainActivity, ImageDetailActivity::class.java)
                intent.putExtra(IMAGE_TITLE, photo.title)
                intent.putExtra(IMAGE_URI, photo.url)
                startActivity(intent)
            }
        })
        rv.adapter = adapter
        rv.layoutManager = GridLayoutManager(this, 3)

        imageViewModel.imagesLiveData.observe(this) {
            if (it.isNotEmpty()) {
                adapter.setImgUrls(it)
            }
        }

        val search: Button = findViewById(R.id.btn_search)
        val etText: EditText = findViewById(R.id.et_search_text)
        etText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(etText.windowToken, 0)
                etText.clearFocus()
                imageViewModel.fetchImages(etText.text.toString(), this)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        search.setOnClickListener {
            if (etText.text.isNotEmpty()) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(etText.windowToken, 0)
                etText.clearFocus()
                imageViewModel.fetchImages(etText.text.toString(), this)
            } else {
                Toast.makeText(this, getString(R.string.no_text), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings, menu)
        val mode = AppCompatDelegate.getDefaultNightMode()
        if (mode == AppCompatDelegate.MODE_NIGHT_YES) {
            menu!!.getItem(0).setTitle(R.string.night_mode_light)
        } else {
            menu!!.getItem(0).setTitle(R.string.night_mode)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.switch_nightmode -> {
                val mode = AppCompatDelegate.getDefaultNightMode()
                if (mode == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                recreate()
            }
        }
        return true
    }

    fun showDialog() {
        alertDialog.show()
    }

    fun closeDialog() {
        alertDialog.dismiss()

    }
}