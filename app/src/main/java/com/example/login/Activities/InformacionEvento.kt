package com.example.login.Activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login.Clases.*
import com.example.login.R
import kotlinx.android.synthetic.main.activity_informacion_evento.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class InformacionEvento : AppCompatActivity() {

    private val usuario = Usuario.iniciar()
    private lateinit var codigo_evento: Evento
    private val adaptador_evento = ParametrosEventos.iniciar()
    private var servicio: Servicio = Servicio()
    private lateinit var evento : EventosDatos
    private var comentarios : ArrayList<Comentario> = ArrayList()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informacion_evento)
        codigo_evento = adaptador_evento.getEvento()
        obtenerEvento()
        setupUI()
        getDataComentarios()

        refreshComentarios.setOnRefreshListener {
            comentarios.clear()
            getDataComentarios()
        }
        boton_reservar.setOnClickListener {
            val intento1 = Intent(this, Reservas::class.java)
            startActivity(intento1)
        }

    }

    private fun getDataComentarios()
    {
        refreshComentarios.isRefreshing = true

        var entrada = BufferedReader(InputStreamReader(servicio.metodoGetBusquedaArray("comentarios", "codigo="+adaptador_evento.getEvento().codigo)))
        var respuesta = StringBuffer()
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

        for (i in 0 until arrayJson.length()) {

            val jsonObject: JSONObject = arrayJson.getJSONObject(i)

            comentarios.add(
                Comentario(
                    jsonObject.optString("descripcion"),
                    jsonObject.optString("calificacion"),
                    jsonObject.optString("idUsuario")
                )
            )
        }
        showDataComentarios()
        entrada.close()

        refreshComentarios.isRefreshing = false
    }

    private fun showDataComentarios()
    {
        ordenarComentarios()
        recyclerComentariosEventos.layoutManager = LinearLayoutManager(this)
        //Luego mandamos esa lista al adaptador para asi enlazarlo con el xml del evento
        recyclerComentariosEventos.adapter = AdaptadorComentarios(comentarios, this)
    }

    private fun ordenarComentarios()
    {
        comentarios.reverse()
    }

    private fun obtenerEvento()
    {
        val jsonObject=servicio.metodoGetBusqueda("eventos", "codigo=" + adaptador_evento.getEvento().codigo)

        val evento = EventosDatos(jsonObject.optString("codigo"),
            jsonObject.optString("titulo"),
            jsonObject.optString("descripcion"),
            jsonObject.optString("imagenes"),
            jsonObject.optString("calificacionP"))

        evento.setFecha(jsonObject.optString("fecha"))
        evento.setDireccion(jsonObject.optString("direccion"))
        evento.setHora(jsonObject.optString("hora"))

        var texto_ubicacion = jsonObject.optString("rutaLugar")

        if("," in texto_ubicacion) {
            var latitud = ""
            val longitud: String

            //AQUI EMPIEZA LA FUNCION PARA SEPARAR LA UBICACION EN DOS VARIABLES: LATITUD Y LONGITUD
            while (texto_ubicacion[0] != ',') {
                latitud += texto_ubicacion[0].toString()
                texto_ubicacion = texto_ubicacion.takeLast(texto_ubicacion.length - 1)
            }
            texto_ubicacion = texto_ubicacion.takeLast(texto_ubicacion.length - 1)
            longitud = texto_ubicacion
            //AQUI TERMINA LA FUNCION

            evento.setLatitud(latitud)
            evento.setLongitud(longitud)
            this.evento = evento
        }
        servicio.desconectar()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupUI()
    {
        //Poner la parte visual
        tituloEventoSeleccionado.text = evento.getTitulo()
        descripcionEventoSeleccionado.text = evento.getDescripcion()
        direccionEventoSeleccionado.text = evento.getDireccion()
        horaEventoSeleccionado.text = evento.getHora()
        fechaEventoSeleccionado.text = evento.getFecha()

        //Poner los botones
        boton_regresar.setOnClickListener{
            val intent = Intent(this, VentanaPrincipal::class.java)
            startActivity(intent)
        }

        boton_Maps.setOnClickListener{
            adaptador_evento.setLatitud(evento.getLatitud())
            adaptador_evento.setLongitud(evento.getLongitud())

            val intent = Intent(this, Mapa::class.java)
            startActivity(intent)
        }

        boton_comentar.setOnClickListener{
            comentar()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun comentar()
    {
        val texto_a_comentar = texto_comentario.text.toString()

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        val fecha = current.format(formatter)

        try {
            val datos = JSONObject()
            datos.put("descripcion",texto_a_comentar)
            datos.put("calificacion","5")
            datos.put("codigoEvento",adaptador_evento.getEvento().codigo)
            datos.put("idUsuario",usuario.getCodigo())

            var mensaje ="Error al crear el comentario"
            if(servicio.metodoPost("comentarios/?codigo="+adaptador_evento.getEvento().codigo, datos))
            {
                mensaje = "Comentario posteado correctamente"
                texto_comentario.setText("")
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                comentarios.clear()
                getDataComentarios()
            }
        }
        catch (e: JSONException)
        {
            e.printStackTrace()
        }
    }
}
