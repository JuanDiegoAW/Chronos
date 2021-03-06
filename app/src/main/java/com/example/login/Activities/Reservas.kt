package com.example.login.Activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.login.Clases.*
import com.example.login.R
import kotlinx.android.synthetic.main.activity_reservas.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList


class Reservas() : AppCompatActivity(),AdapterView.OnItemSelectedListener {


    private var servicio: Servicio = Servicio()
    private val adaptador_evento = ParametrosEventos.iniciar()
    private var datosLocalidad :HashMap<String,Int> = hashMapOf()
    private var arrayBotonera: ArrayList<LinearLayout> = ArrayList()
    private var asientoId : HashMap<Button,AsientosDatos> = hashMapOf()
    private var infoAsientos :ArrayList<AsientosDatos> = ArrayList()
    private var asientosDisponibles=0
    private var asientosTotales=0
    private var siguiente="null"
    private var anterior="null"
    private var precio=0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservas)

        //*************************** REDIRIGE A PAGAR LOS ASIENTOS***************************************************************//
        button2.setOnClickListener(){
            if (this.asientoId.isNotEmpty()){
                val json = JSONArray()
                for (datos:AsientosDatos in this.asientoId.values.toTypedArray()){
                    var jsonObject= JSONObject()
                    jsonObject.put("asiento",datos.getNumeroAsiento())
                    jsonObject.put("localidad",datos.getLocalidad())
                    jsonObject.put("precio",datos.getPrecio())
                    json.put(jsonObject)
                }
                val datosString = json.toString()
                val intento1 = Intent(this, ActivityFactura::class.java)
                intento1.putExtra("datos",datosString)
                startActivity(intento1)
            }
        }
        //************************************************************************************************************************//

        /**
         * Funciones para obtener los datos de las localidades
         * se le manda el codigo del evento para que nos obtenga dichas localidades
         */
        obtenerLocalidad(adaptador_evento.getEvento().codigo)
        var adapterSpinner:ArrayAdapter<String> = ArrayAdapter(this,
            android.R.layout.simple_spinner_item,datosLocalidad.keys.toTypedArray())
        spLocalidad.adapter = adapterSpinner
        spLocalidad.onItemSelectedListener=this
        btnReserva.setOnClickListener {
            AdaptadorCuadroDialogo(this,asientoId)
        }
        btnSiguiente.setOnClickListener {
            getAsientosLocalidad(this.siguiente)
        }
        btnAnterior.setOnClickListener {
            getAsientosLocalidad(this.anterior)
        }

    }

    private fun obtenerLocalidad(eventoCod:String){
        try
        {
            var entrada = BufferedReader(InputStreamReader(this.servicio.metodoGetBusquedaArray
                ("localidad","codigoEventos="+eventoCod)))
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
            for (i in 0 until arrayJson.length()) {
                var objetos=arrayJson.getJSONObject(i)
                datosLocalidad[objetos.optString("tipoLocalidad")] = objetos.optInt("id")

            }
            entrada.close()
        }
        catch (e: IOException) {
            e.printStackTrace()
        }
        catch (e: JSONException) {
            e.printStackTrace()
        }
        finally {
            servicio.desconectar()
        }
    }
    override fun onNothingSelected(p0: AdapterView<*>?) {}

    /**
     * Sobreescribir el evento de selección de un spinner para crear dinámicamente los botones.
     * que tenga la localidad y el codigo del evento.
     */
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p0 != null) {
            var jsonObject = servicio.metodoGetBusqueda("localidad","codigoEventos="+adaptador_evento.getEvento().codigo
                    +"&idTipoLocalidad="+ datosLocalidad[p0.selectedItem])
            this.asientosTotales = jsonObject.optInt("cantidadAsientos")
            this.precio=jsonObject.optDouble("costo")
            this.asientosDisponibles=jsonObject.optInt("cantidadAsientosDisponible")
            this.infoAsientos = ArrayList()
            txtCantidadAsientos.text ="Asientos totales: ${this.asientosTotales}"
            txtPrecio.text="Precio: ${this.precio}"
            txtAsientosDisponibles.text="Asientos disponibles: ${this.asientosDisponibles}"
            getAsientosLocalidad("http://edvfelipe.pythonanywhere.com/api/asientos/?idLocalidad=${jsonObject.optInt("id")}")
        }
    }

    /**
     * Se obtiene un array de tipo JSON con todos los asientos de dicha localidad, lo cual regresa
     * Ejemplo Asientos: [{"id": 2,"numeroAsiento": "557","disponible": true,"idLocalidad": 1}].
     * De ello tomo el id para hacer el cambio, el numero del asiento para ponerlo como texto al boton
     * disponibilidad para deshabilitar/habilitar el boton
     */
    private fun getAsientosLocalidad(ruta:String){
        try
        {
            var entrada = BufferedReader(InputStreamReader(this.servicio.metodoGetPaginacion
                (ruta)))
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
            val jsonObject = JSONObject(json)
            this.siguiente=jsonObject.optString("next")
            this.anterior=jsonObject.optString("previous")
            btnSiguiente.isEnabled=this.siguiente!="null"
            btnAnterior.isEnabled= this.anterior != "null"
            val arrayJson = JSONArray(jsonObject.optString("results"))
            //Limpio el layout principal cada vez que voy a paginar
            if (llPrincipal.childCount>0){
                llPrincipal.removeAllViews()
            }
            this.arrayBotonera = ArrayList()
            var contador=0
            var contador1=0
            for (i in 0 until arrayJson.length()) {
                var objetos=arrayJson.getJSONObject(i)
                this.arrayBotonera.add(LinearLayout(this))
                if (contador1!=4){
                    insertarButton(objetos,contador)
                    contador1++
                }else {
                    insertarButton(objetos,contador)
                    llPrincipal.addView(this.arrayBotonera[contador])
                    contador++
                    contador1=0
                }
            }
            /**
             * Si contador es diferente a 0 quiere decir que hay asientos que no se agregaron
             * dentro del ciclo por ejemplo que hayan 17, dentro del ciclo se agregan 15 y fuera
             * los ultimos 2
             * */
            if (contador1!=0){
                llPrincipal.addView(this.arrayBotonera[contador])
            }
            entrada.close()
        }
        catch (e: IOException) {
            e.printStackTrace()
        }
        catch (e: JSONException) {
            e.printStackTrace()
        }
        finally {
            servicio.desconectar()
        }
    }

    private fun insertarButton(objeto:JSONObject,contador:Int){
        val button = Button(this)
        button.textSize = 15F
        //button.setTextColor(Color.WHITE)
        button.text=objeto.getString("numeroAsiento")
        val estado=objeto.optBoolean("disponible")
        if (!estado){
            button.isEnabled=estado
            button.setBackgroundColor(Color.rgb(253,127,72))
        }else{
            button.setBackgroundColor(Color.rgb(251,74,86))
        }
        button.setOnClickListener(AdaptadorButtonsOnClickListener(this,
            this.asientoId,objeto.getInt("id"),spLocalidad.selectedItem.toString(),this.precio,
            datosLocalidad[spLocalidad.selectedItem.toString()]!!
        ))
        this.arrayBotonera[contador].addView(button)
    }
    override fun onBackPressed() {
        if (asientoId.isNotEmpty()){
            val dialogo1 = AlertDialog.Builder(this)
            dialogo1.setTitle("Importante")
            dialogo1.setMessage("¿Desea salir?\nSi aceepta se cancelará su reservación")
            dialogo1.setCancelable(false)
            dialogo1.setPositiveButton(
                "Confirmar"
            ) { dialogo1, id -> aceptar() }
            dialogo1.setNegativeButton(
                "Cancelar"
            ) { dialogo1,id->cancelar()}
            dialogo1.show()
        }else{
            super.onBackPressed()
        }
    }
    fun aceptar() {
        cancelarReservacion()
        super.onBackPressed()
    }

    fun cancelar(){}
    private fun cancelarReservacion(){
        var json = JSONObject()
        json.put("disponible",true)
        for (datos in asientoId.values){
            servicio.metodoPut("asientos/?idAsiento=${datos.getId()}",json)
            json = JSONObject()
            json.put("cantidadAsientos",0)
            json.put("cantidadAsientosDisponible",0)
            servicio.metodoPut("localidad/?id=${datosLocalidad[datos.getLocalidad()]}&asientos=1",json)
        }

        asientoId= HashMap()
    }
    override fun onDestroy() {
        while(asientoId.isNotEmpty()){
            cancelarReservacion()
        }
        super.onDestroy()

    }
}
