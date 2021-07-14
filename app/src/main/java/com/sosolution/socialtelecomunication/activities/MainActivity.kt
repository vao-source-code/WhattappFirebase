package com.sosolution.socialtelecomunication.activities

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.sosolution.socialtelecomunication.R
import com.sosolution.socialtelecomunication.providers.AuthProvider
import dmax.dialog.SpotsDialog
import java.util.regex.Matcher
import java.util.regex.Pattern


enum class ProviderType {
    BASIC, GOOGLE
}

class MainActivity : AppCompatActivity() {
    //Declaracion de Componentes
    lateinit var mTextViewRegister: TextView
    lateinit var mTextInputEmail: TextInputEditText
    lateinit var mTextInputPassword: TextInputEditText
    lateinit var mBtnLogin: Button
     lateinit var mAuthProvider: AuthProvider
    lateinit var mBtnLoginGoogle: SignInButton
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var mFirebaseFirestore: FirebaseFirestore
    lateinit var mAlertDialog: AlertDialog

    val  GOOGLE_SIGN_IN : Int  = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inicializarVistas()

        //inicio google autentificacion

        mFirebaseFirestore = FirebaseFirestore.getInstance()
        mAuthProvider = AuthProvider()
        mAlertDialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Cargando ...")
            .setCancelable(false).build()

        iniciarSesionGoogle()



        clickRegistrarActivity()
        clickIniciarSesion()
    }

    private fun iniciarSesionGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)


        mBtnLoginGoogle.setOnClickListener {
            signInGoogle()
        }

    }


    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }

    }

    private fun firebaseAuthWithGoogle(account : GoogleSignInAccount) {
        mAlertDialog.show()
        mAuthProvider.googleLogin(account).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val id = mAuthProvider.getUid()

                    if (id != null) {
                        checkUserExist(id)
                    }
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    showHomeActivity()

                } else {

                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }

    }

    private fun checkUserExist(id: String) {
        mFirebaseFirestore.collection("Users").document(id).get().addOnSuccessListener { it ->

            if(it.exists()){
                mAlertDialog.dismiss()
                showHomeActivity()
            }else{
                usuarioNuevoGoogle(id)

            }
        }
    }

    private fun usuarioNuevoGoogle(id : String) {
        val email = mAuthProvider.getEmail()

        val map : MutableMap<String,String> = mutableMapOf()
        if(email !=null){
            map["email"] = email

        }
        mFirebaseFirestore.collection("Users").document(id).set(map).addOnCompleteListener {
            if(it.isSuccessful){
                mAlertDialog.dismiss()

                intent = Intent(this, CompleteProfileActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this, "No se pudo almacenar la informacion del usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Inicializo los componentes que usare
    private fun inicializarVistas() {
        mTextViewRegister = findViewById(R.id.textViewRegister);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mBtnLogin = findViewById(R.id.btnLogin);
        mBtnLoginGoogle = findViewById(R.id.btnLoginGoogle)


    }

    //Voy  a la pantalla Register Activity
    private fun clickRegistrarActivity() {
        mTextViewRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }


    private fun clickIniciarSesion() {
        mBtnLogin.setOnClickListener {
            login()
        }
    }






    private fun login() {

        val email = mTextInputEmail.text.toString();
        val password = mTextInputPassword.text.toString();
        Log.d("Campo", "email:$email")
        Log.d("Campo", "password:$password")

        mAlertDialog.show()
        if (email.isNotEmpty() && password.isNotEmpty()) {

            if (isEmailValid(email)) {
                mAuthProvider.login(email,password).addOnCompleteListener {
                    mAlertDialog.dismiss()
                    if (it.isSuccessful) {
                            Toast.makeText(this, "pase por aca ", Toast.LENGTH_LONG).show()

                            showHomeActivity()
                        } else {

                            Toast.makeText(this, "El email o contraseña no son correctas ", Toast.LENGTH_LONG).show()
                        }

                    }
            }
        } else {
            Toast.makeText(this, "Error al procesar los campos", Toast.LENGTH_LONG)
                .show()
        }
        mAlertDialog.dismiss()


    }

    //ejecuta vista de Home activity
    private fun showHomeActivity() {

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    //valida email si posee los caracteres necesarios
    private fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }

}