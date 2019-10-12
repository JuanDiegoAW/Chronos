package com.example.login.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.login.Clases.Evento
import com.example.login.Clases.EventosDatos
import com.example.login.Clases.ParametrosEventos
import com.example.login.Clases.Servicio
import com.example.login.R
import kotlinx.android.synthetic.main.activity_informacion_evento.*

class InformacionEvento : AppCompatActivity() {

    private lateinit var codigo_evento: Evento
    private val adaptador_evento = ParametrosEventos.iniciar()
    private var servicio: Servicio = Servicio()
    private lateinit var evento : EventosDatos

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informacion_evento)
        codigo_evento = adaptador_evento.getEvento()
        obtenerEvento()
        setupUI()
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
            super.onBackPressed()
        }

        boton_Maps.setOnClickListener{
            adaptador_evento.setLatitud(evento.getLatitud())
            adaptador_evento.setLongitud(evento.getLongitud())

            val intent = Intent(this, Mapa::class.java)
            startActivity(intent)
        }
    }
}
