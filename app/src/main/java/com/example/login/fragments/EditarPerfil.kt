package com.example.login.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import com.bumptech.glide.Glide
//import com.example.login.Clases.Servicio
import com.example.login.Clases.Usuario
import com.example.login.R
import kotlinx.android.synthetic.main.fragment_editar_perfil.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class editar_perfil : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    //private val servicio:Servicio = Servicio()
    private var listener: OnFragmentInteractionListener? = null
    private var usuario: Usuario = Usuario.iniciar()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private fun setupUI()
    {
        fotoperfil.setOnClickListener{
            verificarPermisos()
        }
    }

    fun verificarPermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (context?.let { checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE) } ==
                PackageManager.PERMISSION_DENIED){
                //Si el permiso esta negado
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                //Solicita los permisos
                requestPermissions(permissions, PERMISSION_CODE)
            }
            else{
                //Si ya se dio permiso
                elegirImagen()
            }
        }
        else{
            //system OS is < Marshmallow
            elegirImagen()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    elegirImagen()
                }
                else{
                    //Si no se le brindo permiso
                    Toast.makeText(context, "Permiso Denegado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun elegirImagen()
    {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IMAGE_PICK_CODE){
            Glide.with(this).load(data?.data).into(fotoperfil)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        setupUI()
        ponerDatos(view)
    }

    private fun ponerDatos(view: View){
        //Se coloca el nombre actual
        val nombre_usuario = view.findViewById(R.id.textnombre) as TextView
        nombre_usuario.text = usuario.getNombre()

        //Se coloca la imagen actual
        Glide.with(this).load(usuario.getUrlFoto()).into(fotoperfil)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_editar_perfil, container, false)
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
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        private val IMAGE_PICK_CODE = 1000
        private val PERMISSION_CODE = 1001
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment editar_perfil.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            editar_perfil().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
