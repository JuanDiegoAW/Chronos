package com.example.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class Usuario{

    companion object Static{
        fun iniciar(): Usuario = Usuario()
        private lateinit var nombre: String
        private lateinit var urlFoto: Uri
        private var sesion: Int = 0
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

    fun getNombre() : String
    {
        return  nombre
    }

    fun getUrlFoto(): Uri
    {
        return urlFoto
    }

    fun getSesion() : Int
    {
        return sesion
    }
}