package com.wmontgom85.contactlist.api

import com.wmontgom85.contactlist.sealed.APIResult
import java.io.IOException

/**
 * Module which provides all required dependencies about network
 */
object APIHandler {
    fun apiCall(task: APITask, request: RESTRequest): APIResult<Any> {
        val result: APIResult<Any> = ConnectionProvider(task, request).makeRequest()

        when (result) {
            is APIResult.Success -> {
                try {
                    // attempt parsing and set data as object
                    task.jsonAdapter.fromJson(result.data as String)?.let {
                        return APIResult.Success(it)
                    } ?: run {
                        // parsing error
                        return APIResult.Error(IOException("An error has occurred while parsing. Error code AH001"))
                    }
                } catch (tx : Throwable) {
                    // fatal error
                    return APIResult.Error(IOException("An error has occurred while parsing. Error code AH002"))
                }

            }
        }

        return result
    }
}




