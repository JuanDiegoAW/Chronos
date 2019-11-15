package com.example.login.Clases

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.Toast
import org.json.JSONObject


internal class AdaptadorButtonsOnClickListener(
    var context: Context, var lista:HashMap<Button,AsientosDatos>,
    var id:Int, var localidad: String, var precio: Double ) : View.OnClickListener {

    override fun onClick(v: View) {
        if (lista.size<10){
            val b = v as Button
            //Toast.makeText(this.context, "Ha reservado:  ${b.text}", Toast.LENGTH_SHORT).show()
            v.isEnabled=false
            v.setBackgroundColor(Color.rgb(253,127,72))
            lista[b] = AsientosDatos(id,precio,b.text.toString(),localidad)
            var servicio = Servicio()
            val json = JSONObject()
            json.put("disponible",false)
            servicio.metodoPut("asientos/?idAsiento=${id}",json)
        }else{
            Toast.makeText(this.context, "El Limite de asientos a reservar son 10", Toast.LENGTH_SHORT).show()
        }
    }
}