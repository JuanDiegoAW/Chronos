package com.example.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupUI()
        obtenerUsuario()
    }

    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    private fun obtenerUsuario()
    {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("id_token")
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null)
        {
            //poner nombre del usuario
            nombreUsuario.setText(user.displayName)

            //poner foto del usuario
            val photoUrl = user.photoUrl
            Glide.with(this).load(photoUrl).into(imagenUsuario)
        }
    }

    private fun setupUI()
    {
        cerrarSesion.setOnClickListener {
            signOutApp()
        }
    }

    private fun signOutApp()
    {
        startActivity(MainActivity.getLaunchIntent(this))
        FirebaseAuth.getInstance().signOut()
        signOutGoogle()

    }

    private fun signOutGoogle()
    {
        mGoogleSignInClient.signOut()
    }
}