package com.example.login.Clases

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.login.Activities.InformacionEvento
import com.example.login.R
import kotlinx.android.synthetic.main.layout_historial.view.*


class AdaptadorHistorial(val listaEventos: List<Evento>, val contexto: Context?): RecyclerView.Adapter<AdaptadorHistorial.MostradorEvento>(){

  //  private val evento_mostrar = ParametrosEventos.iniciar()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MostradorEvento {
        return MostradorEvento(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_historial, parent, false)
        )
    }

    override fun getItemCount() = listaEventos.size

    //Aqui enlazamos el xml con los datos obtenidos de las consultas del evento
    override fun onBindViewHolder(holder: MostradorEvento, position: Int) {
        val evento_actual = listaEventos[position]

        holder.view.tituloEvento.text = evento_actual.titulo
        //holder.view.descripcionEvento.text = evento_actual.descripcion
        holder.view.calificacionEvento.text = evento_actual.calificacion


        //Cuando se presiona el boton de un evento, se envia a la clase de Parametro para luego ser usada por la actividad de
        //InformacionEvento
 //       holder.view.botonDetallesEvento.setOnClickListener{
   //         evento_mostrar.setEvento(evento_actual)
     //       val intent = Intent(contexto, InformacionEvento::class.java)
       //     contexto!!.startActivity(intent)
        //}
    }
    class MostradorEvento(val view: View) : RecyclerView.ViewHolder(view)

}

