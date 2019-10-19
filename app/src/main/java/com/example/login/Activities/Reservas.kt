package com.example.login.Activities

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
import com.example.login.R


class Reservas() : AppCompatActivity(),AdapterView.OnItemSelectedListener {


    private var servicio: Servicio = Servicio()
    private val adaptador_evento = ParametrosEventos.iniciar()
    private var datosLocalidad:HashMap<String,Int> = hashMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.login.R.layout.activity_reservas)
        //DAtos de los layouts
        val llBotonera = findViewById(com.example.login.R.id.llBotonera) as LinearLayout
        val llBotonera1 = findViewById(com.example.login.R.id.llBotonera1) as LinearLayout
        val llBotonera2 = findViewById(com.example.login.R.id.llBotonera2) as LinearLayout
        val llBotonera3 = findViewById(com.example.login.R.id.llBotonera3) as LinearLayout
        val llBotonera4 = findViewById(com.example.login.R.id.llBotonera4) as LinearLayout
        /**
         * Funciones para obtener los datos de las localidades
         * se le manda el codigo del evento para que nos obtenga dichas localidades
         */
        obtenerLocalidad(adaptador_evento.getEvento().codigo)
        var adapterSpinner:ArrayAdapter<String> = ArrayAdapter(this,android.R.layout.simple_spinner_item,datosLocalidad.keys.toTypedArray())
        spLocalidad.adapter = adapterSpinner
        var contador = 1
        for (i in 1 until 19) {

            val button = Button(this)
            button.textSize = 9F;
            button.text = contador.toString()
            contador +=5
            button.setOnClickListener(ButtonsOnClickListener(this));
            llBotonera.addView(button);
        }
        contador=2;
        for (i in 18 until 36) {
            val button = Button(this)
            //button.setLayoutParams(lp)
            button.textSize = 9F;
            button.text = contador.toString()
            contador+=5
            button.setOnClickListener(ButtonsOnClickListener(this));
            llBotonera1.addView(button);
        }
        contador =3
        for (i in 37 until 55) {
            val button = Button(this)
            //button.setLayoutParams(lp)
            button.textSize = 9F
            button.text = contador.toString()
            contador+=5
            button.setOnClickListener(ButtonsOnClickListener(this));
            llBotonera2.addView(button);
        }
        contador =4
        for (i in 55 until 73) {
            val button = Button(this)
            //button.setLayoutParams(lp)
            button.textSize = 9F;
            button.text = contador.toString()
            contador+=5
            button.setOnClickListener(ButtonsOnClickListener(this));
            llBotonera3.addView(button);
        }
        contador=5
        for (i in 73 until 91) {
            val button = Button(this)
            button.textSize = 9F
            button.text = contador.toString()
            contador+=5
            button.setOnClickListener(ButtonsOnClickListener(this));
            llBotonera4.addView(button)
        }
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
    fun mostrar(){
        for(datos in datosLocalidad){
            println("ID->"+datos.key+" LOCALIDAD->"+datos.value)
        }
    }
    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p0 != null) {
            var jsonObject = servicio.metodoGetBusqueda("localidad","codigoEventos="+adaptador_evento.getEvento().codigo
                    +"&idTipoLocalidad="+ datosLocalidad[p0.selectedItem])
            if (jsonObject!=null){
                println(jsonObject)
            }else{
                println(":V")
            }
        }
    }

}
