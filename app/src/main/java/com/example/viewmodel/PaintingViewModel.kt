package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.CatalogueItemEntity
import com.example.data.model.DirectMeasurementEntity
import com.example.data.model.EstimateItemEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.ProjectWithDetails
import com.example.data.model.RoomMeasurementEntity
import com.example.data.repository.PaintingRepository
import com.example.util.AppLanguage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppScreen {
    DASHBOARD,
    CUSTOMER_SITE,
    ROOM_CALCULATOR,
    DIRECT_CALCULATOR,
    CATALOGUE,
    ESTIMATE_BUILDER,
    PDF_QUOTATION,
    PAYMENTS,
    SAVED_PROJECTS,
    REPORTS,
    SETTINGS_BACKUP,
    MATERIAL_CALCULATOR,
    COLOR_PALETTE,
    LABOUR_ATTENDANCE,
    SITE_PROGRESS,
    WARRANTY_CERTIFICATE
}

class PaintingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PaintingRepository

    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val screenBackStack = mutableListOf(AppScreen.DASHBOARD)

    private val _selectedProjectId = MutableStateFlow<Long?>(null)
    val selectedProjectId: StateFlow<Long?> = _selectedProjectId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow("All")
    val statusFilter: StateFlow<String> = _statusFilter.asStateFlow()

    val allProjects: StateFlow<List<ProjectEntity>>
    val allCatalogueItems: StateFlow<List<CatalogueItemEntity>>
    val allPayments: StateFlow<List<PaymentEntity>>
    val allWorkers: StateFlow<List<com.example.data.model.WorkerEntity>>

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedProjectWithDetails: StateFlow<ProjectWithDetails?>

    @OptIn(ExperimentalCoroutinesApi::class)
    val projectAttendance: StateFlow<List<com.example.data.model.AttendanceRecordEntity>>

    @OptIn(ExperimentalCoroutinesApi::class)
    val siteProgress: StateFlow<com.example.data.model.SiteProgressEntity?>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = PaintingRepository(database.paintingDao())

        allProjects = repository.allProjects.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allCatalogueItems = repository.allCatalogueItems.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allPayments = repository.allPayments.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allWorkers = repository.allWorkers.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        selectedProjectWithDetails = _selectedProjectId.flatMapLatest { id ->
            if (id != null) {
                repository.getProjectWithDetails(id)
            } else {
                flowOf(null)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        projectAttendance = _selectedProjectId.flatMapLatest { id ->
            if (id != null) {
                repository.getAttendanceByProject(id)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        siteProgress = _selectedProjectId.flatMapLatest { id ->
            if (id != null) {
                repository.getSiteProgress(id)
            } else {
                flowOf(null)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        viewModelScope.launch {
            repository.initializeDefaultDataIfNeeded()
            // Set initial selected project if not selected
            allProjects.collect { projects ->
                if (_selectedProjectId.value == null && projects.isNotEmpty()) {
                    _selectedProjectId.value = projects.first().id
                }
            }
        }
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.ENGLISH) AppLanguage.HINDI else AppLanguage.ENGLISH
    }

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            screenBackStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        return if (screenBackStack.size > 1) {
            screenBackStack.removeAt(screenBackStack.size - 1)
            _currentScreen.value = screenBackStack.last()
            true
        } else if (_currentScreen.value != AppScreen.DASHBOARD) {
            _currentScreen.value = AppScreen.DASHBOARD
            screenBackStack.clear()
            screenBackStack.add(AppScreen.DASHBOARD)
            true
        } else {
            false
        }
    }

    fun selectProject(projectId: Long) {
        _selectedProjectId.value = projectId
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(status: String) {
        _statusFilter.value = status
    }

    fun createNewProject(
        customerName: String,
        mobileNumber: String,
        siteName: String,
        siteAddress: String,
        flatType: String,
        notes: String,
        reqs: String
    ) {
        viewModelScope.launch {
            val count = allProjects.value.size + 1
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
            val currentDate = dateFormat.format(Date())
            val estNumber = "DE-${SimpleDateFormat("yyyy", Locale.US).format(Date())}-${String.format(Locale.US, "%03d", count)}"

            val newProject = ProjectEntity(
                customerName = customerName.ifBlank { "Client $count" },
                mobileNumber = mobileNumber,
                siteName = siteName.ifBlank { "Site $count" },
                siteAddress = siteAddress,
                flatType = flatType,
                estimateNumber = estNumber,
                estimateDate = currentDate,
                siteNotes = notes,
                customerRequirements = reqs,
                status = "Draft"
            )
            val newId = repository.insertProject(newProject)
            _selectedProjectId.value = newId
            navigateTo(AppScreen.CUSTOMER_SITE)
        }
    }

    fun updateProject(project: ProjectEntity) {
        viewModelScope.launch {
            repository.updateProject(project.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun updateProjectStatus(projectId: Long, status: String) {
        viewModelScope.launch {
            repository.updateProjectStatus(projectId, status)
        }
    }

    fun deleteProject(projectId: Long) {
        viewModelScope.launch {
            repository.deleteProjectById(projectId)
            if (_selectedProjectId.value == projectId) {
                val remaining = allProjects.value.filter { it.id != projectId }
                _selectedProjectId.value = remaining.firstOrNull()?.id
            }
        }
    }

    fun duplicateProject(projectId: Long) {
        viewModelScope.launch {
            val newId = repository.duplicateProject(projectId)
            if (newId > 0) {
                _selectedProjectId.value = newId
            }
        }
    }

    // Room operations
    fun addRoom(
        roomName: String,
        lengthFt: Double,
        lengthIn: Double,
        widthFt: Double,
        widthIn: Double,
        heightFt: Double,
        heightIn: Double,
        doorCount: Int,
        doorWidthFt: Double,
        doorHeightFt: Double,
        windowCount: Int,
        windowWidthFt: Double,
        windowHeightFt: Double,
        additionalDeductionsSqft: Double,
        wallPainting: Boolean,
        ceilingPainting: Boolean,
        notes: String,
        detailedData: String = ""
    ) {
        val projId = _selectedProjectId.value ?: return
        viewModelScope.launch {
            val room = RoomMeasurementEntity(
                projectId = projId,
                roomName = roomName,
                lengthFt = lengthFt,
                lengthIn = lengthIn,
                widthFt = widthFt,
                widthIn = widthIn,
                heightFt = heightFt,
                heightIn = heightIn,
                doorCount = doorCount,
                doorWidthFt = doorWidthFt,
                doorHeightFt = doorHeightFt,
                windowCount = windowCount,
                windowWidthFt = windowWidthFt,
                windowHeightFt = windowHeightFt,
                additionalDeductionsSqft = additionalDeductionsSqft,
                wallPaintingEnabled = wallPainting,
                ceilingPaintingEnabled = ceilingPainting,
                notes = notes,
                detailedData = detailedData
            )
            repository.insertRoom(room)
        }
    }

    fun saveDetailedRoom(room: RoomMeasurementEntity) {
        val projId = _selectedProjectId.value ?: return
        viewModelScope.launch {
            if (room.id == 0L) {
                repository.insertRoom(room.copy(projectId = projId))
            } else {
                repository.updateRoom(room.copy(projectId = projId))
            }
        }
    }

    fun updateRoom(room: RoomMeasurementEntity) {
        viewModelScope.launch {
            repository.updateRoom(room)
        }
    }

    fun deleteRoom(roomId: Long) {
        viewModelScope.launch {
            repository.deleteRoomById(roomId)
        }
    }

    fun duplicateRoom(room: RoomMeasurementEntity) {
        viewModelScope.launch {
            repository.insertRoom(room.copy(id = 0, roomName = "${room.roomName} (Copy)"))
        }
    }

    // Direct Sqft operations
    fun addDirectMeasurement(
        surfaceName: String,
        quantity: Double,
        unit: String,
        notes: String
    ) {
        val projId = _selectedProjectId.value ?: return
        viewModelScope.launch {
            val direct = DirectMeasurementEntity(
                projectId = projId,
                surfaceName = surfaceName,
                quantity = quantity,
                unit = unit,
                notes = notes
            )
            repository.insertDirect(direct)
        }
    }

    fun updateDirectMeasurement(direct: DirectMeasurementEntity) {
        viewModelScope.launch {
            repository.updateDirect(direct)
        }
    }

    fun deleteDirectMeasurement(id: Long) {
        viewModelScope.launch {
            repository.deleteDirectById(id)
        }
    }

    // Estimate items operations
    fun addEstimateItem(item: EstimateItemEntity) {
        val projId = _selectedProjectId.value ?: return
        viewModelScope.launch {
            repository.insertEstimateItem(item.copy(projectId = projId))
        }
    }

    fun updateEstimateItem(item: EstimateItemEntity) {
        viewModelScope.launch {
            repository.updateEstimateItem(item)
        }
    }

    fun deleteEstimateItem(id: Long) {
        viewModelScope.launch {
            repository.deleteEstimateItemById(id)
        }
    }

    fun saveProductToCatalogue(item: EstimateItemEntity) {
        viewModelScope.launch {
            val title = if (item.productVariant.isNotBlank()) {
                "${item.workDescription} - ${item.productName} (${item.productVariant})"
            } else if (item.productName.isNotBlank()) {
                "${item.workDescription} - ${item.productName}"
            } else {
                item.workDescription
            }
            val brandProd = if (item.brandName.isNotBlank() && item.productName.isNotBlank()) {
                "${item.brandName} ${item.productName}"
            } else if (item.brandName.isNotBlank()) {
                item.brandName
            } else {
                item.productName.ifBlank { "Custom" }
            }
            val catItem = CatalogueItemEntity(
                category = item.category,
                titleEn = title,
                titleHi = title,
                brandProduct = brandProd,
                surfaceType = item.surfaceType,
                unit = item.unit,
                materialRate = item.materialRate,
                labourRate = item.labourRate,
                coats = item.coats,
                remarks = item.remarks
            )
            repository.insertCatalogueItem(catItem)
        }
    }

    fun autoPopulateEstimateFromMeasurements(
        selectedCatalogueItems: List<CatalogueItemEntity>
    ) {
        val projWithDetails = selectedProjectWithDetails.value ?: return
        val projId = projWithDetails.project.id
        val totalPaintable = projWithDetails.combinedPaintableSqft

        viewModelScope.launch {
            val newItems = selectedCatalogueItems.map { cat ->
                val qty = when (cat.unit.lowercase()) {
                    "sqft", "sqm" -> if (totalPaintable > 0) totalPaintable else 500.0
                    "rft" -> 50.0
                    "piece", "pieces" -> 2.0
                    else -> 1.0
                }
                var brand = ""
                var product = cat.brandProduct
                val knownBrands = listOf("Asian Paints", "Berger Paints", "Berger", "Nerolac", "Dulux", "Indigo Paints", "Birla White", "J.K. Cement")
                for (b in knownBrands) {
                    if (cat.brandProduct.contains(b, ignoreCase = true)) {
                        brand = if (b == "Berger") "Berger Paints" else b
                        product = cat.brandProduct.replace(b, "", ignoreCase = true).trim().removePrefix("/").removePrefix("-").trim()
                        break
                    }
                }
                EstimateItemEntity(
                    projectId = projId,
                    category = cat.category,
                    workDescription = cat.titleEn,
                    brandName = brand,
                    productName = product,
                    productVariant = "",
                    finish = "Matt",
                    surfaceType = cat.surfaceType,
                    quantity = qty,
                    unit = cat.unit,
                    materialRate = cat.materialRate,
                    labourRate = cat.labourRate,
                    coats = cat.coats,
                    remarks = cat.remarks
                )
            }
            repository.replaceEstimateItems(projId, newItems)
        }
    }

    // Payment operations
    fun addPayment(
        amount: Double,
        paymentDate: String,
        paymentMethod: String,
        referenceNumber: String,
        remarks: String
    ) {
        val projId = _selectedProjectId.value ?: return
        viewModelScope.launch {
            val payment = PaymentEntity(
                projectId = projId,
                amount = amount,
                paymentDate = paymentDate,
                paymentMethod = paymentMethod,
                referenceNumber = referenceNumber,
                remarks = remarks
            )
            repository.insertPayment(payment)
        }
    }

    fun deletePayment(id: Long) {
        viewModelScope.launch {
            repository.deletePaymentById(id)
        }
    }

    // Catalogue operations
    fun addCatalogueItem(item: CatalogueItemEntity) {
        viewModelScope.launch {
            repository.insertCatalogueItem(item)
        }
    }

    fun updateCatalogueItem(item: CatalogueItemEntity) {
        viewModelScope.launch {
            repository.updateCatalogueItem(item)
        }
    }

    fun deleteCatalogueItem(id: Long) {
        viewModelScope.launch {
            repository.deleteCatalogueItemById(id)
        }
    }

    fun resetCatalogue() {
        viewModelScope.launch {
            repository.resetCatalogueToDefault()
        }
    }

    // Workers operations
    fun addWorker(name: String, role: String, dailyRate: Double, mobile: String) {
        viewModelScope.launch {
            val worker = com.example.data.model.WorkerEntity(
                name = name,
                role = role,
                dailyRate = dailyRate,
                mobileNumber = mobile
            )
            repository.insertWorker(worker)
        }
    }

    fun updateWorker(worker: com.example.data.model.WorkerEntity) {
        viewModelScope.launch {
            repository.updateWorker(worker)
        }
    }

    fun deleteWorker(id: Long) {
        viewModelScope.launch {
            repository.deleteWorkerById(id)
        }
    }

    // Attendance operations
    fun recordAttendance(
        workerId: Long,
        date: String,
        status: String,
        overtime: Double,
        advance: Double,
        notes: String
    ) {
        val projId = _selectedProjectId.value ?: return
        viewModelScope.launch {
            val record = com.example.data.model.AttendanceRecordEntity(
                workerId = workerId,
                projectId = projId,
                date = date,
                status = status,
                overtimeHours = overtime,
                advancePaid = advance,
                notes = notes
            )
            repository.insertAttendance(record)
        }
    }

    fun deleteAttendance(id: Long) {
        viewModelScope.launch {
            repository.deleteAttendanceById(id)
        }
    }

    // Site progress operations
    fun updateSiteProgress(progress: com.example.data.model.SiteProgressEntity) {
        viewModelScope.launch {
            repository.saveSiteProgress(progress)
        }
    }
}
