package com.example.fil_rouge

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.TextView

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

import com.firebase.ui.auth.AuthUI
import com.google.firebase.iid.FirebaseInstanceId
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.cert.CertificateException
import java.util.*
import java.util.Arrays.asList
import javax.crypto.*
import javax.crypto.spec.GCMParameterSpec


class LoginActivity : AppCompatActivity() {

    //private var logged = false

    private var mAuth = FirebaseAuth.getInstance()
    private var keyStore = KeyStore.getInstance("AndroidKeyStore")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initKeyStore()

        val loginBtn = findViewById<View>(R.id.btnLogin) as Button


        loginBtn.setOnClickListener(View.OnClickListener { view ->
            login()
        })


    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()

        testEncrypt()

    }

    private fun login() {
        val emailTxt = findViewById<View>(R.id.textLoginIdentifiant) as EditText
        val email = emailTxt.text.toString()
        val passwordTxt = findViewById<View>(R.id.textLoginPassword) as EditText
        val password = passwordTxt.text.toString()

        if (!email.isEmpty() && !password.isEmpty()) {
            this.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, HomeActivity::class.java))
                        Toast.makeText(this, "Successfully Logged in :)", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Error Logging in :(", Toast.LENGTH_SHORT).show()
                    }
                })

        } else {
            Toast.makeText(this, "Please fill up the Credentials :|", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun testEncrypt()
    {
        initKeyStore()

        var check = false
        val currentId= FirebaseInstanceId.getInstance().id
        if(File("/data/data/com.example.fil_rouge/files/instance_data_iv").isFile() && File("/data/data/com.example.fil_rouge/files/instance_data_enc").isFile())
        {
            val Bla = Collections.list(keyStore.aliases())
            for (ali in Bla) {
                if (ali == "InstanceIDSaved") {
                    Log.d("||||Check Alias", "Found!")
                    check = true
                }
            }
        }else
        {
            Log.d("||||File Check", "File not found ")
        }
        if(check)//Now we decrypt the instance ID and compare it to the current one
        {
            val wantedId = readData("InstanceIDSaved")
            if(currentId != wantedId)
            {
                Log.d("||||Id Check", "Missmatch 😕")
                //Supprimer les données sensibles
                finish()//On quitte l'app
            }else
            {
                Log.d("||||Id Check", "Correct 😀")
            }
        }else//We have to put the current id into the keystore
        {
            encryptText("InstanceIDSaved", currentId)
            Log.d("||||New Instance", "Succesfully installed")
            //Il faudrait supprimer tous les fichiers sensibles si il y en a
        }
    }

    @Throws(KeyStoreException::class, CertificateException::class, NoSuchAlgorithmException::class, IOException::class)
    private fun initKeyStore() {
        //keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
    }

    @Throws(
        UnrecoverableEntryException::class,
        NoSuchAlgorithmException::class,
        KeyStoreException::class,
        NoSuchProviderException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        IOException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        InvalidAlgorithmParameterException::class
    )
    fun decryptData(alias: String, encryptedData: ByteArray, encryptionIv: ByteArray): String {

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")//ICI

        val spec = GCMParameterSpec(128, encryptionIv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKeyDec(alias), spec)

        return String(cipher.doFinal(encryptedData), StandardCharsets.UTF_8)
    }

    @Throws(NoSuchAlgorithmException::class, UnrecoverableEntryException::class, KeyStoreException::class)
    private fun getSecretKeyDec(alias: String): SecretKey {
        return (keyStore.getEntry(alias, null) as KeyStore.SecretKeyEntry).secretKey
    }

    //Decrypt
    @RequiresApi(Build.VERSION_CODES.M)
    @Throws(
        UnrecoverableEntryException::class,
        NoSuchAlgorithmException::class,
        KeyStoreException::class,
        NoSuchProviderException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        IOException::class,
        InvalidAlgorithmParameterException::class,
        SignatureException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    fun encryptText(alias: String, textToEncrypt: String): ByteArray {

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKeyEnc(alias))

        var iv = cipher.iv
        var encryption = cipher.doFinal(textToEncrypt.toByteArray(charset("UTF-8")))

        registerData(encryption,iv)
        return encryption
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Throws(NoSuchAlgorithmException::class, NoSuchProviderException::class, InvalidAlgorithmParameterException::class)
    private fun getSecretKeyEnc(alias: String): SecretKey {

        val keyGenerator = KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")

        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        )

        return keyGenerator.generateKey()
    }
    //Register data
    private fun registerData(encryption: ByteArray,iv: ByteArray) {
        if(encryption.isNotEmpty() && iv.isNotEmpty())
        {
            try {
                val fileEncOutput = openFileOutput("instance_data_enc", Context.MODE_PRIVATE)
                fileEncOutput.write(encryption)
                fileEncOutput.close()
                val fileIVOutput = openFileOutput("instance_data_iv", Context.MODE_PRIVATE)
                fileIVOutput.write(iv)
                fileIVOutput.close()
                Toast.makeText(this,"Appli enregistré!!", Toast.LENGTH_LONG).show()
            }catch (e: FileNotFoundException)
            {
                Log.e("ErrorStorage", e.message)
            }
        }else
        {
            Toast.makeText(this,"Problème...", Toast.LENGTH_LONG).show()
        }

    }

    private fun readData(alias: String) : String
    {
        var instanceID = ""
        try {
            val fileInputIV = openFileInput("instance_data_iv")
            val iv = fileInputIV.readBytes()
            fileInputIV.close()

            val fileInputEnc = openFileInput("instance_data_enc")
            val encryption = fileInputEnc.readBytes()
            fileInputEnc.close()
            instanceID = decryptData(alias, encryption, iv)

        }catch (e: FileNotFoundException)
        {
            Log.e("ErrorStorage", e.message)
        }
        return instanceID
    }

}
