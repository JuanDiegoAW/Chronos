package com.example.login.Clases

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.Toast
import org.json.JSONObject


internal class AdaptadorButtonsOnClickListener(
    var context: Context, var lista:HashMap<Button,AsientosDatos>,
    var id:Int, var localidad: String, var precio: Double,var idLocalidad: Int ) : View.OnClickListener {

    override fun onClick(v: View) {
        if (lista.size<10){
            val b = v as Button
            v.isEnabled=false
            v.setBackgroundColor(Color.rgb(253,127,72))
            lista[b] = AsientosDatos(id,precio,b.text.toString(),localidad)
            var servicio = Servicio()
            var json = JSONObject()
            json.put("disponible",false)
            var mensaje = if (servicio.metodoPut("asientos/?idAsiento=${id}",json)){
                json = JSONObject()
                json.put("cantidadAsientos",0)
                json.put("cantidadAsientosDisponible",0)
                servicio.metodoPut("localidad/?id=$idLocalidad&asientos=1",json)
                "Reservado con exito...!"
            }else{
                "Error al reservar...!"
            }
            Toast.makeText(this.context, mensaje, Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this.context, "El Limite de asientos a reservar son 10", Toast.LENGTH_SHORT).show()
        }
    }
}