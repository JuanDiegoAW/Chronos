package com.example.login.Clases

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.Toast
import android.R.attr.button



internal class ButtonsOnClickListener(var context: Context) : View.OnClickListener {

    override fun onClick(v: View) {
        val b = v as Button
        Toast.makeText(this.context, "Ha reservado: "+ b.text, Toast.LENGTH_SHORT).show()
        v.isEnabled=false
        //v.setBackgroundColor(Color.RED)

    }
}