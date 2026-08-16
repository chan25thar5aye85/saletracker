package com.hninakari.saletracker.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Query("SELECT * FROM tags WHERE isDeleted = 0 ORDER BY name ASC")
    fun getAllTags(): Flow<List<TagEntity>>
    
    @Query("SELECT * FROM tags WHERE isDeleted = 0 AND id IN (:tagIds)")
    fun getTagsByIds(tagIds: List<Int>): Flow<List<TagEntity>>
    
    @Query("SELECT * FROM tags WHERE isDeleted = 0 AND name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchTags(query: String): Flow<List<TagEntity>>
    
    @Query("SELECT * FROM tags WHERE isDeleted = 0 AND id = :tagId")
    suspend fun getTagById(tagId: Int): TagEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagEntity)
    
    @Update
    suspend fun updateTag(tag: TagEntity)
    
    @Query("UPDATE tags SET isDeleted = 1 WHERE id = :tagId")
    suspend fun softDeleteTag(tagId: Int)
    
    @Query("UPDATE tags SET isDeleted = 0 WHERE id = :tagId")
    suspend fun restoreTag(tagId: Int)
}
