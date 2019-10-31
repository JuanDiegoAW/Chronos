package com.example.login.Clases

class AsientosDatos {
    private var disponible=true
    private var numeroAsiento=""
    private var idAsiento=0
    constructor(disponible: Boolean, numeroAsiento: String,idAsiento:Int) {
        this.disponible = disponible
        this.numeroAsiento = numeroAsiento
        this.idAsiento = idAsiento
    }
    fun isDisponible(): Boolean{
        return this.disponible
    }
    fun getNumeroAsiento():String{
        return this.numeroAsiento
    }
    fun getIdAsiento():Int{
        return this.idAsiento
    }
}