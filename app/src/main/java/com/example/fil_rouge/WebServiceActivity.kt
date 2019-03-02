package com.example.fil_rouge

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_web_service.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.io.*


class WebServiceActivity : AppCompatActivity() {
    private val teachers: Array<SpiritualTeacher>
        get() =
            arrayOf(SpiritualTeacher("Rumi", "Out beyond ideas of wrongdoing and rightdoing there is a field.I'll meet you there.", R.drawable.logo_isen),
                SpiritualTeacher("Anthony De Mello", "Don't Carry Over Experiences from the past", R.drawable.logo_isen),
                SpiritualTeacher("Eckhart Tolle", "Walk as if you are kissing the Earth with your feet.", R.drawable.logo_isen),
                SpiritualTeacher("Meister Eckhart", "Man suffers only because he takes seriously what the gods made for fun.", R.drawable.logo_isen),
                SpiritualTeacher("Mooji", "I have lived with several Zen masters -- all of them cats.", R.drawable.logo_isen),
                SpiritualTeacher("Confucius", "I'm simply saying that there is a way to be sane. I'm saying that you ", R.drawable.logo_isen),
                SpiritualTeacher("Francis Lucille", "The way out is through the door. Why is it that no one will use this method?", R.drawable.logo_isen),
                SpiritualTeacher("Thich Nhat Hanh", "t is the power of the mind to be unconquerable.", R.drawable.logo_isen),
                SpiritualTeacher("Dalai Lama", "It's like you took a bottle of ink and you threw it at a wall. Smash! ", R.drawable.logo_isen),
                SpiritualTeacher("Jiddu Krishnamurti", "A student, filled with emotion and crying, implored, 'Why is there so much suffering?", R.drawable.logo_isen),
                SpiritualTeacher("Osho", "Only the hand that erases can write the true thing.", R.drawable.logo_isen),
                SpiritualTeacher("Sedata", "Many have died; you also will die. The drum of death is being beaten.", R.drawable.logo_isen),
                SpiritualTeacher("Allan Watts", "Where there are humans, You'll find flies,And Buddhas.", R.drawable.logo_isen),
                SpiritualTeacher("Leo Gura", "Silence is the language of Om. We need silence to be able to reach our Self.", R.drawable.logo_isen),
                SpiritualTeacher("Rupert Spira", "One day in my shoes and a day for me in your shoes, the beauty of travel lies ", R.drawable.logo_isen),
                SpiritualTeacher("Sadhguru", "Like vanishing dew,a passing apparition or the sudden flashnof lightning", R.drawable.logo_isen))
    internal var sb: StringBuilder? = null
    internal lateinit var adapter: ContactAdapter

    inner class SpiritualTeacher(var name: String?, val quote: String, val image: Int)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.INTERNET),3)
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.INTERNET),3)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            setContentView(R.layout.activity_web_service)
            val url = "https://randomuser.me/api/?results=10&nat=fr"
            WebServiceTask(object : WebServiceInterface{
                override fun onSuccess(result: String) {
                    var gson = Gson()
                    var model = gson.fromJson(result, Character::class.java)
                    //Adapter
                    adapter = ContactAdapter(this@WebServiceActivity, model.results)
                    //RECYCLER
                    rv_contact_list.layoutManager = LinearLayoutManager(this@WebServiceActivity)
                    rv_contact_list.itemAnimator = DefaultItemAnimator()

                    //SET ADAPTER
                    rv_contact_list.adapter = adapter
                }

                override fun onError() {
                    Log.d("Error","WebServiceTask failed..")
                }

            }).execute(url)
        }


    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            3 -> {//ACCESS_FINE_LOCATION
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //OK
                } else {
                    Toast.makeText(this,"Accés à internet non autorisé :/", Toast.LENGTH_LONG).show()
                    finish()
                }
                return
            }
            else -> {
                // Toutes autres requetes
            }
        }
    }
}
