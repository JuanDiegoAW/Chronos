package com.example.login.Clases

import android.content.Context
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.login.R
import kotlinx.android.synthetic.main.layout_eventos.view.*


class AdaptadorEventos(val listaEventos: List<Evento>): RecyclerView.Adapter<AdaptadorEventos.MostradorEvento>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MostradorEvento {
        return MostradorEvento(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_eventos, parent, false)
        )
    }

    override fun getItemCount() = listaEventos.size

    //Aqui enlazamos el xml con los datos obtenidos de las consultas del evento
    override fun onBindViewHolder(holder: MostradorEvento, position: Int) {
        val evento_actual = listaEventos[position]

        holder.view.tituloEvento.text = evento_actual.titulo
        holder.view.descripcionEvento.text = evento_actual.descripcion
        holder.view.calificacionEvento.text = evento_actual.calificacion
    }

    class MostradorEvento(val view: View) : RecyclerView.ViewHolder(view)
}