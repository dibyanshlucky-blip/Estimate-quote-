package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CatalogueItemEntity
import com.example.data.model.DirectMeasurementEntity
import com.example.data.model.EstimateItemEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.RoomMeasurementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaintingDao {

    // Projects
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    fun getProjectById(id: Long): Flow<ProjectEntity?>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun getProjectByIdDirect(id: Long): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: Long)

    @Query("UPDATE projects SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateProjectStatus(id: Long, status: String, updatedAt: Long = System.currentTimeMillis())

    // Room Measurements
    @Query("SELECT * FROM room_measurements WHERE projectId = :projectId ORDER BY id ASC")
    fun getRoomsByProject(projectId: Long): Flow<List<RoomMeasurementEntity>>

    @Query("SELECT * FROM room_measurements WHERE projectId = :projectId ORDER BY id ASC")
    suspend fun getRoomsByProjectDirect(projectId: Long): List<RoomMeasurementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: RoomMeasurementEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRooms(rooms: List<RoomMeasurementEntity>)

    @Update
    suspend fun updateRoom(room: RoomMeasurementEntity)

    @Delete
    suspend fun deleteRoom(room: RoomMeasurementEntity)

    @Query("DELETE FROM room_measurements WHERE id = :id")
    suspend fun deleteRoomById(id: Long)

    // Direct Measurements
    @Query("SELECT * FROM direct_measurements WHERE projectId = :projectId ORDER BY id ASC")
    fun getDirectByProject(projectId: Long): Flow<List<DirectMeasurementEntity>>

    @Query("SELECT * FROM direct_measurements WHERE projectId = :projectId ORDER BY id ASC")
    suspend fun getDirectByProjectDirect(projectId: Long): List<DirectMeasurementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDirect(direct: DirectMeasurementEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDirects(directs: List<DirectMeasurementEntity>)

    @Update
    suspend fun updateDirect(direct: DirectMeasurementEntity)

    @Delete
    suspend fun deleteDirect(direct: DirectMeasurementEntity)

    @Query("DELETE FROM direct_measurements WHERE id = :id")
    suspend fun deleteDirectById(id: Long)

    // Estimate Items
    @Query("SELECT * FROM estimate_items WHERE projectId = :projectId ORDER BY id ASC")
    fun getEstimateItemsByProject(projectId: Long): Flow<List<EstimateItemEntity>>

    @Query("SELECT * FROM estimate_items WHERE projectId = :projectId ORDER BY id ASC")
    suspend fun getEstimateItemsByProjectDirect(projectId: Long): List<EstimateItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstimateItem(item: EstimateItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstimateItems(items: List<EstimateItemEntity>)

    @Update
    suspend fun updateEstimateItem(item: EstimateItemEntity)

    @Delete
    suspend fun deleteEstimateItem(item: EstimateItemEntity)

    @Query("DELETE FROM estimate_items WHERE id = :id")
    suspend fun deleteEstimateItemById(id: Long)

    @Query("DELETE FROM estimate_items WHERE projectId = :projectId")
    suspend fun deleteEstimateItemsByProject(projectId: Long)

    // Payments
    @Query("SELECT * FROM payments WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getPaymentsByProject(projectId: Long): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE projectId = :projectId ORDER BY createdAt DESC")
    suspend fun getPaymentsByProjectDirect(projectId: Long): List<PaymentEntity>

    @Query("SELECT * FROM payments ORDER BY createdAt DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long

    @Delete
    suspend fun deletePayment(payment: PaymentEntity)

    @Query("DELETE FROM payments WHERE id = :id")
    suspend fun deletePaymentById(id: Long)

    // Catalogue Items
    @Query("SELECT * FROM catalogue_items ORDER BY id ASC")
    fun getAllCatalogueItems(): Flow<List<CatalogueItemEntity>>

    @Query("SELECT * FROM catalogue_items ORDER BY id ASC")
    suspend fun getAllCatalogueItemsDirect(): List<CatalogueItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatalogueItem(item: CatalogueItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatalogueItems(items: List<CatalogueItemEntity>)

    @Update
    suspend fun updateCatalogueItem(item: CatalogueItemEntity)

    @Delete
    suspend fun deleteCatalogueItem(item: CatalogueItemEntity)

    @Query("DELETE FROM catalogue_items WHERE id = :id")
    suspend fun deleteCatalogueItemById(id: Long)

    @Query("DELETE FROM catalogue_items")
    suspend fun clearCatalogue()

    // Workers
    @Query("SELECT * FROM workers ORDER BY id ASC")
    fun getAllWorkers(): Flow<List<com.example.data.model.WorkerEntity>>

    @Query("SELECT * FROM workers ORDER BY id ASC")
    suspend fun getAllWorkersDirect(): List<com.example.data.model.WorkerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorker(worker: com.example.data.model.WorkerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkers(workers: List<com.example.data.model.WorkerEntity>)

    @Update
    suspend fun updateWorker(worker: com.example.data.model.WorkerEntity)

    @Query("DELETE FROM workers WHERE id = :id")
    suspend fun deleteWorkerById(id: Long)

    // Attendance Records
    @Query("SELECT * FROM attendance_records WHERE projectId = :projectId ORDER BY id DESC")
    fun getAttendanceByProject(projectId: Long): Flow<List<com.example.data.model.AttendanceRecordEntity>>

    @Query("SELECT * FROM attendance_records WHERE projectId = :projectId ORDER BY id DESC")
    suspend fun getAttendanceByProjectDirect(projectId: Long): List<com.example.data.model.AttendanceRecordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(record: com.example.data.model.AttendanceRecordEntity): Long

    @Query("DELETE FROM attendance_records WHERE id = :id")
    suspend fun deleteAttendanceById(id: Long)

    // Site Progress
    @Query("SELECT * FROM site_progress WHERE projectId = :projectId LIMIT 1")
    fun getSiteProgress(projectId: Long): Flow<com.example.data.model.SiteProgressEntity?>

    @Query("SELECT * FROM site_progress WHERE projectId = :projectId LIMIT 1")
    suspend fun getSiteProgressDirect(projectId: Long): com.example.data.model.SiteProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSiteProgress(progress: com.example.data.model.SiteProgressEntity)

    // Wood & Polish Measurements
    @Query("SELECT * FROM wood_polish_items WHERE projectId = :projectId ORDER BY id ASC")
    fun getWoodPolishByProject(projectId: Long): Flow<List<com.example.data.model.WoodPolishEntity>>

    @Query("SELECT * FROM wood_polish_items WHERE projectId = :projectId ORDER BY id ASC")
    suspend fun getWoodPolishByProjectDirect(projectId: Long): List<com.example.data.model.WoodPolishEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWoodPolishItem(item: com.example.data.model.WoodPolishEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWoodPolishItems(items: List<com.example.data.model.WoodPolishEntity>)

    @Update
    suspend fun updateWoodPolishItem(item: com.example.data.model.WoodPolishEntity)

    @Delete
    suspend fun deleteWoodPolishItem(item: com.example.data.model.WoodPolishEntity)

    @Query("DELETE FROM wood_polish_items WHERE id = :id")
    suspend fun deleteWoodPolishItemById(id: Long)
}
