package com.example.login.Activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.login.Clases.EventosDatos
import com.example.login.Clases.Servicio
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
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.time.LocalDate

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class Mapa : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener  {

    override fun onMarkerClick(p0: Marker?) = false

    //ubicacion
    private var googleMap: GoogleMap? = null
    private lateinit var ubicacion_actual: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var datos : ArrayList<EventosDatos> = ArrayList()
    private var servicio: Servicio = Servicio()

    companion object {

        private const val PERMISO_UBICACION = 1

        fun getLaunchIntent(from: Context) = Intent(from, Mapa::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    //GOOGLE MAPS
    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap

        this.googleMap?.uiSettings?.isZoomControlsEnabled = true
        this.googleMap?.setOnMarkerClickListener(this)

        //Inicializar mapa
        setUpMap()

        //Mostrar eventos
        showData()
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

        //Obtener eventos
        getData()
    }

    private fun showData()
    {
        for (i in 0 until datos.size)
        {
            val latitud: Double = datos[i].getLatitud().toDouble()
            val longitud: Double = datos[i].getLongitud().toDouble()

            val currentLatLng = LatLng(latitud, longitud)  // localizacion del evento
            googleMap!!.addMarker(MarkerOptions().position(currentLatLng).title(datos[i].getTitulo()))
        }
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
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 7f))
            }
        }
    }

    private fun getData(){
        try {
            val entrada = BufferedReader(InputStreamReader(servicio.metodoGet("eventos")))
            val respuesta = StringBuffer()
            //Ciclo para ir leyendo línea por línea e ir agregarlo en respuesta
            var linea : String?
            do {
                linea = entrada.readLine()
                if (linea == null) {
                    break
                }
                respuesta.append(linea)
            } while (true)
            val json: String
            //paso a un string el json que tengo para posteriormente manipularlo
            json = respuesta.toString()
            val arrayJson = JSONArray(json)
            /**
             * Ciclo para ir sacando del array que tiene forma del json regresado y va a ir
             * almacenando en el arraylist
             */
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //    var fecha_actual = LocalDate.now()
            //}

            for (i in 0 until arrayJson.length()) {
                val jsonObject: JSONObject = arrayJson.getJSONObject(i)

                val evento: EventosDatos = EventosDatos(jsonObject.optString("codigo"),
                    jsonObject.optString("titulo"),
                    jsonObject.optString("descripcion"),
                    jsonObject.optString("imagenes"))

                var texto_ubicacion = jsonObject.optString("rutaLugar")
                println("TEXTOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO: " + texto_ubicacion)

                if("," in texto_ubicacion)
                {
                    var latitud = ""
                    var longitud: String
                    var j = 0

                    while(texto_ubicacion[j].toString() != ",")
                    {
                        latitud += texto_ubicacion[j].toString()
                        texto_ubicacion = texto_ubicacion.takeLast(texto_ubicacion.length - 1)
                    }
                    texto_ubicacion = texto_ubicacion.takeLast(texto_ubicacion.length - 1)
                    longitud = texto_ubicacion

                    evento.setLatitud(latitud)
                    evento.setLongitud(longitud)

                    datos.add(evento)
                }
            }
            entrada.close()
        } catch (e: IOException) {
            Toast.makeText(this,"Verifique su conexión a internet", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }catch (e: JSONException) {
            e.printStackTrace()
        }finally {
            servicio.desconectar()
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
