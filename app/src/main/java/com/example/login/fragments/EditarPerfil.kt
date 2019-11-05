package com.example.login.fragments

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.login.Clases.*
import com.example.login.R
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_editar_perfil.*
import org.json.JSONException
import org.json.JSONObject
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.util.*
class editar_perfil : Fragment(){

    private var listener: OnFragmentInteractionListener? = null
    private var usuario: Usuario = Usuario.iniciar()
    private var nombre_inicial =""
    private var imagen_nueva: Uri? = null


    //Conexion API
    private val servicio: Servicio = Servicio()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nombre_inicial = usuario.getNombre()
    }

    private fun setupUI()
    {
        fotoperfil.setOnClickListener{
            elegirImagen()
        }
        actualizar.setOnClickListener{
            actualizarNombre(textnombre.text.toString())
            actualizarImagen()
            //la actualizacion de datos
            activity!!.recreate()
        }
    }

    private fun actualizarImagen()
    {
        if(imagen_nueva ==  null) return

        val nombre_archivo = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/Imagenes/$nombre_archivo")

        usuario.setUrlFoto(imagen_nueva!!)
        activity!!.recreate()

        ref.putFile(imagen_nueva!!)
            .addOnSuccessListener {
                //Toast.makeText(context, "Imagen actualizada", Toast.LENGTH_SHORT).show()
                imagen_nueva = null
                ref.downloadUrl.addOnSuccessListener {
                    usuario.setUrlFoto(it)
                    actualizarUrlFoto()
                }
            }
    }

    private fun actualizarUrlFoto()
    {
        println(usuario.getUrlFoto())
    }

    private fun actualizarNombre(nombre_nuevo : String)
    {
        //Comparamos si el nombre cambió
        if (nombre_nuevo != nombre_inicial)
        {
            try
            {
                //Creamos el JSON para actualizar el nombre
                val datos = JSONObject()
                datos.put("nombre", nombre_nuevo)

                var mensaje = "Error al actualizar el nombre de usuario"
                //Si logra actualizar el nombre
                if (servicio.metodoPut("usuarios/?correo=" + usuario.getCorreo(), datos))
                {
                    mensaje = "Nombre actualizado correctamente"
                    usuario.setNombre(nombre_nuevo)
                    //Recreamos la actividad principal (que contiene el menu lateral) para que se refleje visiblemente
                }
                //Y mostramos al usuario si se logró o no actualizar el nombre
                Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                servicio.desconectar()
            }
            catch (e: JSONException)
            {
                e.printStackTrace()
            }
        }
    }

    private fun elegirImagen()
    {
        getContext()?.let {
            CropImage.activity() //Iniciamos la actividad para cortar la imagen
                .setGuidelines(CropImageView.Guidelines.ON)//Encendemos la restricción de cortado
                .setAspectRatio(300,300)//Restringimos el radio de cortado de la imagen para que sea cuadrado
                .start(it, this)//Iniciamos la actividad
        }
    }

    //Cuando obtenemos el resultado de la actividad donde cortamos la imagen...
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            //Si se logró seleccionar y cortar la imagen correctamente
            if (resultCode == RESULT_OK)
            {
                //Obtenemos la dirección de la imagen cortada
                val resultUri = result.uri
                imagen_nueva = resultUri
                //Convertimos la imagen a un Bitmap
                val bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, resultUri)
                //Reescalamos el bitmap al tamaño del Image Button donde se mostrará
                val imagen_mostrar = Bitmap.createScaledBitmap(bitmap, 300, 300, false)
                //Y finalmente mostramos la imagen tratada al usuario
                Glide.with(this).load(imagen_mostrar).into(fotoperfil)
            }
            //De lo contrario
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error.toString()
                //Mostramos el mensaje de error al usuario
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
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

        //Se coloca la imagen actual de forma grafica en la aplicacion
        Glide.with(this).load(usuario.getUrlFoto()).into(fotoperfil)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_editar_perfil, container, false)
    }

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
            editar_perfil().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
