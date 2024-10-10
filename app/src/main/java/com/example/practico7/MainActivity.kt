package com.example.practico7

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var editTextUrl: EditText
    private lateinit var buttonDownload: Button
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextUrl = findViewById(R.id.editTextUrl)
        buttonDownload = findViewById(R.id.buttonDownload)
        imageView = findViewById(R.id.imageView)

        buttonDownload.setOnClickListener {
            val imageUrl = editTextUrl.text.toString()
            if (imageUrl.isNotEmpty()) {
                downloadAndSaveImage(imageUrl)
            } else {
                Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun downloadAndSaveImage(imageUrl: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    // Загрузка изображения в фоновом потоке (Network)
                    Log.d("NetworkThread", "Downloading image in thread: ${Thread.currentThread().name}")
                    downloadImage(imageUrl)
                }

                withContext(Dispatchers.IO) {
                    // Сохранение изображения во внутренней памяти (Disk)
                    Log.d("DiskThread", "Saving image in thread: ${Thread.currentThread().name}")
                    saveImageToInternalStorage(bitmap)
                }

                // Отображение изображения в основном потоке (Main)
                imageView.setImageBitmap(bitmap)

                Toast.makeText(this@MainActivity, "Image downloaded and saved", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun downloadImage(imageUrl: String): Bitmap {
        return URL(imageUrl).openStream().use {
            BitmapFactory.decodeStream(it)
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap) {
        val file = File(filesDir, "downloaded_image.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        Log.d("ImageSavePath", "Image saved to: ${file.absolutePath}")
    }
}