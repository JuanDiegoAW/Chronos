package com.example.login

import android.hardware.camera2.TotalCaptureResult
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.login.Clases.Evento
import com.example.login.Clases.ParametrosEventos
import com.example.login.Clases.Usuario
import kotlinx.android.synthetic.main.activity_pagos_reservas.*
import org.json.JSONArray

class ActivityPagosReservas : AppCompatActivity() {

    private val usuario = Usuario.iniciar()
    private val adaptador_evento = ParametrosEventos.iniciar()
    lateinit var evento1 : Evento
    private var lista =JSONArray()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagos_reservas)
        val datos= intent.getStringExtra("datos")
        this.lista=JSONArray(datos)
        var total=0.0
        for (i in 0 until this.lista.length()){
            println(this.lista.getJSONObject(i).optString("asiento"))
            println(this.lista.getJSONObject(i).optString("localidad"))
            println(this.lista.getJSONObject(i).optString("precio"))
            total+=this.lista.getJSONObject(i).optDouble("precio")
        }
        println("Total->$total")
        evento1 = adaptador_evento.getEvento()
        txtCodigo.setText(usuario.getCodigo())
        txtNombre.setText(usuario.getNombre())
        txtCorreo.setText(usuario.getCorreo())
        txtCodigoEvento.setText(evento1.codigo)

    }
}
