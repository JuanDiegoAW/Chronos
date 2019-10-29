package com.example.login.Clases

import android.os.StrictMode
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
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
    fun metodoGet(link:String) : InputStream? {
        var url = URL(this.urlApi + link + "/")
        this.conexion = url.openConnection() as HttpURLConnection
        this.conexion!!.requestMethod = "GET"
        this.conexion!!.connect()

        return if(this.conexion!!.responseCode==200)
            this.conexion!!.inputStream
        else
            null
    }

    /**
     * Éste método nos ayuda a obtener un array de JSON para el cual podemos obtener
     * información según se solicite, por ejemplo la lista de localidades de un evento o los
     * asientos de dicho evento.
     */
    fun metodoGetBusquedaArray(link:String,id:String) : InputStream? {
        var url = URL(this.urlApi + link+"/?"+id)
        this.conexion = url.openConnection() as HttpURLConnection
        this.conexion!!.requestMethod = "GET"
        this.conexion!!.connect()

        return if(this.conexion!!.responseCode==200)
            this.conexion!!.inputStream
        else
            null
    }

    /**
     * Método post, se utiliza para enviarle al servidor un objeto de tipo JSON, estó nos sirver
     * por ejemplo enviar la información del usuario para validar si este ya existe o no en la api
     */
    fun metodoPost(link: String,datos : JSONObject): Boolean {

        val url = URL("$urlApi$link")
        this.conexion = url.openConnection() as HttpURLConnection
        this.conexion!!.doOutput=true
        this.conexion!!.requestMethod = "POST"
        this.conexion!!.setRequestProperty("Content-Type", "application/json")
        this.conexion!!.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        try {
            val os = this.conexion!!.outputStream
            os.write(datos.toString().toByteArray())
            os.close()
            val br = BufferedReader(InputStreamReader(this.conexion!!.inputStream))
            var linea : String?
            println(this.conexion!!.responseCode)
            do {
                linea = br.readLine()
                if (linea == null) {
                    break
                }
                println(linea)
            } while (true)
            return true
        }
        catch (e : IOException) {
            e.printStackTrace()
        }
        return false
    }

    fun metodoPut(link: String,datos : JSONObject): Boolean {

        val url = URL("$urlApi$link")
        this.conexion = url.openConnection() as HttpURLConnection
        this.conexion!!.doOutput=true
        this.conexion!!.requestMethod = "PUT"
        this.conexion!!.setRequestProperty("Content-Type", "application/json")
        this.conexion!!.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        try {
            val os = this.conexion!!.outputStream
            os.write(datos.toString().toByteArray())
            os.close()
            val br = BufferedReader(InputStreamReader(this.conexion!!.inputStream))
            var linea : String?
            println(this.conexion!!.responseCode)
            do {
                linea = br.readLine()
                if (linea == null) {
                    break
                }
                println(linea)
            } while (true)
            return true
        }
        catch (e : IOException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * Método que utilizamos cuando el servidor nos va a retornar un objeto de tipo JSON
     * Como por ejemplo al hacer click a un evento solicitamos su información en concreto!
     */
    fun metodoGetBusqueda(link:String,id:String):JSONObject{
        try {
            var url = URL(this.urlApi + link+"/?"+id)
            this.conexion = url.openConnection() as HttpURLConnection
            this.conexion!!.requestMethod = "GET"
            this.conexion!!.setRequestProperty("User-Agent","Mozilla/5.0")
            this.conexion!!.connect()
            var entrada = BufferedReader(InputStreamReader(this.conexion!!.inputStream))
            var respuesta = StringBuffer()
            //Ciclo para ir leyendo línea por línea e ir agregarlo en respuesta
            var linea : String?
            do {
                linea = entrada.readLine()
                if (linea == null) {
                    break
                }
                respuesta.append(linea)
            } while (true)
            var json: String
            //paso a un string el json que tengo para posteriormente manipularlo
            json = respuesta.toString()
            val arrayJson = JSONObject(json)
            desconectar()
            return arrayJson
        }
        catch (e: IOException) {
            e.printStackTrace()
        }
        catch (e: JSONException) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    /**
     * Desconectarnos de la api
     */
    fun desconectar(){
        this.conexion?.disconnect()
    }
}