package com.example.fil_rouge

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {

    private var toto = 0.toLong()
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnHomeCycle = findViewById<ImageButton>(R.id.btnHomeCycle)
        btnHomeCycle.setOnClickListener {
            goCycle()
        }
        btnHomeDisconnect.setOnClickListener{view ->
            showMessage(view, "Logging Out...")
            signOut()
        }
        btnHomeForm.setOnClickListener{
            goForm()
        }
        btnHomePermission.setOnClickListener{
            goPermission()
        }
        btnHomeWeb.setOnClickListener{
            goWeb()
        }

        mAuth.addAuthStateListener {
            if(mAuth.currentUser == null){
                this.finish()
            }
        }
    }

    private fun goWeb() {
        val intent = Intent(this, WebServiceActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        if(System.currentTimeMillis()-toto<3000)
        {
            signOut()
        }else
        {
            Toast.makeText(this,"Appuyez une deuxième fois pour se déconnecter", Toast.LENGTH_LONG).show()
            toto = System.currentTimeMillis()
        }


    }


    private fun goCycle(){
        val intent = Intent(this, CycleActivity::class.java)
        startActivity(intent)
    }

    private fun goForm(){
        val intent = Intent(this, FormActivity::class.java)
        startActivity(intent)
    }
    private fun goPermission(){
        val intent = Intent(this, PermissionActivity::class.java)
        startActivity(intent)
    }

    private fun signOut(){
        mAuth.signOut()
    }

    private fun showMessage(view: View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show()
    }
}
