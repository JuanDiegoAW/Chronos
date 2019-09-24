package com.example.login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    override fun onMarkerClick(p0: Marker?) = false

    //DECLARACIÓN DE VARIABLES GLOBALES
    private val usuario : Usuario = Usuario.iniciar()
    private var googleMap: GoogleMap? = null
    private lateinit var ubicacion_actual: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object
    {
        private const val PERMISO_UBICACION = 1

        fun getLaunchIntent(from: Context) = Intent(from, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    //GOOGLE MAPS
    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap

        this.googleMap?.uiSettings?.isZoomControlsEnabled = true
        this.googleMap?.setOnMarkerClickListener(this)

        setUpMap()
    }

    //INICIALIZACION DEL MAPA
    private fun setUpMap() {

        //SE REVISA SI EL USUARIO YA LE DIO PERMISO AL DISPOSITIVO PARA USAR LA UBICACIÓN DEL DISPOSITIVO.
        //SI NO LO HA HECHO, SOLICITA EL PERMISO.
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISO_UBICACION)

            //SI NO SE ACEPTA EL PERMISO, SE REGRESA DE LA FUNCION
            return
        }

        //DE LO CONTRARIO, ACTIVA LA UBICACION
        googleMap!!.isMyLocationEnabled = true

        //FUSEDLOCATIONCLIENT DA LA ULTIMA UBICACION DISPONIBLE DEL USUARIO
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->

            if (location != null) {
             //SI SE OBTUVO UNA UBICACION, CENTRAR EL MAPA
                ubicacion_actual = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
        FirebaseAuth.getInstance().signOut()
        if (usuario.getSesion() == 1)
        {
            cerrarSesionGoogle()
        }
        else
        {
            cerrarSesionFacebook()
        }
        startActivity(MainActivity.getLaunchIntent(this))
    }

    private fun cerrarSesionFacebook()
    {
        LoginManager.getInstance().logOut()
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
