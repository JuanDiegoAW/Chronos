package com.example.login.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.login.Clases.Usuario
import com.example.login.Activities.InicioSesion
import com.example.login.Activities.VentanaPrincipal
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth


class CerrarSesion : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private val usuario : Usuario = Usuario.iniciar()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signOutApp()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null)
        {
            val intent = Intent(activity, InicioSesion::class.java)
            startActivity(intent)
        }

        return inflater.inflate(com.example.login.R.layout.fragment_cerrar_sesion, container, false)
    }

    //SE DETECTA SI INICIÓ SESIÓN CON GOOGLE O FACEBOOK
    private fun signOutApp()
    {
        FirebaseAuth.getInstance().signOut()
        if (usuario.getSesion() == 2)
        {
            cerrarSesionFacebook()
        }

        val intent = Intent(activity, InicioSesion::class.java)
        startActivity(intent)
    }

    private fun cerrarSesionFacebook()
    {
        LoginManager.getInstance().logOut()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, VentanaPrincipal::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }
}
