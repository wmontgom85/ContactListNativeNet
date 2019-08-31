package com.wmontgom85.contactlist.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types.newParameterizedType
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wmontgom85.contactlist.api.APIHandler
import com.wmontgom85.contactlist.api.APITask
import com.wmontgom85.contactlist.api.RESTRequest
import com.wmontgom85.contactlist.model.Person
import com.wmontgom85.contactlist.sealed.APIResult
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class PersonsViewModel : ViewModel() {
    //create a new Job
    private val parentJob = Job()

    //create a coroutine context with the job and the dispatcher
    private val coroutineContext : CoroutineContext get() = parentJob + Dispatchers.Default

    //create a coroutine scope with the coroutine context
    private val scope = CoroutineScope(coroutineContext)

    //live data that will be populated as persons update
    val personsLiveData = MutableLiveData<APIResult<Any>>()

    @Suppress("UNCHECKED_CAST")
    fun getRandomPerson() {
        ///launch the coroutine scope
        scope.launch {
            // create the task
            // @TODO use DI for moshi and api task to avoid code duplication

            val moshi =  Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val adapter = moshi.adapter(Person::class.java)
            val task = APITask(adapter as JsonAdapter<Any>, null, "An error has occurred.")
            val request = RESTRequest()

            // make the network call
            personsLiveData.postValue(APIHandler.apiCall(task, request))
        }
    }

    fun cancelRequests() = coroutineContext.cancel()
}