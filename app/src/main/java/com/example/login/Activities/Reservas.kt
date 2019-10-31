package com.example.login.Activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.login.Clases.*
import com.example.login.Clases.AdapterButtonsOnClickListener
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
    private var asientoId : ArrayList<Button> = ArrayList()
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
            AdaptadorCuadroDialogo(this,arrayBotonera,asientoId)
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
                datosLocalidad.put(objetos.optString("tipoLocalidad"),objetos.optInt("id"))

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
    }
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p0 != null) {
            var jsonObject = servicio.metodoGetBusqueda("localidad","codigoEventos="+adaptador_evento.getEvento().codigo
                    +"&idTipoLocalidad="+ datosLocalidad[p0.selectedItem])
            var cantidad = jsonObject.optInt("cantidadAsientos")
            var cant = cantidad
            var contador=1
            this.infoAsientos = ArrayList()
            getAsientosLocalidad(datosLocalidad[p0.selectedItem]!!.toInt())
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
                        }else{
                            button.text = contador.toString()
                            button.setBackgroundColor(Color.rgb(251,74,86))
                        }



                        button.setOnClickListener(AdapterButtonsOnClickListener(this,this.asientoId))
                        this.arrayBotonera[i].addView(button)
                    }else
                        break
                    contador ++
                }
                llPrincipal.addView(this.arrayBotonera[i])
            }
        }
    }

    private fun getAsientosLocalidad(idLocalidad:Int){
        try
        {
            var entrada = BufferedReader(InputStreamReader(this.servicio.metodoGetBusquedaArray
                ("asientos/codigoeventos", "idLocalidad=$idLocalidad")))
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
                this.infoAsientos.add(AsientosDatos(objetos.optBoolean("disponible"),objetos.optString("numeroAsiento")))
                println(objetos)
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
}
