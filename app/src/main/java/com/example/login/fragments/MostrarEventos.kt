package com.example.login.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login.Clases.AdaptadorEventos
import com.example.login.Clases.Evento
import com.example.login.Clases.EventosDatos
import com.example.login.Clases.Servicio
import kotlinx.android.synthetic.main.fragment_eventos.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class MostrarEventos : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private var datos : ArrayList<EventosDatos> = ArrayList()
    private var servicio:Servicio = Servicio()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Esto controla las acciones que ocurren cuando se "recarga" el fragment
        refreshLayout.setOnRefreshListener {
            datos.clear()
            getData()
        }
        //obtenemos los datos
        getData()
    }

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(com.example.login.R.layout.fragment_eventos, container, false)
    }

    private fun showData()
    {
        //Hacemos una lista vacia mutable
        val lista = mutableListOf<Evento>()

        //Y le agregamos cada evento encontrado de la consulta
        for (dato in this.datos)
        {
            lista.add(Evento(dato.getCodigo(),dato.getTitulo(),dato.getDescripcion(),dato.getImagen(),dato.getCalificacion()))
            println("Imagen->${dato.getImagen()}")
        }

        recyclerViewModel.layoutManager = LinearLayoutManager(activity)
        //Luego mandamos esa lista al adaptador para asi enlazarlo con el xml del evento
        recyclerViewModel.adapter = AdaptadorEventos(lista,context)
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
            MostrarEventos().apply {
                arguments = Bundle().apply {

                }
            }
    }

    /**
     * Función para listar todos los MostrarEventos
     */
    private fun getData(){
        refreshLayout.isRefreshing = true
        try
        {
            var entrada = BufferedReader(InputStreamReader(servicio.metodoGet("eventos/fecha")))
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
            val json: String
            //paso a un string el json que tengo para posteriormente manipularlo
            json = respuesta.toString()
            val arrayJson = JSONArray(json)
            /**
            * Ciclo para ir sacando del array que tiene forma del json regresado y va a ir
            * almacenando en el arraylist
            */
            for (i in 0 until arrayJson.length()) {
                val jsonObject: JSONObject = arrayJson.getJSONObject(i)
                datos.add(
                    EventosDatos(
                        jsonObject.optString("codigo"),
                        jsonObject.optString("titulo"),
                        jsonObject.optString("descripcion"),
                        getImageEvento(jsonObject.optString("codigo")),
                        jsonObject.optString("calificacionP")
                    )
                )
            }
            entrada.close()
            refreshLayout.isRefreshing = false
        }
        catch (e: IOException)
        {
            Toast.makeText(this.context,"Verifique su conexión a internet",Toast.LENGTH_SHORT).show()
            e.printStackTrace()
            refreshLayout.isRefreshing = false
        }
        catch (e: JSONException)
        {
            e.printStackTrace()
            refreshLayout.isRefreshing = false
        }
        finally
        {
            servicio.desconectar()
        }
        //Despues de obtener los datos, los mostramos
        showData()
    }

    private fun getImageEvento(cod:String):String{
        var mensaje ="No imagen"
        try
        {
            var entrada = BufferedReader(InputStreamReader(servicio.metodoGetBusquedaArray("imagenes","codigo=$cod")))
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
            val json: String
            //paso a un string el json que tengo para posteriormente manipularlo
            json = respuesta.toString()
            val arrayJson = JSONArray(json)
            /**
             * Ciclo para ir sacando del array que tiene forma del json regresado y va a ir
             * almacenando en el arraylist
             **/
            entrada.close()
            mensaje = if(arrayJson.length()>0){"http://edvfelipe.pythonanywhere.com" +
                    arrayJson.getJSONObject(0).optString("imagen")}
            else {"No imagen"}
        }
        catch (e: IOException) {
            Toast.makeText(this.context,"Verifique su conexión a internet",Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
        catch (e: JSONException) {
            e.printStackTrace()
        }
        finally {
            servicio.desconectar()
        }
        return mensaje
    }
}
