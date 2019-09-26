package com.example.login.Clases

import android.location.Location
import android.net.Uri

class Usuario{

    companion object Static{
        fun iniciar(): Usuario = Usuario()
        private lateinit var nombre: String
        private lateinit var urlFoto: Uri
        private var sesion: Int = 0
        private lateinit var ubicacion: Location
        private var permisoUbicacion : Boolean = false
    }

    fun setNombre(nomb : String)
    {
        nombre = nomb
    }

    fun setUrlFoto(url : Uri)
    {
        urlFoto = url
    }

    fun setSesion(ses: Int)
    {
        sesion = ses
    }

    fun setLocation(locacion: Location)
    {
        permisoUbicacion = true
        ubicacion = locacion
    }

    fun getNombre() : String
    {
        return nombre
    }

    fun getUrlFoto(): Uri
    {
        return urlFoto
    }

    fun getSesion() : Int
    {
        return sesion
    }

    fun getLocation(): Location
    {
        return ubicacion
    }

    fun getPermiso() : Boolean
    {
        return permisoUbicacion
    }
}