package com.example.heartup

import android.app.Activity
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.bumptech.glide.Glide
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts

import androidx.appcompat.app.AppCompatActivity
import okhttp3.ResponseBody
import java.io.File

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image)
    }

    private val retrofit2 = Retrofit.Builder()
        .baseUrl("https://heartup-ahhs8sj7.b4a.run/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit2.create(ApiService::class.java)


    // Example function to launch the image picker
    fun onUploadButtonClick(view: View) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedImageUri: Uri? = data?.data
                // Handle the selected image URI
                handleImageUri(selectedImageUri)
            }
        }



    private fun handleImageUri(uri: Uri?) {
        if (uri != null) {
            val imageFile = File(uri.path!!)
            if (imageFile.exists()) {
                // Proceed with creating RequestBody and making the API call
            } else {
                Log.e("FILE_ERROR", "File does not exist at the specified path")
                // Handle this case accordingly
            }

            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile)
            val imageBody = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

            apiService.uploadImage(imageBody).enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val jsonResponse = response.body()?.string()
                        Log.d("API_RESPONSE", "Response successful")

                        if (!jsonResponse.isNullOrEmpty()) {
                            updateUIWithResponse(jsonResponse)
                        }
                    } else {
                        updateUIWithResponse("shit")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // Handle network error
                    Log.e("API_RESPONSE", "Network error: ${t.message}")
                }
            })
        }
    }


    private fun updateUIWithResponse(apiResponse: String) {
        System.out.println(apiResponse)
        findViewById<TextView>(R.id.result_text).setText(apiResponse)
    }

}