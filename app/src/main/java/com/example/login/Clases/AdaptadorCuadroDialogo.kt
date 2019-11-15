package com.example.login.Clases

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Window
import android.widget.*
import androidx.core.view.get
import com.example.login.R
import org.json.JSONObject

class AdaptadorCuadroDialogo {
    /**
     * Constructor para mi clase adaptador cuadro de dialogo en el cual muestro con checks box
     * los asientos reservados que el usuario haya marcado, esto para que seleccione los asientos
     * que ya no quiere reservar.
     */
    constructor(contexto : Context,reservas:HashMap<Button,AsientosDatos>){
        if (reservas.isNotEmpty()){ //Mientras la lista de botones no sea vacía muestra el cuadro de dialogo
            val dialogo = Dialog(contexto)
            dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogo.setCancelable(false)
            dialogo.setContentView(R.layout.cuadro_dialogo)
            val tlLayout = dialogo.findViewById<TableLayout>(R.id.tlReservaciones)
            tlLayout.removeAllViews() //Elimino todo lo que tenga TableLayout
            //Creación dinámica de los checkbox
            for (res in reservas){
                var checkBox = CheckBox(contexto)
                checkBox.text ="Asiento no ${res.key.text}"
                val row =TableRow(contexto)
                row.addView(checkBox)
                tlLayout.addView(row)
            }
            val btnAceptar = dialogo.findViewById<Button>(R.id.btnAceptar)
            //Acción del botón cuando le da aceptar
            btnAceptar.setOnClickListener {
                val listaAux = ArrayList<Button>() //Creo una lista para almacenar los que se selecciono para posteriormente eliminar
                // El elemento que se selecciono
                var i=0
                for (res in reservas){
                    //Obtengo un tablerow y el checkbox que tiene
                    val row = tlLayout.getChildAt(i) as TableRow
                    val check =row[0] as CheckBox
                    //Si el checkbox es seleccionado cambio color al boton lo habilito y mando a la api a cancelar reserva
                    if(check.isChecked){
                        res.key.setBackgroundColor(Color.rgb(251,74,86))
                        res.key.isEnabled=true
                        //Acá se pondrá la ruta para cancelar la reserva
                        listaAux.add(res.key)//Almaceno el botón que se eliminará
                    }
                    i++
                }
                //Ciclo para eliminar el elemento que se selecciono, se le hace un lista[i]-i ya que si no lo hago accedo a una posición inexistente
                val servicio=Servicio()
                val jsonObject = JSONObject()
                jsonObject.put("disponible",true)
                var mensaje = ""
                var cont=0

                for (i in 0 until listaAux.size){
                    if (servicio.metodoPut("asientos/?idAsiento=${reservas[listaAux[i]]?.getId()}",jsonObject)){
                       reservas.remove(listaAux[i])
                        cont++
                    }else{
                        break
                    }

                }
                mensaje=if (cont==listaAux.size)
                    "Reservación cancelada correctamente"
                else
                    "Hubo un error al cancelar la reservación"
                Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show()
                dialogo.dismiss()//Cierro el dialogo
            }
            val btnCancelar =dialogo.findViewById<Button>(R.id.btnCancelar)
            btnCancelar.setOnClickListener {
                //solo cierra el dialogo ya que es el botón cancelar
                dialogo.dismiss()
            }
            dialogo.show()
        }
    }
}