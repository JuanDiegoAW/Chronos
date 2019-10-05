package com.example.login.fragments

import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.login.Clases.EventosDatos
import com.example.login.Clases.Servicio
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
    private var servicio:Servicio = Servicio()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
        /**
         * Hago el llamado al método que almacena los datos en el arraylist
         * En el arraylist tenés los métodos get de cada 1 por ejemplo para sacar el titulo del
         * primer evento sería:
         * datos.get(0).getTitulo
         * Si queres el de todos utiliza un for-each está en la función
         * mostrarData()
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
        //Cambié la forma para tener los datos, utilizo un for-each
        for (dato in this.datos){
            println("Código: "+dato.getCodigo())
            println("Titulo: "+dato.getTitulo())
            println("Descripción: "+dato.getDescripcion())
            println("Imágenes: "+dato.getImagenes())
        }
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
    private fun getData(){

        var entrada = BufferedReader(InputStreamReader(servicio.metodoGet("evento")))
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
        servicio.desconectar()
    }
}
