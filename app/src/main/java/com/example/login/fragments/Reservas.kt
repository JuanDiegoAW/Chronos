package com.example.login.fragments

import android.content.Context
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
import com.example.login.Clases.AdaptadorHistorial
import com.example.login.Clases.Servicio
import kotlinx.android.synthetic.main.fragment_reservas.*
import com.example.login.R
import kotlinx.android.synthetic.main.fragment_eventos.*
import kotlinx.android.synthetic.main.fragment_reservas.recyclerViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [reservas.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [reservas.newInstance] factory method to
 * create an instance of this fragment.
 */
class reservas : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var listener: MostrarEventos.OnFragmentInteractionListener? = null
    private var datos : ArrayList<EventosDatos> = ArrayList()
    private var servicio: Servicio = Servicio()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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
        recyclerViewModel.adapter = AdaptadorHistorial(lista,context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(com.example.login.R.layout.fragment_reservas, container, false)
    }
    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }



    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment reservas.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            reservas().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Esto controla las acciones que ocurren cuando se "recarga" el fragment
        refreshLayouth.setOnRefreshListener {
            datos.clear()
            getData()
        }
        //obtenemos los datos
        getData()
    }

    private fun getData(){
        refreshLayouth.isRefreshing = true
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
            refreshLayouth.isRefreshing = false
        }
        catch (e: IOException)
        {
            Toast.makeText(this.context,"Verifique su conexión a internet", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
            refreshLayouth.isRefreshing = false
        }
        catch (e: JSONException)
        {
            e.printStackTrace()
            refreshLayouth.isRefreshing = false
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
            Toast.makeText(this.context,"Verifique su conexión a internet", Toast.LENGTH_SHORT).show()
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
