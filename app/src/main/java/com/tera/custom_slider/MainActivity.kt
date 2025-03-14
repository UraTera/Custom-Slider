package com.tera.custom_slider

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tera.custom_slider.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        const val VALUE = "value"
    }

    private lateinit var binding: ActivityMainBinding
    private var myValue = 0f
    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Orientation
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        sp = getSharedPreferences("settings", Context.MODE_PRIVATE)
        myValue = sp.getFloat(VALUE, 0f)

        setSlider()
        initSlider()

    }

    private fun setSlider() = with(binding) {
        slWave.valueMax = 50f
        slWave.value = myValue
        slStretch.value = myValue
        slIincrease.value = myValue

        slNormal.value = myValue
        var str = "%.1f".format(myValue)
        str = str.replace(',', '.')
        tvNormal.text = str
    }

    private fun initSlider() = with(binding) {
        slNormal.setOnChangeListener {
            myValue = it
            slWave.value = it
            slStretch.value = it
            slIincrease.value = it

            var str = "%.1f".format(it)
            str = str.replace(',', '.')
            tvNormal.text = str
        }
    }

    override fun onStop() {
        super.onStop()
        val editor = sp.edit()
        editor.putFloat(VALUE, myValue)
        editor.apply()
    }

}