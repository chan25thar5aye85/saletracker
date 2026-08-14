package com.hninakari.saletracker.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Query("SELECT * FROM people WHERE isDeleted = 0 ORDER BY name ASC")
    fun getAllPeople(): Flow<List<PersonEntity>>
    
    @Query("SELECT * FROM people WHERE isDeleted = 0 AND id = :personId")
    suspend fun getPersonById(personId: Int): PersonEntity?
    
    @Query("SELECT * FROM people WHERE isDeleted = 0 AND name LIKE '%' || :query || '%'")
    fun searchPeople(query: String): Flow<List<PersonEntity>>
    
    @Insert
    suspend fun insertPerson(person: PersonEntity)
    
    @Update
    suspend fun updatePerson(person: PersonEntity)
    
    @Query("UPDATE people SET isDeleted = 1 WHERE id = :personId")
    suspend fun softDeletePerson(personId: Int)
}
