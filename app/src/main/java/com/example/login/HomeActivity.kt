package com.example.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    //DECLARACIÓN DE VARIABLES
    private val usuario : Usuario = Usuario.iniciar()
    private var googleMap: GoogleMap? = null

    //GOOGLE MAPS
    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //DONDE SE ASIGNAN LOS BOTONES A LOS COMPONENTES
        setupUI()

        //GOOGLE MAPS
        val mapFragment = supportFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    //SE OBTIENE EL USUARIO ACTUAL Y SE ASIGNA A LA VARIABLE GLOBAL "usuario"
    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null)
        {
            user.displayName?.let { usuario.setNombre(it) }
            user.photoUrl?.let { usuario.setUrlFoto(it) }

            //SE MUESTRAN LOS DATOS GRAFICAMENTE
            ponerDatos()
        }
    }

    companion object
    {
        fun getLaunchIntent(from: Context) = Intent(from, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    //FUNCION PARA MOSTRAR LOS DATOS GRAFICAMENTE
    private fun ponerDatos()
    {
        nombreUsuario.text = usuario.getNombre()
        Glide.with(this).load(usuario.getUrlFoto()).into(imagenUsuario)
    }

    //ASIGNACION DE FUNCIONES A COMPONENTES
    private fun setupUI()
    {
        cerrarSesion.setOnClickListener {
            signOutApp()
        }
    }

    //SE DETECTA SI INICIÓ SESIÓN CON GOOGLE O FACEBOOK
    private fun signOutApp()
    {
        startActivity(MainActivity.getLaunchIntent(this))
        FirebaseAuth.getInstance().signOut()
        if (usuario.getSesion() == 1)
        {
            cerrarSesionGoogle()
        }
        else
        {
            cerrarSesionFacebook()
        }
    }

    private fun cerrarSesionFacebook()
    {
        LoginManager.getInstance().logOut();
    }

    private fun cerrarSesionGoogle()
    {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("id_token")
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mGoogleSignInClient.signOut()
    }
}