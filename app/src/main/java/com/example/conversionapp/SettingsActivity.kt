package com.example.conversionapp


import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.google.android.material.floatingactionbutton.FloatingActionButton


class SettingsActivity : AppCompatActivity() {
    private var isLength = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        isLength = intent.getBooleanExtra("IS_LENGTH", true)
        val fromSpinner = findViewById<Spinner>(R.id.spin)
        val toSpinner = findViewById<Spinner>(R.id.spin2)
        val doneButton = findViewById<FloatingActionButton>(R.id.doneButton)

        
        val fromUnits = intent.getStringExtra("FROM_UNITS")
        val options = ArrayList<String>()
        if (isLength) {
            for (unit in UnitsConverter.LengthUnits.values()) {
                options.add(unit.name)
            }
        } else {
            for (unit in UnitsConverter.VolumeUnits.values()) {
                options.add(unit.name)
            }
        }

        val select = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, options
        )
        select.setDropDownViewResource(android.R.layout.simple_spinner_item)
        fromSpinner.adapter = select
        fromSpinner.setSelection(options.indexOf(fromUnits.ifEmpty { options[0] }), false)

        val toUnits = intent.getStringExtra("TO_UNITS")
        toSpinner.adapter = select
        toSpinner.setSelection(options.indexOf(toUnits.ifEmpty { options[0] }), false)
        doneButton.setOnClickListener { saveSettings() }
    }

    private fun saveSettings() {
        val result = Intent()
        val fromUnits = findViewById<Spinner>(R.id.spin).selectedItem.toString()
        result.putExtra("FROM_UNITS", fromUnits)
        val toUnits = findViewById<Spinner>(R.id.spin2).selectedItem.toString()
        result.putExtra("TO_UNITS", toUnits)
        setResult(Activity.RESULT_OK, result)
        finish()
    }
}
