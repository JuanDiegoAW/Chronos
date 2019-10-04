package com.example.login.Activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.example.login.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_mapa.*

class Mapa : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener  {

    override fun onMarkerClick(p0: Marker?) = false

    //ubicacion
    private var googleMap: GoogleMap? = null
    private lateinit var ubicacion_actual: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {

        private const val PERMISO_UBICACION = 1

        fun getLaunchIntent(from: Context) = Intent(from, InicioSesion::class.java).apply {
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

    private fun setupUI()
    {
        boton_regresar.setOnClickListener{
            regresarMenu()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //GOOGLE MAPS
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapa_fragmento) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Boton Regresar
        setupUI()
    }

    //INICIALIZACION DEL MAPA
    private fun setUpMap() {

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

        //DE LO CONTRARIO, ACTIVA LA UBICACION
        googleMap!!.isMyLocationEnabled = true

        //FUSEDLOCATIONCLIENT DA LA ULTIMA UBICACION DISPONIBLE DEL USUARIO
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->

            if (location != null) {
                //SI SE OBTUVO UNA UBICACION, CENTRAR EL MAPA
                ubicacion_actual = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }
            else
            {
                val currentLatLng = LatLng(15.5000000, -90.2500000)  // localizacion de Guatemala
                googleMap!!.addMarker(MarkerOptions().position(currentLatLng).title("Guatemala"))
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 7f))
            }
        }
    }

    private fun regresarMenu()
    {
        startActivity(MenuLateral.getLaunchIntent(this))
        finish()
    }

    override fun onBackPressed() {
        startActivity(MenuLateral.getLaunchIntent(this))
        finish()
    }
}
