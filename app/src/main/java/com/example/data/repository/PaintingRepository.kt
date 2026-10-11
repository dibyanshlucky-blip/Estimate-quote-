package com.example.data.repository

import com.example.data.dao.PaintingDao
import com.example.data.model.CatalogueItemEntity
import com.example.data.model.DirectMeasurementEntity
import com.example.data.model.EstimateItemEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.ProjectWithDetails
import com.example.data.model.RoomMeasurementEntity
import com.example.data.sample.DefaultCatalogue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

class PaintingRepository(private val dao: PaintingDao) {

    val allProjects: Flow<List<ProjectEntity>> = dao.getAllProjects()
    val allCatalogueItems: Flow<List<CatalogueItemEntity>> = dao.getAllCatalogueItems()
    val allPayments: Flow<List<PaymentEntity>> = dao.getAllPayments()

    fun getProjectById(id: Long): Flow<ProjectEntity?> = dao.getProjectById(id)
    fun getRoomsByProject(projectId: Long): Flow<List<RoomMeasurementEntity>> = dao.getRoomsByProject(projectId)
    fun getDirectByProject(projectId: Long): Flow<List<DirectMeasurementEntity>> = dao.getDirectByProject(projectId)
    fun getEstimateItemsByProject(projectId: Long): Flow<List<EstimateItemEntity>> = dao.getEstimateItemsByProject(projectId)
    fun getPaymentsByProject(projectId: Long): Flow<List<PaymentEntity>> = dao.getPaymentsByProject(projectId)

    fun getProjectWithDetails(projectId: Long): Flow<ProjectWithDetails?> {
        return combine(
            dao.getProjectById(projectId),
            dao.getRoomsByProject(projectId),
            dao.getDirectByProject(projectId),
            dao.getEstimateItemsByProject(projectId),
            dao.getPaymentsByProject(projectId)
        ) { project, rooms, directs, items, payments ->
            if (project != null) {
                ProjectWithDetails(
                    project = project,
                    rooms = rooms,
                    directMeasurements = directs,
                    estimateItems = items,
                    payments = payments
                )
            } else {
                null
            }
        }
    }

    suspend fun getProjectWithDetailsDirect(projectId: Long): ProjectWithDetails? {
        val project = dao.getProjectByIdDirect(projectId) ?: return null
        val rooms = dao.getRoomsByProjectDirect(projectId)
        val directs = dao.getDirectByProjectDirect(projectId)
        val items = dao.getEstimateItemsByProjectDirect(projectId)
        val payments = dao.getPaymentsByProjectDirect(projectId)
        return ProjectWithDetails(project, rooms, directs, items, payments)
    }

    // Seeding default catalogue and sample data if empty
    suspend fun initializeDefaultDataIfNeeded() {
        val existingCatalogue = dao.getAllCatalogueItemsDirect()
        if (existingCatalogue.isEmpty()) {
            dao.insertCatalogueItems(DefaultCatalogue.getDefaultItems())
        }

        val existingProjects = dao.getAllProjects().first()
        if (existingProjects.isEmpty()) {
            val sampleProject = DefaultCatalogue.getSampleProject()
            val projectId = dao.insertProject(sampleProject)
            dao.insertRooms(DefaultCatalogue.getSampleRooms(projectId))
            dao.insertDirects(DefaultCatalogue.getSampleDirectMeasurements(projectId))
            dao.insertEstimateItems(DefaultCatalogue.getSampleEstimateItems(projectId))
            val payments = DefaultCatalogue.getSamplePayments(projectId)
            payments.forEach { dao.insertPayment(it.copy(projectId = projectId)) }

            // Seed default workers and sample attendance & progress
            val workers = DefaultCatalogue.getDefaultWorkers()
            dao.insertWorkers(workers)
            val attendanceList = DefaultCatalogue.getSampleAttendance(projectId)
            attendanceList.forEach { dao.insertAttendance(it) }
            dao.insertOrUpdateSiteProgress(DefaultCatalogue.getSampleProgress(projectId))
        }
    }

    // Projects CRUD
    suspend fun insertProject(project: ProjectEntity): Long = dao.insertProject(project)
    suspend fun updateProject(project: ProjectEntity) = dao.updateProject(project)
    suspend fun deleteProjectById(id: Long) = dao.deleteProjectById(id)
    suspend fun updateProjectStatus(id: Long, status: String) = dao.updateProjectStatus(id, status)

    suspend fun duplicateProject(projectId: Long): Long {
        val details = getProjectWithDetailsDirect(projectId) ?: return -1
        val newProject = details.project.copy(
            id = 0,
            estimateNumber = details.project.estimateNumber + "-COPY",
            siteName = details.project.siteName + " (Copy)",
            status = "Draft",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val newProjectId = dao.insertProject(newProject)
        val newRooms = details.rooms.map { it.copy(id = 0, projectId = newProjectId) }
        dao.insertRooms(newRooms)
        val newDirects = details.directMeasurements.map { it.copy(id = 0, projectId = newProjectId) }
        dao.insertDirects(newDirects)
        val newItems = details.estimateItems.map { it.copy(id = 0, projectId = newProjectId) }
        dao.insertEstimateItems(newItems)
        return newProjectId
    }

    // Room CRUD
    suspend fun insertRoom(room: RoomMeasurementEntity): Long = dao.insertRoom(room)
    suspend fun updateRoom(room: RoomMeasurementEntity) = dao.updateRoom(room)
    suspend fun deleteRoomById(id: Long) = dao.deleteRoomById(id)
    suspend fun duplicateRoom(roomId: Long) {
        val rooms = dao.getRoomsByProjectDirect(0) // or lookup
        // We can duplicate by passing room
    }

    // Direct CRUD
    suspend fun insertDirect(direct: DirectMeasurementEntity): Long = dao.insertDirect(direct)
    suspend fun updateDirect(direct: DirectMeasurementEntity) = dao.updateDirect(direct)
    suspend fun deleteDirectById(id: Long) = dao.deleteDirectById(id)

    // Estimate Items CRUD
    suspend fun insertEstimateItem(item: EstimateItemEntity): Long = dao.insertEstimateItem(item)
    suspend fun updateEstimateItem(item: EstimateItemEntity) = dao.updateEstimateItem(item)
    suspend fun deleteEstimateItemById(id: Long) = dao.deleteEstimateItemById(id)
    suspend fun replaceEstimateItems(projectId: Long, items: List<EstimateItemEntity>) {
        dao.deleteEstimateItemsByProject(projectId)
        dao.insertEstimateItems(items)
    }

    // Payment CRUD
    suspend fun insertPayment(payment: PaymentEntity): Long = dao.insertPayment(payment)
    suspend fun deletePaymentById(id: Long) = dao.deletePaymentById(id)

    // Catalogue CRUD
    suspend fun insertCatalogueItem(item: CatalogueItemEntity): Long = dao.insertCatalogueItem(item)
    suspend fun updateCatalogueItem(item: CatalogueItemEntity) = dao.updateCatalogueItem(item)
    suspend fun deleteCatalogueItemById(id: Long) = dao.deleteCatalogueItemById(id)
    // Workers CRUD
    val allWorkers: Flow<List<com.example.data.model.WorkerEntity>> = dao.getAllWorkers()
    suspend fun insertWorker(worker: com.example.data.model.WorkerEntity): Long = dao.insertWorker(worker)
    suspend fun updateWorker(worker: com.example.data.model.WorkerEntity) = dao.updateWorker(worker)
    suspend fun deleteWorkerById(id: Long) = dao.deleteWorkerById(id)

    // Attendance CRUD
    fun getAttendanceByProject(projectId: Long): Flow<List<com.example.data.model.AttendanceRecordEntity>> = dao.getAttendanceByProject(projectId)
    suspend fun insertAttendance(record: com.example.data.model.AttendanceRecordEntity): Long = dao.insertAttendance(record)
    suspend fun deleteAttendanceById(id: Long) = dao.deleteAttendanceById(id)

    // Site Progress CRUD
    fun getSiteProgress(projectId: Long): Flow<com.example.data.model.SiteProgressEntity?> = dao.getSiteProgress(projectId)
    suspend fun saveSiteProgress(progress: com.example.data.model.SiteProgressEntity) = dao.insertOrUpdateSiteProgress(progress)

    suspend fun resetCatalogueToDefault() {
        dao.clearCatalogue()
        dao.insertCatalogueItems(DefaultCatalogue.getDefaultItems())
    }
}
