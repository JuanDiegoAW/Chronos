package com.example.login.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.example.login.Clases.Usuario
import com.example.login.R
import com.google.android.gms.maps.model.*

class Mapa : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private val usuario = Usuario.iniciar()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_mapa, container, false)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapa_fragmento) as SupportMapFragment?  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment!!.getMapAsync { mMap ->
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            mMap.clear() //clear old markers

            if (usuario.getPermiso() != false) {

                mMap!!.isMyLocationEnabled = true

                //SI SE OBTUVO UNA UBICACION, CENTRAR EL MAPA
                val currentLatLng = LatLng(usuario.getLocation().latitude, usuario.getLocation().longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }
            else
            {
                val Guatemala = LatLng(15.7835, -90.2308)
                mMap.addMarker(MarkerOptions().position(Guatemala).title("Guatemala"))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Guatemala, 7f))
            }

        }


        //mMap.addMarker(
        //    MarkerOptions()
        //        .position(LatLng(37.3092293, -122.1136845))
        //        .title("Captain America")
        //)

        return rootView
    }

    // TODO: Rename method, update argument and hook method into UI event
    //fun onButtonPressed(uri: Uri)
    //{
    //    listener?.onFragmentInteraction(uri)
    //}

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            Mapa().apply {
                arguments = Bundle().apply {
                    //putString(ARG_PARAM1, param1)
                    //putString(ARG_PARAM2, param2)
                }
            }
    }
}
