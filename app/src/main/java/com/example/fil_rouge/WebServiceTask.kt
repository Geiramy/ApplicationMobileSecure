package com.example.fil_rouge

import android.os.AsyncTask
import android.util.Log
import com.google.gson.Gson
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class WebServiceTask(val myInterface: WebServiceInterface) : AsyncTask<String, String, String>() {

    override fun doInBackground(vararg urls: String?): String {
        var inString =""
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(urls[0])

            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connectTimeout = 60000
            urlConnection.readTimeout = 60000

            inString = streamToString(urlConnection.inputStream)
        } catch (ex: Exception) {

            Log.d("________doInBackground",ex.toString())
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect()
            }
        }
        return inString
    }

    override fun onPostExecute(result: String?) {
        // Done
        if(!(result.isNullOrEmpty()))
        {
            myInterface.onSuccess(result ?: "")
        }else{
            myInterface.onError()
        }
    }
    fun streamToString(inputStream: InputStream): String {
        val bufferReader = BufferedReader(InputStreamReader(inputStream))
        var line: String
        var result = ""

        try {
            do {
                line = bufferReader.readLine()
                if (line != null) {
                    result += line
                }
            } while (line != null)
            inputStream.close()
        } catch (ex: Exception) {

        }

        return result
    }
}