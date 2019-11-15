package com.example.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.login.Clases.Evento
import com.example.login.Clases.ParametrosEventos
import com.example.login.Clases.Usuario
import kotlinx.android.synthetic.main.activity_pagos_reservas.*

class ActivityPagosReservas : AppCompatActivity() {

    private val usuario = Usuario.iniciar()
    private val adaptador_evento = ParametrosEventos.iniciar()
    lateinit var evento1 : Evento

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagos_reservas)

        evento1 = adaptador_evento.getEvento()
        txtCodigo.setText(usuario.getCodigo())
        txtNombre.setText(usuario.getNombre())
        txtCorreo.setText(usuario.getCorreo())
        txtCodigoEvento.setText(evento1.codigo)

    }
}
