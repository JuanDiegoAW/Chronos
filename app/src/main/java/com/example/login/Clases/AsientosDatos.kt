package com.example.login.Clases

class AsientosDatos {
    private var disponible=true
    private var numeroAsiento=""

    constructor(disponible: Boolean, numeroAsiento: String) {
        this.disponible = disponible
        this.numeroAsiento = numeroAsiento
    }
    fun isDisponible(): Boolean{
        return this.disponible
    }
    fun getNumeroAsiento():String{
        return this.numeroAsiento
    }
}