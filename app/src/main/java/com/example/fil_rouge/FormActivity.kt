package com.example.fil_rouge

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_form.*
import java.util.*
import android.widget.DatePicker
import android.widget.Toast
import com.firebase.ui.auth.AuthUI.getApplicationContext
import org.json.JSONObject
import java.io.*
import java.lang.Math.log
import java.text.SimpleDateFormat


class FormActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        // Creates Android Key Store and provides manage functions



// Create and Save asymmetric key
        keyStoreWrapper.createAndroidKeyStoreAsymmetricKey("MASTER_KEY")

        // Get key from keyStore
        var masterKey = keyStoreWrapper.getAndroidKeyStoreAsymmetricKeyPair("MASTER_KEY")

        // Creates Cipher with given transformation and provides encrypt and decrypt functions
        var cipherWrapper = CipherWrapper("RSA/ECB/PKCS1Padding")

        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener{ datePicker: DatePicker, year: Int, monthofYear: Int, dayOfMonth: Int ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthofYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val formater = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH)
                txtFormDate.text = formater.format(cal.time)//"$dayOfMonth/$monthofYear/$year"
            }

        txtFormDate.setOnClickListener{
            showDatePicker(dateSetListener , cal)
        }
        btnFormRegister.setOnClickListener{
            registerData(textFormFirstname.text.toString(), textFormName.text.toString(), txtFormDate.text.toString())
        }
        btnFormView.setOnClickListener {
            readData()
        }
    }



    private fun showDatePicker(dateSetListener: DatePickerDialog.OnDateSetListener, cal: Calendar) {
        DatePickerDialog(
            this@FormActivity,
            dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    private fun registerData(firstname: String,name: String,date: String) {
        if(firstname.isNotEmpty() && name.isNotEmpty() && date.isNotEmpty())
        {
            try {
                val toto = JSONObject()
                toto.putOpt("Firstname", firstname)
                toto.putOpt("Name", name)
                toto.putOpt("Birthday", date)

                val fileOutput = openFileOutput("user_data_json", Context.MODE_PRIVATE)
                var encrypted = ""
                val data = toto.toString()
                try {
                    encrypted = encrypt(data)
                    Log.d("TEST", "encrypted:$encrypted")
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                fileOutput.write(encrypted.toByteArray())
                fileOutput.close()
                Toast.makeText(this,"Formulaire enregistré!", Toast.LENGTH_LONG).show()
            }catch (e: FileNotFoundException)
            {
                Log.e("ErrorStorage", e.message)
            }

        }else
        {
            Toast.makeText(this,"Formulaire incorrect.", Toast.LENGTH_LONG).show()
        }

    }

    private fun readData()
    {
        try {
            val fileInput = openFileInput("user_data_json")
            val txt = convertInputStreamToString(fileInput)
            var decrypted = ""
            try {
                decrypted = decrypt(txt)
                Log.d("TEST", "decrypted:$decrypted")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val myJSON = JSONObject(decrypted)
            //Toast.makeText(this,myJSON.getString("Firstname") + "!!!!", Toast.LENGTH_LONG).show()
            fileInput.close()
            val dialogAlert = AlertDialog.Builder(this@FormActivity)
            dialogAlert.setTitle("Vos infos")
            val birthdate = myJSON.getString("Birthday")
            val cal = Calendar.getInstance()
            val age = cal.get(Calendar.YEAR)-birthdate.substringAfterLast("/").toInt()
            dialogAlert.setMessage(myJSON.getString("Firstname")+" " +myJSON.getString("Name")+" atteint l'age de "+age+" an cette année!"  )
            val dialog: AlertDialog = dialogAlert.create()
            dialog.show()
        }catch (e: FileNotFoundException)
        {
            Log.e("ErrorStorage", e.message)
        }
    }
    private fun convertInputStreamToString(inputStream: InputStream): String {
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val stringBuffer = StringBuffer("")
        var readLine: String? = bufferedReader.readLine()

        while(readLine != null) {
            stringBuffer.append(readLine)
            stringBuffer.append("\n")
            readLine = bufferedReader.readLine()
        }

        inputStream.close()
        return stringBuffer.toString()
    }

    private val keyStoreWrapper = KeyStoreWrapper(context)

    companion object {
        const val MASTER_KEY = "MASTER_KEY"
        @SuppressLint("RestrictedApi", "StaticFieldLeak")
        val context = getApplicationContext()

    }

    private fun encrypt(data: String): String {
        val masterKey = keyStoreWrapper.getAndroidKeyStoreAsymmetricKeyPair(MASTER_KEY)
        return CipherWrapper(CipherWrapper.TRANSFORMATION_ASYMMETRIC).encrypt(data, masterKey?.public)
    }

    private fun decrypt(data: String): String {
        val masterKey = keyStoreWrapper.getAndroidKeyStoreAsymmetricKeyPair(MASTER_KEY)
        return CipherWrapper(CipherWrapper.TRANSFORMATION_ASYMMETRIC).decrypt(data, masterKey?.private)
    }


}