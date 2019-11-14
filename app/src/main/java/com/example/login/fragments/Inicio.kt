package com.example.login.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.example.login.Activities.InformacionEvento
import com.example.login.Activities.Mapa
import com.example.login.R
import com.example.login.Activities.ViewPageAdapter
import com.example.login.Clases.Evento
import com.example.login.Clases.EventosDatos
import com.example.login.Clases.ParametrosEventos
import com.example.login.Clases.Servicio
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_inicio.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Double
import java.lang.Math.pow
import kotlin.math.sqrt

class Inicio : Fragment(), OnMapReadyCallback {
    //Layouts
    private lateinit var viewPager: ViewPager
    private lateinit var layouts: IntArray
    private lateinit var puntosLayout : LinearLayout
    private lateinit var puntos: Array<ImageView>
    private lateinit var adaptador : ViewPageAdapter

    //Mapa
    private lateinit var mapa: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var ubicacion : Location? = null
    private var evento_mas_cercano : EventosDatos? = null
    private var nuevo_evento : EventosDatos? = null

    //API
    private var datos : ArrayList<EventosDatos> = ArrayList()
    private var servicio: Servicio = Servicio()

    override fun onMapReady(googleMap: GoogleMap?) {
        this.mapa = googleMap!!
        this.mapa.uiSettings?.isZoomControlsEnabled = true
        setUpMap()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        layouts = intArrayOf(R.layout.ventana_inicio_1, R.layout.ventana_inicio_2, R.layout.ventana_inicio_3)

        //Inicializamos valores una vez se haya creado la vista
        viewPager = view.findViewById(R.id.vpImagenes)
        adaptador = context?.let { ViewPageAdapter(layouts, it) }!!
        viewPager.adapter = adaptador
        puntosLayout = view.findViewById(R.id.puntos) as LinearLayout

        //Siempre se inicializara la vista en la primera pagina
        mostrarPuntos(0)

        //Se establece la funcion de los botones para cuando se cree la vista.
        botonIzquierda.setOnClickListener{
            val intent = Intent(activity, Mapa::class.java)
            startActivity(intent)
        }
        botonDerecha.setOnClickListener{
            if (evento_mas_cercano != null) {
                val evento_mostrar = ParametrosEventos.iniciar()
                evento_mostrar.setEvento(Evento(evento_mas_cercano!!.getCodigo(), "", "", "", ""))
                val intent = Intent(context, InformacionEvento::class.java)
                context!!.startActivity(intent)
            }
            else
            {
                Toast.makeText(context, "Por favor active la ubicacion para usar todas las funciones",Toast.LENGTH_SHORT).show()
            }
        }

        //Cuando se cambia de pagina ocurre alguna de las siguientes opciones:
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
                //Nada pasa si cambia el estado del scroll
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                //Nada pasa si se empieza a cambiar una pagina
            }

            override fun onPageSelected(position: Int) {
                //Cuando se selecciona una nueva pagina, se refleja en la barra de puntos
                mostrarPuntos(position)
                if (position == 0)
                {
                    setDatosVentanaUbicacion()
                }
                else if (position == 1)
                {
                    setDatosVentanaEventos()
                }
                else
                {
                    setDatosVentana3()
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    fun setDatosVentanaUbicacion()
    {
        botonIzquierda.text = "Explora el mapa!"
        setUpMapUbicacion()

        botonIzquierda.setOnClickListener{
            val intent = Intent(activity, Mapa::class.java)
            startActivity(intent)
        }
        botonDerecha.setOnClickListener{
            if (evento_mas_cercano != null) {
                val evento_mostrar = ParametrosEventos.iniciar()
                evento_mostrar.setEvento(Evento(evento_mas_cercano!!.getCodigo(), "", "", "", ""))
                val intent = Intent(context, InformacionEvento::class.java)
                context!!.startActivity(intent)
            }
            else
            {
                Toast.makeText(context, "Por favor active la ubicacion para usar todas las funciones",Toast.LENGTH_SHORT).show()
            }
        }

    }
    @SuppressLint("SetTextI18n")
    fun setDatosVentanaEventos()
    {
        botonIzquierda.text = "Explora eventos!"
        if (nuevo_evento != null) {
            setUpMapEvento()
        }
        else
        {
            nombre_evento.text = "Error obteniendo el evento"
            Toast.makeText(context, "Hubo un error obteniendo los eventos. Por favor intentelo de nuevo",Toast.LENGTH_SHORT).show()
        }

        botonIzquierda.setOnClickListener{
            println("algo")
        }
        botonDerecha.setOnClickListener{
            if (nuevo_evento != null) {
                val evento_mostrar = ParametrosEventos.iniciar()
                evento_mostrar.setEvento(Evento(nuevo_evento!!.getCodigo(), "", "", "", ""))
                val intent = Intent(context, InformacionEvento::class.java)
                context!!.startActivity(intent)
            }
            else
            {
                Toast.makeText(context, "Hubo un error obteniendo los eventos. Por favor intentelo de nuevo",Toast.LENGTH_SHORT).show()
            }
        }
    }
    @SuppressLint("SetTextI18n")
    fun setDatosVentana3()
    {
        botonIzquierda.text = ""
        botonIzquierda.setOnClickListener{

        }
    }

    fun setUpMap()
    {
        //SE REVISA SI EL USUARIO YA LE DIO PERMISO AL DISPOSITIVO PARA USAR LA UBICACIÓN DEL DISPOSITIVO.
        //SI NO LO HA HECHO, SOLICITA EL PERMISO.
        if (ActivityCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(activity!!,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISO_UBICACION
            )
            return
        }
        setUpMapUbicacion()
    }

    fun setUpMapUbicacion()
    {
        mapa.clear()
        mapa.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(activity!!) { location ->
            if (location != null)
            {
                ubicacion = location
                if (evento_mas_cercano == null)
                {
                    getData(location)
                }
                else
                {
                    mostrarEventoMasCercano()
                }
            }
            else
            {
                evento_mas_cercano = null

                val currentLatLng =  LatLng(15.5000000, -90.2500000)  // localizacion de Guatemala
                mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 7f))
                getData(null)

                nombre_evento.text = "Por favor active la ubicacion para habilitar todas las funciones"
            }
        }
    }

    fun setUpMapEvento()
    {
        //Se muestra el evento mas cercano en el mapa
        mapa.clear()
        val ubicacion_evento_nuevo = LatLng(Double.parseDouble(nuevo_evento!!.getLatitud()), Double.parseDouble(nuevo_evento!!.getLongitud()))
        mapa.addMarker(
            MarkerOptions().position(ubicacion_evento_nuevo)
                .title(nuevo_evento!!.getTitulo())
                .snippet(nuevo_evento!!.getDescripcion())
        )

        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(ubicacion_evento_nuevo, 15f))

        //Y luego se muestran los datos en texto
        nombre_evento.text = nuevo_evento!!.getTitulo()
    }

    fun mostrarPuntos(posicion : Int)
    {
        //Si hubiera una vista ya establecida, se quita ya que será reemplazada por una nueva vista en donde se refleje
        //El cambio de posicion de pagina
        puntosLayout.removeAllViews()

        puntos = Array(layouts.size, {i -> ImageView(context)})
        for (i in 0 until layouts.size)
        {
            //Si la posicion de la ventana a la que se cambio es igual a la posicion del punto a dibujar
            if (i == posicion)
            {
                //Se dibuja el punto como blanco
                puntos[i].setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.puntos_activos) })
            }
            else
            {
                //De lo contrario, el punto se dibuja gris
                puntos[i].setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.puntos_no_activos) })
            }

            //Se establece distanca entre los puntos
            val parametros : LinearLayout.LayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            parametros.setMargins(10,0,10,0)

            //Y finalmente esta vista se coloca en el Layout
            puntosLayout.addView(puntos[i], parametros)
        }
    }

    private fun getData(ubicacion_actual : Location?){
        try {
            val entrada = BufferedReader(InputStreamReader(servicio.metodoGet("eventos/fecha")))
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
            var jsonObject =JSONObject(json)//Acá obtengo el json con el array que devuelve la api
            val arrayJson = JSONArray(jsonObject.optString("results"))
            /**
             * Ciclo para ir sacando del array que tiene forma del json regresado y va a ir
             * almacenando en el arraylist
             */
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //    var fecha_actual = LocalDate.now()
            //}

            var distancia = 0.0
            for (i in 0 until arrayJson.length()) {
                val jsonObject: JSONObject = arrayJson.getJSONObject(i)

                val evento = EventosDatos(jsonObject.optString("codigo"),
                    jsonObject.optString("titulo"),
                    jsonObject.optString("descripcion"),
                    jsonObject.optString("imagenes"),
                    jsonObject.optString("calificacionP"))

                var texto_ubicacion = jsonObject.optString("rutaLugar")

                if("," in texto_ubicacion)
                {
                    var latitud_string = ""
                    var longitud_string = ""

                    //AQUI EMPIEZA LA FUNCION PARA SEPARAR LA UBICACION EN DOS VARIABLES: LATITUD Y LONGITUD
                    while(texto_ubicacion[0] != ',')
                    {
                        latitud_string += texto_ubicacion[0].toString()
                        texto_ubicacion = texto_ubicacion.takeLast(texto_ubicacion.length - 1)
                    }
                    texto_ubicacion = texto_ubicacion.takeLast(texto_ubicacion.length - 1)
                    longitud_string = texto_ubicacion

                    var aceptado = true
                    try {
                        Double.parseDouble(latitud_string)
                        Double.parseDouble(longitud_string)
                    } catch (e: NumberFormatException) {
                        aceptado = false
                    }
                    //AQUI TERMINA LA FUNCION

                    if(aceptado == true) {
                        evento.setLatitud(latitud_string)
                        evento.setLongitud(longitud_string)

                        datos.add(evento)

                        val latitud = Double.parseDouble(latitud_string)
                        val longitud = Double.parseDouble(longitud_string)

                        //Si estamos analizando el primer elemento, automaticamente lo colocamos como el evento mas cercano
                        if(i == 0 && ubicacion_actual != null)
                        {
                            //Obtenemos la distancia a la que se encuentra la logitud del evento con la longitud actual
                            distancia = sqrt( (pow(ubicacion_actual!!.latitude - latitud, 2.0)+(pow(ubicacion_actual!!.longitude - longitud, 2.0))) )
                            evento_mas_cercano = evento
                        }
                        //De lo contrario, comparamos la distancia del evento actual con la menor distancia encontrada anteriormente
                        else if (ubicacion_actual != null)
                        {
                            val distancia_provisional = sqrt( (pow(ubicacion_actual!!.latitude - latitud, 2.0)+(pow(ubicacion_actual!!.longitude - longitud, 2.0))) )
                            //Si la distancia del evento actual es menor, entonces se vuelve la distancia menor encontrada por el momento
                            if (distancia_provisional < distancia)
                            {
                                distancia = distancia_provisional
                                evento_mas_cercano = evento
                            }
                        }
                        //Si este fue el ultimo evento ingresado
                        if (i == arrayJson.length() - 1)
                        {
                            nuevo_evento = evento
                        }
                    }
                }
            }
            entrada.close()
            if (ubicacion_actual != null) {
                mostrarEventoMasCercano()
            }
        }
        catch (e: IOException)
        {
            Toast.makeText(context!!,"Verifique su conexión a internet", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
        catch (e: JSONException)
        {
            e.printStackTrace()
        }
        finally
        {
            servicio.desconectar()
        }
    }

    fun mostrarEventoMasCercano()
    {
        //Se muestra el evento mas cercano en el mapa
        val locacionEvento = LatLng(Double.parseDouble(evento_mas_cercano!!.getLatitud()), Double.parseDouble(evento_mas_cercano!!.getLongitud()))

        mapa.addMarker(
            MarkerOptions().position(locacionEvento)
                .title(evento_mas_cercano!!.getTitulo())
                .snippet(evento_mas_cercano!!.getDescripcion()))

        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(locacionEvento, 15f))

        //Y luego se muestran los datos en texto
        nombre_evento.text = evento_mas_cercano!!.getTitulo()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root_view = inflater.inflate(R.layout.fragment_inicio, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        mapFragment = childFragmentManager.findFragmentById(R.id.mapa_inicio) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        return root_view
    }

    companion object {
        const val PERMISO_UBICACION = 1
        var mapFragment : SupportMapFragment?=null
    }
}
