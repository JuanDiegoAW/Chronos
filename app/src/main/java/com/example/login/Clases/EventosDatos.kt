package com.example.login.Clases

class EventosDatos {
    private var codigo:String=""
    private var titulo:String=""
    private var descripcion:String=""
    private var imagenes:String=""
    private var longitud: String=""
    private var latitud: String=""

    constructor(codigo: String,titulo: String,descripcion: String,imagenes: String){
        this.codigo=codigo
        this.titulo=titulo
        this.descripcion=descripcion
        this.imagenes=imagenes
    }

    fun setLongitud(longitud: String)
    {
        this.longitud = longitud
    }

    fun setLatitud(latitud: String)
    {
        this.latitud = latitud
    }

    fun getCodigo():String{
        return this.codigo
    }
    fun getTitulo():String{
        return this.titulo
    }
    fun getDescripcion():String{
        return this.descripcion
    }
    fun getImagenes():String{
        return this.imagenes
    }

    fun getLongitud():String{
        return this.longitud
    }

    fun getLatitud():String{
        return this.latitud
    }
}