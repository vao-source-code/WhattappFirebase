package com.sosolution.socialtelecomunication.activities

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.sosolution.socialtelecomunication.R
import com.sosolution.socialtelecomunication.utils.FileUtil
import java.io.File

class PostActivity : AppCompatActivity() {

    lateinit var mImageViewPost1: ImageView
    val GALLERY_RESQUEST_CODE =1
    lateinit var mImageFile : File
    var  fileUtil  = FileUtil()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        inicializarVistas()
        abrirImagen1()
    }

    private fun abrirImagen1() {
        mImageViewPost1.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
       val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent,GALLERY_RESQUEST_CODE)

    }

    private fun inicializarVistas() {
        mImageViewPost1 = findViewById(R.id.imageViewPost1)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GALLERY_RESQUEST_CODE && resultCode == RESULT_OK){
            try{

                    mImageFile = fileUtil.from(this, data?.data)!!
                mImageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.absolutePath))


            }catch (e : Exception){
                Log.d("ERROR", "Se produjo un error" + e.message)
                Toast.makeText(this,"Se produjo un error"+ e.message, Toast.LENGTH_LONG).show()

            }
        }
    }


}