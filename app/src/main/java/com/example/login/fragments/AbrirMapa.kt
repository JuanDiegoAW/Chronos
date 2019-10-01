package com.example.login.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.login.Activities.Mapa
import com.example.login.Activities.MenuLateral

class AbrirMapa : Fragment() {

    private var listener: CerrarSesion.OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(activity, MenuLateral::class.java)
        startActivity(intent)

        abrirMapa()
    }

    private fun abrirMapa()
    {
        val intent = Intent(activity, Mapa::class.java)
        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(com.example.login.R.layout.fragment_abrir_mapa, container, false)
    }

    //fun onButtonPressed(uri: Uri)
    //{
    //    listener?.onFragmentInteraction(uri)
    //}

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {

        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            AbrirMapa().apply {
                arguments = Bundle().apply {
                    //putString(ARG_PARAM1, param1)
                    //putString(ARG_PARAM2, param2)
                }
            }
    }
}
