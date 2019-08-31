package com.wmontgom85.contactlist.viewmodel


import android.app.Application
import android.util.Log
import androidx.annotation.NonNull
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.wmontgom85.contactlist.api.APIHandler
import com.wmontgom85.contactlist.api.APITask
import com.wmontgom85.contactlist.api.DBHelper
import com.wmontgom85.contactlist.api.RESTRequest
import com.wmontgom85.contactlist.apiresult.PersonsApiResult
import com.wmontgom85.contactlist.dao.PersonDao
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
            // @TODO use DI for moshi and api task to avoid code duplication

            val moshi =  Moshi.Builder().build()
            //val type = Types.newParameterizedType(List::class.java, Person::class.java)
            //val adapter = moshi.adapter<List<String>>(type)
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
                            getPersonsFromDB()
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

    fun getPersonsFromDB() {
        personsLiveData.postValue(personDao?.getPeople())
    }

    fun cancelRequests() = coroutineContext.cancel()
}