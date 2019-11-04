package com.example.login.Activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.login.Clases.*
import com.example.login.Clases.AdaptadorButtonsOnClickListener
import kotlinx.android.synthetic.main.activity_reservas.*
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import android.view.View
import android.widget.*
import com.example.login.R
import java.util.ArrayList


class Reservas() : AppCompatActivity(),AdapterView.OnItemSelectedListener {


    private var servicio: Servicio = Servicio()
    private val adaptador_evento = ParametrosEventos.iniciar()
    private var datosLocalidad :HashMap<String,Int> = hashMapOf()
    private var arrayBotonera: ArrayList<LinearLayout> = ArrayList()
    private var asientoId : HashMap<Button,Int> = hashMapOf()
    private var infoAsientos :ArrayList<AsientosDatos> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservas)
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
    override fun onNothingSelected(p0: AdapterView<*>?) {
        println(":V")
    }

    /**
     * Sobreescribir el evento de selección de un spinner para crear dinámicamente los botones.
     * que tenga la localidad y el codigo del evento.
     */
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p0 != null) {
            var jsonObject = servicio.metodoGetBusqueda("localidad","codigoEventos="+adaptador_evento.getEvento().codigo
                    +"&idTipoLocalidad="+ datosLocalidad[p0.selectedItem])
            var cantidad = jsonObject.optInt("cantidadAsientos")
            var cant = cantidad
            var contador=1
            this.infoAsientos = ArrayList()
            getAsientosLocalidad(jsonObject.optInt("id"))
            txtCantidadAsientos.text="Cantidad asientos: ${jsonObject.optString("cantidadAsientos")}"
            txtAsientosDisponibles.text="Asientos disponibles: ${jsonObject.optString("cantidadAsientosDisponible")}"
            if (llPrincipal.childCount>0){
                llPrincipal.removeAllViews()
            }
            this.arrayBotonera = ArrayList()
            cantidad = if ((cantidad%5)>0) (cantidad/5) +1 else cantidad/5
            for (i in 0 until cantidad){
                this.arrayBotonera.add(LinearLayout(this))
                for (j in 0 until 5){
                    if (contador<=cant){
                        val button = Button(this)
                        button.textSize = 15F
                        button.setTextColor(Color.WHITE)
                        if (contador<=this.infoAsientos.size){
                            button.text = this.infoAsientos[contador-1].getNumeroAsiento()

                            if (!this.infoAsientos[contador-1].isDisponible()){
                                button.isEnabled=this.infoAsientos[contador-1].isDisponible()
                                button.setBackgroundColor(Color.rgb(253,127,72))
                            }else{
                                button.setBackgroundColor(Color.rgb(251,74,86))
                            }
                            button.setOnClickListener(AdaptadorButtonsOnClickListener(this,
                                this.asientoId,this.infoAsientos[contador-1].getIdAsiento()))
                        }else {
                            button.text = contador.toString()
                            button.setBackgroundColor(Color.rgb(251, 74, 86))
                            button.setOnClickListener(
                                AdaptadorButtonsOnClickListener(
                                    this,
                                    this.asientoId, contador
                                )
                            )
                        }
                        this.arrayBotonera[i].addView(button)
                    }else
                        break
                    contador ++
                }
                llPrincipal.addView(this.arrayBotonera[i])
            }
        }
    }

    /**
     * Se obtiene un array de tipo JSON con todos los asientos de dicha localidad, lo cual regresa
     * Ejemplo Asientos: [{"id": 2,"numeroAsiento": "557","disponible": true,"idLocalidad": 1}].
     * De ello tomo el id para hacer el cambio, el numero del asiento para ponerlo como texto al boton
     * disponibilidad para deshabilitar/habilitar el boton
     */
    private fun getAsientosLocalidad(idLocalidad:Int){
        try
        {
            var entrada = BufferedReader(InputStreamReader(this.servicio.metodoGetBusquedaArray
                ("asientos", "idLocalidad=$idLocalidad")))
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
                this.infoAsientos.add(AsientosDatos(objetos.optBoolean("disponible"),
                    objetos.optString("numeroAsiento"),
                    objetos.optInt("id")))
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
    override fun onBackPressed() {
        if (asientoId.isNotEmpty()){
            println("Hay datos reservados")
        }else{
            println("No hay datos reservados")
            super.onBackPressed()
        }

    }
}
