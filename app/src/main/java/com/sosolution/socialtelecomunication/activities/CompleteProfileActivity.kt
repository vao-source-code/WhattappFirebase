package com.sosolution.socialtelecomunication.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sosolution.socialtelecomunication.R
import dmax.dialog.SpotsDialog

class CompleteProfileActivity : AppCompatActivity() {

    //Declaracion de Componentes

    lateinit var rTextInputUsername: TextInputEditText
    lateinit var rBtnConfirmar: Button

    lateinit var rAuth: FirebaseAuth
    lateinit var rFirebaseFirestore: FirebaseFirestore

lateinit var  mAlertDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_profile)


        inicializarVistas()
        clickRegistrar()
        mAlertDialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Completando datos ...")
            .setCancelable(false).build()

    }


    //btnRegister Hacer accion de registar
    private fun clickRegistrar() {
        rBtnConfirmar.setOnClickListener {
            register()
        }
    }

    private fun register() {

        val userName = rTextInputUsername.text.toString()

        if (userName.isNotEmpty()) {
            updateUser(userName)
        } else {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_LONG).show()

        }
    }

    //una vez validado crea usuario
    private fun updateUser(userName: String) {
        val id: String = rAuth.currentUser?.uid ?: ""
        val map: MutableMap<String, Any> = mutableMapOf()
        map["username"] = userName
        updateFirebaseBBDD(map, id)



    }

    private fun updateFirebaseBBDD(map: MutableMap<String, Any>, id: String) {
        mAlertDialog.show()
        rFirebaseFirestore.collection("Users").document(id).update(map).addOnCompleteListener {
            if (it.isSuccessful) {
                mAlertDialog.dismiss()
                intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)

            } else {
                Toast.makeText(
                    this,
                    "El usuario  no se almacenó en la base de datos",
                    Toast.LENGTH_LONG
                ).show()

            }
        }
    }

    //ValidarPassword
    private fun isPasswordValid(password: String, confirmPassword: String): Boolean {
        if (password.equals(confirmPassword)) {
            if (password.length >= 6) {
                return true
            }
        }
        return false
    }


    //Inicializo los componentes que usare
    private fun inicializarVistas() {
        rTextInputUsername = findViewById(R.id.textInputUsername)

        rBtnConfirmar = findViewById(R.id.btnConfirmar)
        rAuth = FirebaseAuth.getInstance()
        rFirebaseFirestore = Firebase.firestore

    }
}


