package com.wmontgom85.contactlist.dao

import androidx.room.*
import com.wmontgom85.contactlist.model.Person

@Dao
public interface PersonDao {
    @Query("SELECT * FROM Person")
    fun getPeople(): List<Person>

    @Query("SELECT * FROM Person WHERE _id = :id")
    fun getPerson(id: Long) : Person

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(person: Person)

    @Delete
    fun delete(person: Person)

    @Update
    fun update(person: Person)

    @Query("DELETE FROM Person")
    fun deleteAll()
}