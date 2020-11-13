package com.example.conversioncalculator

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.view.Menu;
import android.view.MenuItem
import android.widget.TextView
import com.example.conversioncalculator.UnitsConverter.*
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var isLength = true
    private var fromLenUnits = LengthUnits.Yards
    private var toLenUnits = LengthUnits.Meters
    private var fromVolUnits = VolumeUnits.Gallons
    private var toVolUnits = VolumeUnits.Liters
    private val SETTINGS_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val calcButton = findViewById<Button>(R.id.calcButton)
        val clearButton = findViewById<Button>(R.id.clearButton)
        val modeButton = findViewById<Button>(R.id.modeButton)

        // set listeners for buttons
        clearButton.setOnClickListener {
            clearFields()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(fromInput.windowToken, 0)
        }
        calcButton.setOnClickListener {
            calculate()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(fromInput.windowToken, 0)
        }
        modeButton.setOnClickListener {
            changeMode()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(fromInput.windowToken, 0)
        }


        val fromInput = findViewById<EditText>(R.id.fromInput)
        val toInput = findViewById<EditText>(R.id.toInput)
        fromInput.setOnFocusChangeListener { _, _ -> toInput.text.clear() }
        toInput.setOnFocusChangeListener { _, _ -> fromInput.text.clear() }
    }

    private fun clearFields() {
        val fromInput = findViewById<EditText>(R.id.fromInput)
        val toInput = findViewById<EditText>(R.id.toInput)

        fromInput.text.clear()
        toInput.text.clear()
    }


    private fun calculate() {
        val fromInput = findViewById<EditText>(R.id.fromInput)
        val toInput = findViewById<EditText>(R.id.toInput)

        // If both input fields empty - display error message.
        if(fromInput.text.isEmpty() && toInput.text.isEmpty()) {
            AlertDialog.Builder(this).setTitle("Error Message").
            setMessage("Please enter a value to calculate.")
                    .setPositiveButton("OK") { dialog, _->
                        //clearFields()
                        dialog.dismiss()
                    }.create().show()

        }

        val fieldToRead = if (fromInput.text.isNotEmpty()) fromInput else toInput
        val fieldToPopulate = if (fromInput.text.isEmpty()) fromInput else toInput

        if (isLength) {
            val fromUnits = if (fieldToRead == fromInput) fromLenUnits else toLenUnits
            val toUnits = if (fromUnits == fromLenUnits) toLenUnits else fromLenUnits
            fieldToPopulate.setText(
                    convert(fieldToRead.text.toString().toDouble(), fromUnits, toUnits).toString())
        } else {
            val fromUnits = if (fieldToRead == fromInput) fromVolUnits else toVolUnits
            val toUnits = if (fromUnits == fromVolUnits) toVolUnits else fromVolUnits
            fieldToPopulate.setText(
                    convert(fieldToRead.text.toString().toDouble(), fromUnits, toUnits).toString())
        }
    }

    private fun changeMode() {
        isLength = !isLength
        val title = findViewById<TextView>(R.id.titleLabel)
        val fromUnits = findViewById<TextView>(R.id.fromUnits)
        val toUnits = findViewById<TextView>(R.id.toUnits)
        findViewById<EditText>(R.id.toInput).text.clear()
        if (isLength) {
            title.text = resources.getText(R.string.lengthTitle)
            fromUnits.text = fromLenUnits.name
            toUnits.text = toLenUnits.name
        } else {
            title.text = resources.getText(R.string.volumeTitle)
            fromUnits.text = fromVolUnits.name
            toUnits.text = toVolUnits.name
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(layout.menu.menu_settings,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings_action -> {
                navigateToSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToSettings() {
        val intent = Intent(this, SettingsActivity::class.java).apply {
            if (isLength) {
                putExtra("IS_LENGTH", true)
                putExtra("FROM_UNITS", fromLenUnits.name)
                putExtra("TO_UNITS", toLenUnits.name)
            } else {
                putExtra("IS_LENGTH", false)
                putExtra("FROM_UNITS", fromVolUnits.name)
                putExtra("TO_UNITS", toVolUnits.name)
            }
        }
        startActivityForResult(intent, SETTINGS_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_REQUEST_CODE) {
            clearFields()
            if (resultCode == Activity.RESULT_OK) {
                val intentFromUnits = data?.getStringExtra("FROM_UNITS")
                val intentToUnits = data?.getStringExtra("TO_UNITS")
                val fromUnits = findViewById<TextView>(R.id.fromUnits)
                val toUnits = findViewById<TextView>(R.id.toUnits)
                if (isLength) {
                    if (intentFromUnits != null) {
                        val unitString = LengthUnits.valueOf(intentFromUnits)
                        fromLenUnits = unitString
                        fromUnits.text = fromLenUnits.name
                    }

                    if (intentToUnits != null) {
                        val unitString = LengthUnits.valueOf(intentToUnits)
                        toLenUnits = unitString
                        toUnits.text = toLenUnits.name
                    }
                } else {
                    if (intentFromUnits != null) {
                        val unitString = VolumeUnits.valueOf(intentFromUnits)
                        fromVolUnits = unitString
                        fromUnits.text = fromVolUnits.name
                    }

                    if (intentToUnits != null) {
                        val unitString = VolumeUnits.valueOf(intentToUnits)
                        toVolUnits = unitString
                        toUnits.text = toVolUnits.name
                    }
                }
            }
        }
    }
}
