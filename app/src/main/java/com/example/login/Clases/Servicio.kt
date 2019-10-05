package com.example.login.Clases

import android.os.StrictMode
import org.json.JSONException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class Servicio {
    private val urlApi:String="http://edvfelipe.pythonanywhere.com/api/"
    private var url:URL?=null
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
        try {
            this.url = URL(this.urlApi + servicio + "/")
            this.conexion = this.url!!.openConnection() as HttpURLConnection
            this.conexion!!.requestMethod = "GET"
            this.conexion!!.connect()
            return this.conexion!!.inputStream
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Desconecta el servicio con el servidor
     */
    fun desconectar(){
        this.conexion?.disconnect()
    }
}