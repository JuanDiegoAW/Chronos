package com.example.login.Activities

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.login.Clases.Servicio
import com.example.login.Clases.Usuario
import com.example.login.R
import com.facebook.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_inicio_sesion.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import org.json.JSONException
import org.json.JSONObject

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

    //Conexion API
    private val servicio: Servicio = Servicio()

    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, InicioSesion::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)

        //Configuracion de Google
        configureGoogleSignIn()
        setupUI()
        firebaseAuth = FirebaseAuth.getInstance()

        //Configuracion de Facebook
        auth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()
        setupFacebook()

    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null)
        {
            val usuario : Usuario = Usuario.iniciar()

            user.photoUrl?.let { usuario.setUrlFoto(it) }

            verificar(user)

            startActivity(VentanaPrincipal.getLaunchIntent(this))
            finish()
        }
        else
        {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("id_token")
                .requestEmail()
                .build()

            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
            if (mGoogleSignInClient != null)
            {
                mGoogleSignInClient.signOut()
            }
            if (LoginManager.getInstance()!= null)
            {
                LoginManager.getInstance().logOut()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onBackPressed() {
        finishAffinity()
        System.exit(0)
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
                    usuario.setSesion(1)
                    verificar(user)

                    //poner foto del usuario
                    user.photoUrl?.let { it1 -> usuario.setUrlFoto(it1) }

                        startActivity(VentanaPrincipal.getLaunchIntent(this))
                }
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
                        usuario.setSesion(2)
                        verificar(user)
                        user.photoUrl?.let { it1 -> usuario.setUrlFoto(it1) }
                        startActivity(VentanaPrincipal.getLaunchIntent(this))
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

    private fun crear(user:FirebaseUser){
        try {
            val datos = JSONObject()
            datos.put("nombre",user.displayName)
            datos.put("correo",user.email)
            datos.put("contrasena","1234")

            user.displayName?.let { usuario.setNombre(it) }
            user.email?.let { usuario.setCorreo(it) }

            var mensaje:String="Error al crear el usuario"
            if(servicio.metodoPost("usuarios", datos))
                mensaje="Usuario creado correctamente"
            Toast.makeText(this, mensaje,Toast.LENGTH_SHORT).show()
        }
        catch (e: JSONException)
        {
            e.printStackTrace()
        }
    }
    private fun verificar(user:FirebaseUser)
    {
        val jsonObject=servicio.metodoGetBusqueda("usuarios","correo="+ user.email)
        if (jsonObject.length()==1)
            crear(user)
        else
        {
            usuario.setNombre(jsonObject.optString("nombre"))
            user.email?.let { usuario.setCorreo(it) }
        }
        servicio.desconectar()
    }
}
