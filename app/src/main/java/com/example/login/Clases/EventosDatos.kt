package com.example.login.Clases

class EventosDatos {
    private var codigo:String=""
    private var titulo:String=""
    private var descripcion:String=""
    private var imagenes:String=""
    constructor(codigo: String,titulo: String,descripcion: String,imagenes: String){
        this.codigo=codigo
        this.titulo=titulo
        this.descripcion=descripcion
        this.imagenes=imagenes
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
}