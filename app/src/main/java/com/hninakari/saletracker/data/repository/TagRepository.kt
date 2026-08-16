package com.hninakari.saletracker.data.repository

import android.content.Context
import com.hninakari.saletracker.core.SyncTrigger
import com.hninakari.saletracker.data.database.AppDatabase
import com.hninakari.saletracker.data.database.TagEntity
import com.hninakari.saletracker.data.model.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TagRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val tagDao = database.tagDao()

    private fun toEntity(tag: Tag): TagEntity {
        return TagEntity(
            id = tag.id,
            name = tag.name,
            color = tag.color,
            isDeleted = tag.isDeleted
        )
    }

    private fun toModel(entity: TagEntity): Tag {
        return Tag(
            id = entity.id,
            name = entity.name,
            color = entity.color,
            isDeleted = entity.isDeleted
        )
    }

    fun getAllTags(): Flow<List<Tag>> {
        return tagDao.getAllTags().map { entities ->
            entities.map { toModel(it) }
        }
    }

    fun getTagsByIds(tagIds: List<Int>): Flow<List<Tag>> {
        return tagDao.getTagsByIds(tagIds).map { entities ->
            entities.map { toModel(it) }
        }
    }

    fun searchTags(query: String): Flow<List<Tag>> {
        return tagDao.searchTags(query).map { entities ->
            entities.map { toModel(it) }
        }
    }

    suspend fun getTagById(tagId: Int): Tag? {
        val entity = tagDao.getTagById(tagId)
        return entity?.let { toModel(it) }
    }

    suspend fun addTag(name: String, color: String? = null) {
        val tag = TagEntity(
            name = name,
            color = color
        )
        tagDao.insertTag(tag)
        SyncTrigger.triggerUpload()
    }

    suspend fun updateTag(tag: Tag) {
        tagDao.updateTag(toEntity(tag))
        SyncTrigger.triggerUpload()
    }

    suspend fun deleteTag(tagId: Int) {
        tagDao.softDeleteTag(tagId)
        SyncTrigger.triggerUpload()
    }

    suspend fun restoreTag(tagId: Int) {
        tagDao.restoreTag(tagId)
        SyncTrigger.triggerUpload()
    }
}
