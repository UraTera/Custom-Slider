package com.tera.custom_slider

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tera.custom_slider.databinding.ActivityMainBinding
import java.util.Timer
import java.util.TimerTask


class MainActivity : AppCompatActivity() {

    companion object {
        const val VALUE = "value"
    }

    private lateinit var binding: ActivityMainBinding
    private var valueSl = 0f
    private var valueCustom = 0f
    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Orientation
        //this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        sp = getSharedPreferences("settings", Context.MODE_PRIVATE)
        valueCustom = sp.getFloat(VALUE, 0f)

        setSlider()
        initSlider()

    }

    private fun initSlider() = with(binding) {

        slNormal.valueListener = { value: Float ->
            valueCustom = value
            slWave.value = value
            slStretch.value = value
            slIincrease.value = value

            var str = "%.1f".format(value)
            str  = str .replace(',', '.')
            tvNormal.text = str
        }
    }

    private fun setSlider() = with(binding) {
        slWave.value = valueCustom
        slStretch.value = valueCustom
        slIincrease.value = valueCustom

        slNormal.value = valueCustom
        var str = "%.1f".format(valueCustom)
        str  = str .replace(',', '.')
        tvNormal.text = str
    }

    override fun onStop() {
        super.onStop()
        val editor = sp.edit()
        editor.putFloat(VALUE, valueCustom)
        editor.apply()
    }


}