package com.wmontgom85.contactlist.viewmodel

import androidx.lifecycle.ViewModel
import com.wmontgom85.contactlist.api.APIHandler
import com.wmontgom85.contactlist.api.ViewModelInjector

abstract class BaseViewModel: ViewModel() {
    private val injector: ViewModelInjector = DaggerViewModelInjector
        .builder()
        .networkModule(APIHandler)
        .build()

    init {
        inject()
    }

    /**
     * Injects the required dependencies
     */
    private fun inject() {
        when (this) {
            is PersonsViewModel -> injector.injectPersonsVM(this)
        }
    }
}