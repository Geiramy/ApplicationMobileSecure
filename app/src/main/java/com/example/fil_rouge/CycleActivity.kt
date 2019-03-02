package com.example.fil_rouge

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_cycle.*

class CycleActivity : AppCompatActivity() {
    private var isActivityRunning = false
    private var totalLog = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cycle)

        val btnCycleRetour = findViewById<Button>(R.id.btnCycleRetour)
        btnCycleRetour.setOnClickListener {
            back()
        }
        showLog("Cycle d'activité: onCreate")
    }

    override fun onStart(){
        super.onStart()
        showLog("Cycle d'activité: onStart")
    }
    override fun onResume(){
        super.onResume()
        isActivityRunning = true
        showLog("Cycle d'activité: onResume")
    }
    override fun onPause(){
        super.onPause()
        isActivityRunning = false
        showLog("Cycle d'activité: onPause")
    }
    override fun onStop(){
        super.onStop()
        showLog("Cycle d'activité: onStop")
    }
    override fun onDestroy(){
        super.onDestroy()
        Toast.makeText(this,"Cycle d'activité: onDestroy", Toast.LENGTH_LONG).show()
    }
    private fun showLog(msg: String) {
        totalLog = totalLog+msg+"\n"

        if(isActivityRunning){
            textCycleLog.text = totalLog
        }
    }
    private fun back() {
        finish()
    }
}
