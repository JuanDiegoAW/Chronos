package com.example.login.Clases

import android.app.Application
import android.content.Context
import android.os.StrictMode
import android.provider.ContactsContract
import android.widget.Toast
import org.json.JSONException
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class Servicio {
    private val urlApi:String="http://edvfelipe.pythonanywhere.com/api/"
    private var conexion: HttpURLConnection? = null

     constructor(){
        //Esto lo hago para que el dispositivo tenga los permisos para consumir la api
        val policy : StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    /**
     * Método get que recibe como parametro un string
     * el cual ya tiene la url de la api y solo extraeremos
     * los datos según el link que se ingrese
     *
     */
    fun metodoGet(servicio:String) : InputStream? {
        var url = URL(this.urlApi + servicio + "/")
        this.conexion = url.openConnection() as HttpURLConnection
        this.conexion!!.requestMethod = "GET"
        this.conexion!!.connect()

        return if(this.conexion!!.responseCode==200)
            this.conexion!!.inputStream
        else
            null
    }
    fun metodoPost(servicio: String,datos : HashMap<String,String>): Unit {
        /**
        var url = URL(this.urlApi + servicio + "/")
        this.conexion = url.openConnection() as HttpURLConnection
        this.conexion!!.requestMethod = "POST"
        this.conexion!!.setRequestProperty("Content-Type", "application/json; utf-8")
        this.conexion!!.setRequestProperty("Accept", "application/json")
        this.conexion!!.doOutput= true
        **/
        for (dato in datos){
            println(" "+dato.key+" "+dato.value)
        }

    }
    /**
     * Desconecta el servicio con el servidor
     */
    fun desconectar(){
        this.conexion?.disconnect()
    }
}