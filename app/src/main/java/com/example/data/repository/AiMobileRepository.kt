package com.example.data.repository

import com.example.data.db.AiMobileDao
import com.example.data.db.AiMobileEntity
import kotlinx.coroutines.flow.Flow

class AiMobileRepository(private val dao: AiMobileDao) {
    val allItems: Flow<List<AiMobileEntity>> = dao.getAllItems()
    val favoriteItems: Flow<List<AiMobileEntity>> = dao.getFavoriteItems()

    fun getItemsByCategory(category: String): Flow<List<AiMobileEntity>> {
        return if (category == "All") {
            dao.getAllItems()
        } else {
            dao.getItemsByCategory(category)
        }
    }

    fun searchItems(query: String): Flow<List<AiMobileEntity>> {
        return if (query.isBlank()) {
            dao.getAllItems()
        } else {
            dao.searchItems(query.trim())
        }
    }

    suspend fun saveItem(item: AiMobileEntity): Long {
        return dao.insertItem(item)
    }

    suspend fun toggleFavorite(id: Long, currentFav: Boolean) {
        dao.setFavorite(id, !currentFav)
    }

    suspend fun deleteItem(id: Long) {
        dao.deleteItemById(id)
    }

    suspend fun clearHistory() {
        dao.clearAll()
    }
}
