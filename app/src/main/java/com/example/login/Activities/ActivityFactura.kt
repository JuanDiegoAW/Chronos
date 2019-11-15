package com.example.login.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import com.example.login.Clases.Evento
import com.example.login.Clases.ParametrosEventos
import com.example.login.Clases.Usuario
import com.example.login.R
import kotlinx.android.synthetic.main.activity_factura.*
import org.json.JSONArray

class ActivityFactura : AppCompatActivity() {
    private val usuario = Usuario.iniciar()
    private val adaptador_evento = ParametrosEventos.iniciar()


    lateinit var evento1 : Evento
    private var lista =JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_factura)

        val datos= intent.getStringExtra("datos")
        this.lista=JSONArray(datos)
        var total=0.0


        for (i in 0 until this.lista.length()){
            println(this.lista.getJSONObject(i).optString("asiento"))
            println(this.lista.getJSONObject(i).optString("localidad"))
            println(this.lista.getJSONObject(i).optString("precio"))
            total+=this.lista.getJSONObject(i).optDouble("precio")
        }
        println("Total->$total")
        //****************************************ASIGNAMOS DATOS A LA FACTURA*****************************************//
        evento1 = adaptador_evento.getEvento()
        txtNombre.setText(usuario.getNombre())
        txtCodigoEvento.setText(evento1.titulo)




        val llAsiento = findViewById<LinearLayout>(R.id.llAsiento)
        val llLocalidad = findViewById<LinearLayout>(R.id.llLocalidad)
        val llPrecio = findViewById<LinearLayout>(R.id.llPrecio)


        //Creamos los botones en bucle
        for (i in 0 until this.lista.length()) {
            //*****************CREAMOS LOS LABELS DINAMICOS****************
            val asiento = TextView(this)
            val localidad =TextView(this)
            val precio = TextView(this)

            //**************ASIGNAMOS DATOS A CADA FILA********************
            asiento.setText(this.lista.getJSONObject(i).optString("asiento"))
            asiento.setTextSize(18F)
            localidad.setText(this.lista.getJSONObject(i).optString("localidad"))
            localidad.setTextSize(18F)
            precio.setText(this.lista.getJSONObject(i).optString("precio"))
            precio.setTextSize(18F)

                    //Añadimos el botón a la botonera
            llAsiento.addView(asiento)
            llLocalidad.addView(localidad)
            llPrecio.addView(precio)
        }

        tvTotalPagar.setText(total.toString())
    }
}
