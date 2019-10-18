package com.example.login.Clases

import android.net.Uri

class Usuario{

    companion object Static{
        fun iniciar(): Usuario = Usuario()
        private lateinit var codigo: String
        private lateinit var nombre: String
        private lateinit var correo: String
        private lateinit var uriFoto: Uri
        private var sesion: Int = 0
    }

    fun setCodigo(cod: String)
    {
        codigo = cod
    }

    fun getCodigo() : String
    {
        return codigo
    }

    fun setNombre(nomb : String)
    {
        nombre = nomb
    }

    fun setCorreo(corr: String)
    {
        correo = corr
    }

    fun setUrlFoto(url : Uri)
    {
        uriFoto = url
    }

    fun setSesion(ses: Int)
    {
        sesion = ses
    }

    fun getNombre() : String
    {
        return nombre
    }

    fun getUrlFoto(): Uri
    {
        return uriFoto
    }

    fun getSesion() : Int
    {
        return sesion
    }


    fun getCorreo(): String
    {
        return correo
    }
}