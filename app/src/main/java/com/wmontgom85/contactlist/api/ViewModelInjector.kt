package com.wmontgom85.contactlist.api

import com.wmontgom85.contactlist.viewmodel.PersonsViewModel
import dagger.Component
import javax.inject.Singleton

/**
 * Component providing inject() methods for presenters.
 */
@Singleton
@Component(modules = [(APIHandler::class)])
interface ViewModelInjector {
    /**
     * Injects required dependencies into the specified PersonsViewModel.
     * @param personsViewModel PersonsViewModel in which to inject the dependencies
     */
    fun injectPersonsVM(personsViewModel: PersonsViewModel)

    @Component.Builder
    interface Builder {
        fun build(): ViewModelInjector

        fun networkModule(networkModule: APIHandler): Builder
    }
}