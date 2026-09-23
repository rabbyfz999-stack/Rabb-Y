package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AiMobileDao {
    @Query("SELECT * FROM ai_mobile_items ORDER BY timestamp DESC")
    fun getAllItems(): Flow<List<AiMobileEntity>>

    @Query("SELECT * FROM ai_mobile_items WHERE category = :category ORDER BY timestamp DESC")
    fun getItemsByCategory(category: String): Flow<List<AiMobileEntity>>

    @Query("SELECT * FROM ai_mobile_items WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteItems(): Flow<List<AiMobileEntity>>

    @Query("SELECT * FROM ai_mobile_items WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR prompt LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchItems(query: String): Flow<List<AiMobileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: AiMobileEntity): Long

    @Update
    suspend fun updateItem(item: AiMobileEntity)

    @Query("UPDATE ai_mobile_items SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Query("DELETE FROM ai_mobile_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("DELETE FROM ai_mobile_items")
    suspend fun clearAll()
}
