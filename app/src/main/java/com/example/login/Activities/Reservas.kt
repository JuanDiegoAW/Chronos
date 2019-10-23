package com.example.login.Activities

import android.annotation.TargetApi
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.login.Clases.*
import com.example.login.Clases.ButtonsOnClickListener
import kotlinx.android.synthetic.main.activity_reservas.*
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.login.R
import kotlinx.android.synthetic.main.activity_reservas.view.*
import java.util.ArrayList


class Reservas() : AppCompatActivity(),AdapterView.OnItemSelectedListener {


    private var servicio: Servicio = Servicio()
    private val adaptador_evento = ParametrosEventos.iniciar()
    private var datosLocalidad :HashMap<String,Int> = hashMapOf()
    private var arrayBotonera: ArrayList<LinearLayout> = ArrayList()
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
    }

    private fun obtenerLocalidad(eventoCod:String){
        try
        {
            var entrada = BufferedReader(InputStreamReader(this.servicio.metodoGetBusquedaArray("localidad","codigoEventos="+eventoCod)))
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
            for (i in 0 until  this.arrayBotonera.size){
                if (this.arrayBotonera[i].childCount>0)
                    this.arrayBotonera[i].removeAllViews()
            }
            this.arrayBotonera = ArrayList()
            cantidad = if ((cantidad%5)>0) (cantidad/5) +1 else cantidad/5

            for (i in 0 until cantidad){
                this.arrayBotonera.add(LinearLayout(this))
                for (j in 0 until 5){
                    if (contador<=cant){
                        val button = Button(this)
                        button.textSize = 15F
                        button.text = contador.toString()
                        button.setTextColor(Color.WHITE)
                        button.setBackgroundColor(Color.GREEN)
                        button.setOnClickListener(ButtonsOnClickListener(this))


                        this.arrayBotonera[i].addView(button)
                    }else
                        break
                    contador ++

                }
                llPrincipal.addView(this.arrayBotonera[i])
            }
        }
    }
}
