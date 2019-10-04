package com.example.login.fragments

import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.login.Clases.EventosDatos
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class eventos : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private var datos : ArrayList<EventosDatos> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
        /**
         * Hago el llamado al método que almacena los datos en el arraylist
         * En el arraylist tenés los métodos get de cada 1 por ejemplo para sacar el titulo del
         * primer evento sería:
         * datos.get(0).getTitulo
         */
        getData()

        mostrarData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(com.example.login.R.layout.fragment_eventos, container, false)
    }

    private fun mostrarData()
    {
        println(datos[0].getTitulo())
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            eventos().apply {
                arguments = Bundle().apply {

                }
            }
    }

    /**
     * Función para listar todos los eventos
     */
    internal fun getData(){
        //la variable urlApi se encarga de tener la url donde se almacena la api directamente a eventos
        var urlApi = "http://edvfelipe.pythonanywhere.com/api/evento/"
        var policy : StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        try {
            //variables para establecer la conexion con la web donde se almacena la api
            var url = URL(urlApi)
            var conexion: HttpURLConnection
            //lo unico que hago es parsear la url al protocolo http en el cual podemos tener: get, post, etc
            conexion = url.openConnection() as HttpURLConnection
            //Le mando a la conexion que necesito el método GET Y establesco una conexión
            conexion.requestMethod = "GET"
            conexion.connect()
            /**
             * la variable entrada me sirve para leer lo que la conexión obtenga con la solicitud GET
             * En la variable respuesta almacenaré cada linea que tendré en entrada
             * Y linea  solo me sirve para ir leyendo linea por linea lo que tenga en mi
             * entrada
             */
            var entrada = BufferedReader(InputStreamReader(conexion.inputStream))
            var respuesta = StringBuffer()
            //Ciclo para ir leyendo linea por linea y almacenarlo agregarlo en respuesta
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
            var arrayJson = JSONArray(json)
            /**
             * Ciclo para ir sacando del array que tiene forma del json regresado y va a ir
             * almacenando en el arraylist
             */
            for (i in 0 until arrayJson.length()) {
                var jsonObject: JSONObject = arrayJson.getJSONObject(i)
                datos.add(EventosDatos(jsonObject.optString("codigo"),
                    jsonObject.optString("titulo"),
                    jsonObject.optString("descripcion"),
                    jsonObject.optString("imagenes")))
            }
            conexion.disconnect()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}
