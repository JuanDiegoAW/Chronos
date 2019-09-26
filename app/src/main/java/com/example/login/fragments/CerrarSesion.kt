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
import com.example.login.InicioSesion
import com.example.login.MenuLateral
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
        return inflater.inflate(com.example.login.R.layout.fragment_cerrar_sesion, container, false)
    }

    //SE DETECTA SI INICIÓ SESIÓN CON GOOGLE O FACEBOOK
    private fun signOutApp()
    {
        FirebaseAuth.getInstance().signOut()
        if (usuario.getSesion() == 1)
        {
            cerrarSesionGoogle()
        }
        else
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

    private fun cerrarSesionGoogle()
    {
        //val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        //    .requestIdToken("id_token")
        //    .requestEmail()
        //    .build()

        //val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        //mGoogleSignInClient.signOut()
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, MenuLateral::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }
}
