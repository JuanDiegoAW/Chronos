package com.example.login.Clases

class ParametrosEventos {

    companion object Static{
        fun iniciar(): ParametrosEventos = ParametrosEventos()
        private lateinit var eventoMostrar: Evento
        private var latitud: String = ""
        private var longitud: String = ""
    }

    fun setEvento(evento: Evento)
    {
        eventoMostrar = evento
    }

    fun getEvento() : Evento
    {
        return eventoMostrar
    }

    fun setLatitud(latitud_evento : String)
    {
        latitud = latitud_evento
    }

    fun setLongitud(longitud_evento : String)
    {
        longitud = longitud_evento
    }

    fun getLatitud() : String
    {
        return latitud
    }

    fun getLongitud() : String
    {
        return longitud
    }

    fun limpiarUbicacion()
    {
        latitud = ""
        longitud = ""
    }

}