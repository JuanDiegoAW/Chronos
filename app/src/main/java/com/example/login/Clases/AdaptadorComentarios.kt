package com.example.login.Clases

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.login.R
import kotlinx.android.synthetic.main.layout_comentarios.view.*
import kotlinx.android.synthetic.main.layout_eventos.view.*

class AdaptadorComentarios(val listaComentarios: List<Comentario>, val contexto: Context?): RecyclerView.Adapter<AdaptadorComentarios.MostradorComentario>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MostradorComentario {
        return MostradorComentario(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_comentarios, parent, false)
        )
    }

    override fun getItemCount() = listaComentarios.size

    //Aqui enlazamos el xml con los datos obtenidos de las consultas del evento
    override fun onBindViewHolder(holder: MostradorComentario, position: Int) {
        val comentario_actual = listaComentarios[position]

        holder.view.nombreUsuarioComentario.text = comentario_actual.nombre_usuario
        holder.view.comentarioEvento.text = comentario_actual.comentario
        holder.view.calificacionComentarioEvento.text = comentario_actual.calificacion

    }
    class MostradorComentario(val view: View) : RecyclerView.ViewHolder(view)
}
