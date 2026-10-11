package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.PaintingDao
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.CatalogueItemEntity
import com.example.data.model.DirectMeasurementEntity
import com.example.data.model.EstimateItemEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.RoomMeasurementEntity
import com.example.data.model.SiteProgressEntity
import com.example.data.model.WorkerEntity

@Database(
    entities = [
        ProjectEntity::class,
        RoomMeasurementEntity::class,
        DirectMeasurementEntity::class,
        CatalogueItemEntity::class,
        EstimateItemEntity::class,
        PaymentEntity::class,
        WorkerEntity::class,
        AttendanceRecordEntity::class,
        SiteProgressEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun paintingDao(): PaintingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "divyansh_painting.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
