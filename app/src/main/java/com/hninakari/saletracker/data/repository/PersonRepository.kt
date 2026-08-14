package com.hninakari.saletracker.data.repository

import android.content.Context
import com.hninakari.saletracker.core.SyncTrigger
import com.hninakari.saletracker.data.database.AppDatabase
import com.hninakari.saletracker.data.database.PersonEntity
import com.hninakari.saletracker.data.model.Person
import com.hninakari.saletracker.data.model.PersonType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PersonRepository(context: Context) {

    private val database = AppDatabase.getDatabase(context)
    private val personDao = database.personDao()

    private fun toEntity(person: Person): PersonEntity {
        return PersonEntity(
            id = person.id,
            name = person.name,
            phone = person.phone,
            type = person.type.name,
            notes = person.notes,
            dateAdded = person.dateAdded,
            isDeleted = person.isDeleted
        )
    }

    private fun toModel(entity: PersonEntity): Person {
        return Person(
            id = entity.id,
            name = entity.name,
            phone = entity.phone,
            type = PersonType.valueOf(entity.type),
            notes = entity.notes,
            dateAdded = entity.dateAdded,
            isDeleted = entity.isDeleted
        )
    }

    fun getAllPeople(): Flow<List<Person>> {
        return personDao.getAllPeople().map { entities ->
            entities.map { toModel(it) }
        }
    }

    suspend fun getPersonById(personId: Int): Person? {
        val entity = personDao.getPersonById(personId)
        return entity?.let { toModel(it) }
    }

    fun searchPeople(query: String): Flow<List<Person>> {
        return personDao.searchPeople(query).map { entities ->
            entities.map { toModel(it) }
        }
    }

    suspend fun addPerson(person: Person) {
        personDao.insertPerson(toEntity(person))
        SyncTrigger.triggerUpload()
    }

    suspend fun updatePerson(person: Person) {
        personDao.updatePerson(toEntity(person))
        SyncTrigger.triggerUpload()
    }

    suspend fun deletePerson(personId: Int) {
        personDao.softDeletePerson(personId)
        SyncTrigger.triggerUpload()
    }
}
