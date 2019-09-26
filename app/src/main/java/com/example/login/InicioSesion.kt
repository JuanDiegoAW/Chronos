package com.example.login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.login.Clases.Usuario
import com.facebook.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider

class InicioSesion : AppCompatActivity() {

    private val RC_SIGN_IN: Int = 1
    private val usuario = Usuario.iniciar()

    private lateinit var auth: FirebaseAuth

    //google
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mGoogleSignInOptions: GoogleSignInOptions

    //ubicacion
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //firebase
    private lateinit var firebaseAuth: FirebaseAuth

    //fb
    private lateinit var callbackManager: CallbackManager

    companion object {

        private const val PERMISO_UBICACION = 1

        fun getLaunchIntent(from: Context) = Intent(from, InicioSesion::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Configuracion de Google
        configureGoogleSignIn()
        setupUI()
        firebaseAuth = FirebaseAuth.getInstance()

        //Configuracion de Facebook
        auth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()
        setupFacebook()

        //GOOGLE MAPS
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        obtenerUbicacion()
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null)
        {
            val usuario : Usuario = Usuario.iniciar()
            user.displayName?.let { usuario.setNombre(it) }
            user.photoUrl?.let { usuario.setUrlFoto(it) }

            obtenerUbicacion()

            startActivity(MenuLateral.getLaunchIntent(this))
            finish()
        }
    }

    private fun obtenerUbicacion() {

        //SE REVISA SI EL USUARIO YA LE DIO PERMISO AL DISPOSITIVO PARA USAR LA UBICACIÓN DEL DISPOSITIVO.
        //SI NO LO HA HECHO, SOLICITA EL PERMISO.
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISO_UBICACION
            )

            //SI NO SE ACEPTA EL PERMISO, SE REGRESA DE LA FUNCION
            return
        }

        //FUSEDLOCATIONCLIENT DA LA ULTIMA UBICACION DISPONIBLE DEL USUARIO
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->

            if (location != null) {
                //SI SE OBTUVO UNA UBICACION, CENTRAR EL MAPA
                usuario.setLocation(location)
            }
        }
    }

    private fun configureGoogleSignIn()
    {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    private fun setupUI()
    {
        google_button.setOnClickListener{
            signInGoogle()
        }
    }

    private fun setupFacebook()
    {
        facebookLogin.setOnClickListener{
            signInFacebook()
        }
    }

    private fun signInFacebook()
    {
        @Suppress("DEPRECATION")
        facebookLogin.setReadPermissions("email", "public_profile")
        LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult>
                {
                    override fun onSuccess(result: LoginResult) {
                        handleFacebookAccessToken(result.accessToken)
                    }

                    override fun onCancel() {
                        Toast.makeText(baseContext, "Inicio cancelado.",
                            Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(error: FacebookException?) {
                        Toast.makeText(baseContext, "Inicio fallido.",
                            Toast.LENGTH_SHORT).show()
                    }
                })
    }

    private fun signInGoogle()
    {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try
            {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            }
            catch (e: ApiException)
            {
                Toast.makeText(this, "Inicio cancelado", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful)
            {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null)
                {
                    //poner nombre del usuario
                    user.displayName?.let { it1 -> usuario.setNombre(it1) }

                    //poner foto del usuario
                    user.photoUrl?.let { it1 -> usuario.setUrlFoto(it1) }

                    usuario.setSesion(1)
                }

                startActivity(MenuLateral.getLaunchIntent(this))
            }
            else
            {
                Toast.makeText(this, "Fallo de inicio de sesión", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun handleFacebookAccessToken(token: AccessToken) {

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null)
                    {
                        user.displayName?.let { it1 -> usuario.setNombre(it1) }
                        user.photoUrl?.let { it1 -> usuario.setUrlFoto(it1) }
                        usuario.setSesion(2)
                        startActivity(MenuLateral.getLaunchIntent(this@InicioSesion))
                    }
                }
                else
                {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Inicio fallido.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}
