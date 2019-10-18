package com.example.login.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import com.example.login.Clases.ButtonsOnClickListener
import com.example.login.R

class Reservas : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservas)

        //DAtos de los layouts
        val llBotonera = findViewById(com.example.login.R.id.llBotonera) as LinearLayout
        val llBotonera1 = findViewById(com.example.login.R.id.llBotonera1) as LinearLayout
        val llBotonera2 = findViewById(com.example.login.R.id.llBotonera2) as LinearLayout
        val llBotonera3 = findViewById(com.example.login.R.id.llBotonera3) as LinearLayout
        val llBotonera4 = findViewById(com.example.login.R.id.llBotonera4) as LinearLayout

        var contador = 1
        for (i in 1 until 19) {

            val button = Button(this)
            button.textSize = 9F;
            button.text = contador.toString()
            contador +=5
            button.setOnClickListener(ButtonsOnClickListener(this));
            llBotonera.addView(button);
        }
        contador=2;
        for (i in 18 until 36) {
            val button = Button(this)
            //button.setLayoutParams(lp)
            button.textSize = 9F;
            button.text = contador.toString()
            contador+=5
            button.setOnClickListener(ButtonsOnClickListener(this));
            llBotonera1.addView(button);
        }
        contador =3
        for (i in 37 until 55) {
            val button = Button(this)
            //button.setLayoutParams(lp)
            button.textSize = 9F
            button.text = contador.toString()
            contador+=5
            button.setOnClickListener(ButtonsOnClickListener(this));
            llBotonera2.addView(button);
        }
        contador =4
        for (i in 55 until 73) {
            val button = Button(this)
            //button.setLayoutParams(lp)
            button.textSize = 9F;
            button.text = contador.toString()
            contador+=5
            button.setOnClickListener(ButtonsOnClickListener(this));
            llBotonera3.addView(button);
        }
        contador=5
        for (i in 73 until 91) {
            val button = Button(this)
            button.textSize = 9F
            button.text = contador.toString()
            contador+=5
            button.setOnClickListener(ButtonsOnClickListener(this));
            llBotonera4.addView(button)
        }
    }
}
