package com.example.login.Activities

import android.content.Context
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.login.R


class ViewPageAdapter: PagerAdapter{

    var layouts: IntArray
    var inflater : LayoutInflater
    var contexto : Context

    constructor(layouts : IntArray, contexto : Context) : super() {
        this.layouts = layouts
        this.contexto = contexto
        inflater = contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return layouts.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layout_actual = inflater.inflate(layouts[position], container, false)
        container.addView(layout_actual)
        return layout_actual
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        var layout_actual= `object` as View
        container.removeView(layout_actual)
    }
}