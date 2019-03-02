package com.example.fil_rouge

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_permission.*
import java.text.SimpleDateFormat
import java.util.*

class PermissionActivity : AppCompatActivity() {
    private var contactModelArrayList: ArrayList<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        btnPermissionLocation.setOnClickListener{
            getLocation()
        }
        getContacts()
        btnPermissionGallery.setOnClickListener{
            getImageAlbum()
        }
    }

    private fun getImageAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, 1)
        }
    }

    private fun getContacts() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_CONTACTS),2)
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_CONTACTS),2)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            showContacts()
            // Permission has already been granted
        }
    }

    private fun showContacts() {

        contactModelArrayList = ArrayList()
        val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED + " DESC")
        while (phones!!.moveToNext()) {
            val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER))
            var lastTime = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED))
            val formater = SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.FRENCH)
            contactModelArrayList!!.add(name+": "+phoneNumber + "\n "+ (if (lastTime.toLong()==0.toLong()){"Jamais contacté"} else{ "Dernier contact le: " +formater.format(Date(lastTime.toLong())) } ))
        }
        phones.close()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, contactModelArrayList)
        dynPermissionContacts!!.adapter = adapter
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            showLocation()
            // Permission has already been granted
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            textPermissionLatitude.text = "Latitude: " + location.latitude
            textPermissionLongitude.text = "Longitude: " + location.longitude
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1 -> {
                if(resultCode == Activity.RESULT_OK)
                {
                    btnPermissionGallery.setImageBitmap(MediaStore.Images.Media.getBitmap(
                        this.contentResolver, data?.data))
                }
                return
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {//ACCESS_FINE_LOCATION
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    showLocation()
                } else {
                    Toast.makeText(this,"Position indisponible :/", Toast.LENGTH_LONG).show()
                }
                return
            }
            2 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission accordée
                    showContacts()
                } else {
                    // permission non accordée
                }
                return
            }
            else -> {
                // Toutes autres requetes
            }
        }
    }

    private fun showLocation()
    {
        Toast.makeText(this,"Recherche de votre position...", Toast.LENGTH_LONG).show()
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        try {
            // Request location updates
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        } catch(ex: SecurityException) {
            Log.d("FunShowLocation", "Security Exception, no location available")
        }
    }
}
