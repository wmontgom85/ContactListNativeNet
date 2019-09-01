package com.wmontgom85.contactlist.viewmodel


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.wmontgom85.contactlist.api.APIHandler
import com.wmontgom85.contactlist.api.APITask
import com.wmontgom85.contactlist.repo.DBHelper
import com.wmontgom85.contactlist.api.RESTRequest
import com.wmontgom85.contactlist.apiresult.PersonsApiResult
import com.wmontgom85.contactlist.repo.PersonDao
import com.wmontgom85.contactlist.model.Person
import com.wmontgom85.contactlist.sealed.APIResult
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class PersonsViewModel(application: Application) : AndroidViewModel(application) {
    //create a new Job
    private val parentJob = Job()

    private val personDao : PersonDao? by lazy { DBHelper.getInstance(application)?.personDao() }

    //create a coroutine context with the job and the dispatcher
    private val coroutineContext : CoroutineContext get() = parentJob + Dispatchers.Default

    //create a coroutine scope with the coroutine context
    private val scope = CoroutineScope(coroutineContext)

    //live data that will be populated as persons update
    val personsLiveData = MutableLiveData<List<Person>>()

    val errorHandler = MutableLiveData<String>()

    @Suppress("UNCHECKED_CAST")
    fun getRandomPerson() {
        ///launch the coroutine scope
        scope.launch {
            // create the task
            // example for when you need to parse a List of Object
            // val type = Types.newParameterizedType(List::class.java, Person::class.java)
            // val adapter = moshi.adapter<List<String>>(type)

            val moshi =  Moshi.Builder().build()
            val adapter = moshi.adapter(PersonsApiResult::class.java)
            val task = APITask(adapter as JsonAdapter<Any>, null, "An error has occurred.")
            val request = RESTRequest()

            APIHandler.apiCall(task, request).run {
                when (this) {
                    is APIResult.Success -> {
                        val p = data as PersonsApiResult

                        p.results[0].let { p ->
                            p.fill()
                            personDao?.insert(p)
                            personsLiveData.postValue(personDao?.getPeople())
                        }
                    }

                    is APIResult.Error -> {
                        Log.d("1.MainActivity", "Exception - ${exception.message}")

                        errorHandler.postValue("An error has occurred. Please try again.")
                    }
                }
            }
        }
    }

    fun deleteAllPersons() {
        scope.launch {
            personDao?.deleteAll()
            getPersons()
        }
    }

    fun deletePersonFromDB(pId : Int) {
        scope.launch {
            personDao?.delete(pId)
            getPersons()
        }
    }

    fun deletePersonFromDB(p : Person) {
        scope.launch {
            personDao?.delete(p)
            getPersons()
        }
    }

    fun getPersonsFromDB() {
        scope.launch { getPersons() }
    }

    fun getPersons() {
        personsLiveData.postValue(personDao?.getPeople())
    }

    fun cancelRequests() = coroutineContext.cancel()
}