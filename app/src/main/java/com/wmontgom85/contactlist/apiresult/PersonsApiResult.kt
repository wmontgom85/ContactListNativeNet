package com.wmontgom85.contactlist.apiresult

import com.wmontgom85.contactlist.model.Person

data class PersonsApiResult(
    val results : List<Person>
)