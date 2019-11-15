package com.example.login.Clases

class AsientosDatos(
    private var idAsiento: Int,
    private var precio:Double,
    private var numeroAsiento: String,
    private var localidad: String
) {
    fun getId(): Int{
        return this.idAsiento
    }
    fun getPrecio(): Double{
        return this.precio
    }
    fun getNumeroAsiento():String{
        return this.numeroAsiento
    }
    fun getLocalidad(): String{
        return this.localidad
    }
}