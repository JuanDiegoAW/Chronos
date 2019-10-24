package com.example.login.Clases

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.Toast
import android.R.attr.button
import com.example.login.R


internal class AdapterButtonsOnClickListener(var context: Context, var lista:ArrayList<Button>) : View.OnClickListener {

    override fun onClick(v: View) {
        if (lista.size<5){
            val b = v as Button
            Toast.makeText(this.context, "Ha reservado:  ${b.text}", Toast.LENGTH_SHORT).show()
            v.isEnabled=false
            v.setBackgroundColor(Color.RED)
            lista.add(b)
        }else{
            Toast.makeText(this.context, "El Limite de asientos a reservar son 5", Toast.LENGTH_SHORT).show()
        }

    }
}