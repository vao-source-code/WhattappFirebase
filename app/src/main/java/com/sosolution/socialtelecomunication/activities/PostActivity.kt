package com.sosolution.socialtelecomunication.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.textfield.TextInputEditText
import com.sosolution.socialtelecomunication.R
import com.sosolution.socialtelecomunication.models.Post
import com.sosolution.socialtelecomunication.providers.AuthProvider
import com.sosolution.socialtelecomunication.providers.ImageProvider
import com.sosolution.socialtelecomunication.providers.PostProvider
import com.sosolution.socialtelecomunication.utils.FileUtil
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import dmax.dialog.SpotsDialog
import java.io.File
import java.util.*


class PostActivity : AppCompatActivity() {


    private val GALLERY_RESQUEST_CODE = 1
    private val GALLERY_RESQUEST_CODE_2 = 2
    private val PHOTO_RESQUEST_CODE = 3
    private val PERMISSION_NUMBER = 777


    private var mCategory: String = ""
    private var fileUtil = FileUtil()
    private var mTitle: String = ""
    private var mDescription: String = ""
    private lateinit var mAlertDialog: AlertDialog
    private lateinit var mBuilderSelector: AlertDialog.Builder
    private val option = arrayOf("Imagen de galería", "Tomar una foto")
    private lateinit var mCircleImageback: CircleImageView

    //variables para tomar foto
    private lateinit var mAbsolutePhotoPath: String
    private lateinit var mPhotoPath: String
    private lateinit var mPhotoFile: File


    //variables relacionadas con activity post

    //varibles de las imagenes
    private var mImageFile1: File? = null
    private var mImageFile2: File? = null
    private lateinit var mImageViewPost1: ImageView
    private lateinit var mImageViewPost2: ImageView


    private lateinit var mButtonPost: Button
    private lateinit var mTextInputTitle: TextInputEditText
    private lateinit var mTextInputDescription: TextInputEditText
    private lateinit var mImageViewPC: ImageView
    private lateinit var mImageViewNintendo: ImageView
    private lateinit var mImageViewXbox: ImageView
    private lateinit var mImageViewPS: ImageView
    private lateinit var mTextViewCategory: TextView


    //variables de firebase
    private lateinit var mImageProvider: ImageProvider
    private lateinit var mPostProvider: PostProvider
    private lateinit var mAuthProvider: AuthProvider


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        inicializarVistas()
        onclickImageViewPost1()
        onclickImageViewPost2()

        onclickImageView()
        onclickPost()
        onclickBack()
    }

    private fun onclickBack() {
        mCircleImageback.setOnClickListener {
            finish()
        }

    }

    //en base a que mImage realizen click se determinara la categoria
    private fun onclickImageView() {
        mImageViewPC.setOnClickListener {
            mCategory = "PC"
            mTextViewCategory.text = mCategory
        }
        mImageViewPS.setOnClickListener {
            mCategory = "PS"
            mTextViewCategory.text = mCategory

        }
        mImageViewNintendo.setOnClickListener {
            mCategory = "Nintendo"
            mTextViewCategory.text = mCategory

        }
        mImageViewXbox.setOnClickListener {
            mCategory = "Xbox"
            mTextViewCategory.text = mCategory

        }


    }

    private fun onclickPost() {
        mButtonPost.setOnClickListener {
            //saveImage()
            clickPost()
        }
    }

    private fun clickPost() {
        mTitle = mTextInputTitle.text.toString()
        mDescription = mTextInputDescription.text.toString()
        if (mTitle.isNotEmpty() && mDescription.isNotEmpty() && mCategory.isNotEmpty()) {
            if (mImageFile1 != null || mImageFile2 != null) {
                saveImage()
            } else {
                Toast.makeText(this, "Debes seleccionar una imagen", Toast.LENGTH_SHORT).show()

            }

        } else {
            Toast.makeText(this, "Completa los datos para publicar", Toast.LENGTH_SHORT).show()
        }

    }

    private fun saveImage() {
        mAlertDialog.show()
        try {
            mImageProvider.save(mImageFile1).addOnCompleteListener { it ->
                if (it.isSuccessful) {

                    addOnSuccessProvider()

                    Toast.makeText(this, "Se almaceno correctamente", Toast.LENGTH_LONG).show()
                } else {
                    mAlertDialog.dismiss()
                    Toast.makeText(this, "No se pudo almacenar correctamente ", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        } catch (ex: Exception) {
            mAlertDialog.dismiss()
            Toast.makeText(this, "El post no se pudo Cargar, error critico", Toast.LENGTH_SHORT)
                .show()

        }
    }

    //funcion para obtener url de la foto subida para enlazarla a la BBDD
    private fun addOnSuccessProvider() {
        mImageProvider.mStorageReference.downloadUrl.addOnSuccessListener {
            val url: String = it.toString()

            addOnCompleteListenerImageProvider(url)

        }
    }

    private fun addOnCompleteListenerImageProvider(url: String) {
        mImageProvider.save(mImageFile2).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                mImageProvider.mStorageReference.downloadUrl.addOnSuccessListener {
                    val url2 = it.toString()
                    // en este caso utilize !! pq en esta instancia ya logueado debe estar si o si el usuario
                    val idUser = mAuthProvider.getUid()!!

                    val post = Post(idUser, mTitle, url, url2, mCategory, mDescription)
                    addOnCompleteListenerProvider(post)

                }

            } else {
                mAlertDialog.dismiss()
                Toast.makeText(this, "La imagen 2 no Se pudo Cargar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addOnCompleteListenerProvider(post: Post) {
        mAlertDialog.dismiss()

        mPostProvider.save(post).addOnCompleteListener {
            if (it.isSuccessful) {
                clearForm()
                Toast.makeText(this, "La información se almaceno correctamente", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(
                    this,
                    "La información no se almaceno correctamente",
                    Toast.LENGTH_SHORT
                ).show()

            }

        }

    }

    //limpiar formulario
    private fun clearForm() {
        mTextInputTitle.setText("")
        mTextViewCategory.text = ""
        mTextInputDescription.setText("")
        mImageViewPost1.setImageResource(R.drawable.ico_foto)
        mImageViewPost2.setImageResource(R.drawable.ico_foto)
        mTitle = ""
        mCategory = ""
        mDescription = ""
    }


    private fun onclickImageViewPost1() {
        mImageViewPost1.setOnClickListener {
            selectOptionImage(GALLERY_RESQUEST_CODE)
        }
    }


    private fun onclickImageViewPost2() {
        mImageViewPost2.setOnClickListener {
            selectOptionImage(GALLERY_RESQUEST_CODE_2)
        }
    }

    private fun selectOptionImage(galeryRescuestCode: Int) {

        mBuilderSelector.setItems(option, DialogInterface.OnClickListener { dialog, i ->
            if (i == 0) {
                openGallery(galeryRescuestCode)
            }
            if (i == 1) {
                takePhoto()
            }
        })

        mBuilderSelector.show()

    }


    private fun takePhoto() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            //permiso no aceptado por el momento
            requestCamaraPermission()
        }else{
            //abrir camara
            openCamera()

        }

      /**
      val takePictureIntent : Intent = Intent()
        if (takePictureIntent.resolveActivity(packageManager) != null){
             var photoFile : File? = null
            try{
                photoFile = creaPhotoFile()
            }catch (e : Exception ){
                Toast.makeText(this, "hubo un error con el archivo:  " + e.message, Toast.LENGTH_SHORT).show()
            }

            if(photoFile != null){
                val photoUri : Uri = FileProvider.getUriForFile(this, "com.sosolution.socialtelecomunication" , photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(takePictureIntent, PHOTO_RESQUEST_CODE)

            }
        }
      **/
    }


    private fun requestCamaraPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
            //el usuario ha rechazado los permisos
            Toast.makeText(this, "Permisos Rechazados", Toast.LENGTH_SHORT).show()


        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_NUMBER)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_NUMBER){
            //nuestros permisos
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openCamera()
            }else{
                //el permiso no esta aceptado
                Toast.makeText(this, "Permisos rechazados por primera vez", Toast.LENGTH_SHORT).show()


            }
        }
    }


    private fun openCamera() {
        Toast.makeText(this, "Abriendo camara", Toast.LENGTH_SHORT).show()



    }


    private fun openGallery(requestCode : Int ) {

       val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent,requestCode)

    }

    private fun inicializarVistas() {

        //inicializar valores del post Activity
        mButtonPost = findViewById(R.id.btnPost)
        mTextInputTitle = findViewById(R.id.textInputPublication)
        mTextInputDescription = findViewById(R.id.textInputDescription)
        mTextViewCategory = findViewById(R.id.textViewCategory)
        mCircleImageback = findViewById(R.id.cicleImageBack)


        //inicializar valores de firebase
        mImageProvider = ImageProvider()
        mPostProvider = PostProvider()
        mAuthProvider = AuthProvider()

        //inicializar valores de imagenes del post Activity
        mImageViewPost1 = findViewById(R.id.imageViewPost1)
        mImageViewPost2 = findViewById(R.id.imageViewPost2)
        mImageViewPC = findViewById(R.id.imageViewPC)
        mImageViewPS = findViewById(R.id.imageViewPS)
        mImageViewNintendo = findViewById(R.id.imageViewNintendo)
        mImageViewXbox = findViewById(R.id.imageViewXbox)

        //alertDialog
        mAlertDialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Cargando  ...")
            .setCancelable(false).build()

        //builders
        mBuilderSelector = AlertDialog.Builder(this)
        mBuilderSelector.setTitle("Selecciona una opción")





    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /**
         * Seleccion de imagen de galeria
         */
        if(requestCode == GALLERY_RESQUEST_CODE && resultCode == RESULT_OK){
            try{

                    mImageFile1 = fileUtil.from(this, data?.data)!!
                mImageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile1!!.absolutePath))


            }catch (e : Exception){
                Log.d("ERROR", "Se produjo un error" + e.message)
                Toast.makeText(this,"Se produjo un error, no se cargo la imagen"+ e.message, Toast.LENGTH_LONG).show()

            }
        }

        if(requestCode == GALLERY_RESQUEST_CODE_2 && resultCode == RESULT_OK){
            try{

                mImageFile2 = fileUtil.from(this, data?.data)!!
                mImageViewPost2.setImageBitmap(BitmapFactory.decodeFile(mImageFile2!!.absolutePath))


            }catch (e : Exception){
                Log.d("ERROR", "Se produjo un error" + e.message)
                Toast.makeText(this,"Se produjo un error, no se cargo la imagen"+ e.message, Toast.LENGTH_LONG).show()

            }
        }


        /**
         * Seleccion de imagen de camara
         */


          if (requestCode == PHOTO_RESQUEST_CODE && resultCode == RESULT_OK){
            Picasso.with(this).load(mPhotoPath).into(mImageViewPost1)
        }

    }






}