package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AiMobileEntity::class], version = 1, exportSchema = false)
abstract class AiMobileDatabase : RoomDatabase() {
    abstract fun aiMobileDao(): AiMobileDao

    companion object {
        @Volatile
        private var INSTANCE: AiMobileDatabase? = null

        fun getDatabase(context: Context): AiMobileDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AiMobileDatabase::class.java,
                    "ai_mobile_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
