package com.wmontgom85.contactlist.api

import android.util.Log
import com.wmontgom85.contactlist.sealed.APIResult
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * Module which provides all required dependencies about network
 */
class ConnectionProvider(
    private val task : APITask,
    private val request : RESTRequest
) {
    /**
     * Makes an HTTP request with a provided RESTRequest object
     */
    fun <T : Any> makeRequest(): APIResult<T> {
        var response = ""

        try {
            val url = URL(request.restURL)

            with(url.openConnection() as HttpURLConnection) {
                connectTimeout = request.timeout
                readTimeout = request.timeout
                requestMethod = request.requestType
                doInput = (request.requestType.equals("POST"))
                doOutput = true
                setRequestProperty("charset", "utf-8")

                if (request.requestType.equals("POST")) {
                    request.buildQuery()?.let {
                        val os = outputStream
                        val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
                        writer.write(it)
                        writer.flush()
                        writer.close()
                        os.close()
                    }
                }

                connect()

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    val br = BufferedReader(InputStreamReader(inputStream))
                    br.readLine().forEach {
                        response += it
                    }
                    return APIResult.Success(response)
                } else {
                    return APIResult.Error(IOException(task.errorMessage?.let { "An error has occurred." }))
                }
            }
        } catch (tx: Throwable) {
            Log.d("1.ConnectionProvider", tx.message)

            // set the connection to null so no further execution can occur
            return APIResult.Error(IOException("${task.errorMessage?.let { "An error has occurred." }}. Error Code CP001"))
        }
    }
}

/**
 * if (response.isSuccessful) return Result.Success(response.body()!!)

return Result.Error(IOException("Error Occurred during getting safe Api result, Custom ERROR - $errorMessage"))
 */
