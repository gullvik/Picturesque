package com.example.picturesque

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import com.example.picturesque.databinding.ActivityMainBinding


private const val NIGHT_MODE_KEY = "nightmode"

class MainActivity : AppCompatActivity() {
    private val imageViewModel: ImageViewModel by viewModels {
        ImageViewModelFactory()
    }
    private lateinit var binding: ActivityMainBinding

    private lateinit var sharedPref: SharedPreferences

    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences("sharedPref", MODE_PRIVATE)

        val mode = sharedPref.getInt(NIGHT_MODE_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(mode)

        alertDialog = AlertDialog.Builder(this).setView(R.layout.loading_dialog).create()

        val adapter = ImageAdapter(this) { photo ->
            val intent = Intent(this@MainActivity, ImageDetailActivity::class.java)
            intent.putExtra(IMAGE_TITLE, photo.title)
            intent.putExtra(IMAGE_URI, photo.url)
            startActivity(intent)
        }
        binding.rvImages.adapter = adapter
        val layoutManager = GridLayoutManager(this, 3)
        binding.rvImages.layoutManager = layoutManager

        imageViewModel.imagesLiveData.observe(this) {
            if (it.isNotEmpty()) {
                adapter.setImageList(it)
            }
        }

        binding.rvImages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    if (layoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount.minus(1)) {
                        loadImages()
                    }
                }
            }
        })

        binding.etSearchText.setOnEditorActionListener { _, actionId, _ ->
            if (binding.etSearchText.text.isNotEmpty() && actionId == EditorInfo.IME_ACTION_SEARCH) {
                search(binding.etSearchText.text.toString())
                return@setOnEditorActionListener true
            }
            Toast.makeText(this, getString(R.string.no_text), Toast.LENGTH_SHORT).show()
            return@setOnEditorActionListener false
        }

        binding.btnSearch.setOnClickListener {
            if (binding.etSearchText.text.isNotEmpty()) {
                search(binding.etSearchText.text.toString())
            }else {
                Toast.makeText(this, getString(R.string.no_text), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun search(searchText: String) {
        hideSoftInput()
        imageViewModel.fetchImages(searchText, this)
    }

    private fun loadImages(){
        imageViewModel.loadImages(this)
    }

    override fun onPause() {
        super.onPause()
        sharedPref.edit().putInt(NIGHT_MODE_KEY, AppCompatDelegate.getDefaultNightMode())
            .apply()
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

    fun showProgress() {
        binding.pbLoadImages.visibility = View.VISIBLE
    }

    fun hideProgress() {
        binding.pbLoadImages.visibility = View.GONE
    }

    private fun hideSoftInput() {
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(binding.etSearchText.windowToken, 0)
        binding.etSearchText.clearFocus()
    }
}
