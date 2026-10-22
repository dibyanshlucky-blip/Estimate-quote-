package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppHeader
import com.example.ui.screens.CatalogueScreen
import com.example.ui.screens.ColorPaletteScreen
import com.example.ui.screens.CustomerSiteScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DirectCalculatorScreen
import com.example.ui.screens.EstimateBuilderScreen
import com.example.ui.screens.LabourAttendanceScreen
import com.example.ui.screens.MaterialCalculatorScreen
import com.example.ui.screens.PaymentScreen
import com.example.ui.screens.PdfQuotationScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.RoomCalculatorScreen
import com.example.ui.screens.SavedProjectsScreen
import com.example.ui.screens.SettingsBackupScreen
import com.example.ui.screens.SiteProgressScreen
import com.example.ui.screens.WarrantyCertificateScreen
import com.example.ui.screens.WoodPolishCalculatorScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.util.LanguageManager
import com.example.viewmodel.AppScreen
import com.example.viewmodel.PaintingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                PaintingApp()
            }
        }
    }
}

@Composable
fun PaintingApp(viewModel: PaintingViewModel = viewModel()) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val allProjects by viewModel.allProjects.collectAsStateWithLifecycle()
    val activeProjectDetails by viewModel.selectedProjectWithDetails.collectAsStateWithLifecycle()
    val catalogueItems by viewModel.allCatalogueItems.collectAsStateWithLifecycle()
    val allPayments by viewModel.allPayments.collectAsStateWithLifecycle()
    val allWorkers by viewModel.allWorkers.collectAsStateWithLifecycle()
    val projectAttendance by viewModel.projectAttendance.collectAsStateWithLifecycle()
    val siteProgress by viewModel.siteProgress.collectAsStateWithLifecycle()
    val businessProfile by viewModel.businessProfile.collectAsStateWithLifecycle()

    // Handle Android system back button
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
        viewModel.navigateBack()
    }

    val screenTitle = when (currentScreen) {
        AppScreen.DASHBOARD -> LanguageManager.get("app_title", language)
        AppScreen.CUSTOMER_SITE -> LanguageManager.get("customer_mgmt", language)
        AppScreen.ROOM_CALCULATOR -> LanguageManager.get("room_measurements", language)
        AppScreen.DIRECT_CALCULATOR -> LanguageManager.get("direct_sqft", language)
        AppScreen.CATALOGUE -> LanguageManager.get("rate_list", language)
        AppScreen.ESTIMATE_BUILDER -> LanguageManager.get("material_labour", language)
        AppScreen.PDF_QUOTATION -> LanguageManager.get("pdf_quotations", language)
        AppScreen.PAYMENTS -> LanguageManager.get("payments_balance", language)
        AppScreen.SAVED_PROJECTS -> LanguageManager.get("saved_projects", language)
        AppScreen.REPORTS -> LanguageManager.get("reports", language)
        AppScreen.SETTINGS_BACKUP -> LanguageManager.get("settings_backup", language)
        AppScreen.MATERIAL_CALCULATOR -> LanguageManager.get("material_calc", language)
        AppScreen.COLOR_PALETTE -> LanguageManager.get("color_palette", language)
        AppScreen.LABOUR_ATTENDANCE -> LanguageManager.get("labour_attendance", language)
        AppScreen.SITE_PROGRESS -> LanguageManager.get("site_progress", language)
        AppScreen.WARRANTY_CERTIFICATE -> LanguageManager.get("warranty_cert", language)
        AppScreen.WOOD_POLISH_CALCULATOR -> LanguageManager.get("woodwork_polish", language)
    }

    val screenSubtitle = when (currentScreen) {
        AppScreen.DASHBOARD -> LanguageManager.get("app_subtitle", language)
        else -> activeProjectDetails?.project?.let { "${it.customerName} • ${it.siteName}" }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppHeader(
                title = screenTitle,
                subtitle = screenSubtitle,
                showBackButton = currentScreen != AppScreen.DASHBOARD,
                onBackClick = { viewModel.navigateBack() },
                language = language,
                onToggleLanguage = { viewModel.toggleLanguage() },
                activeProject = activeProjectDetails?.project,
                allProjects = allProjects,
                onSelectProject = { projId -> viewModel.selectProject(projId) }
            )
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.DASHBOARD -> {
                    DashboardScreen(
                        language = language,
                        businessProfile = businessProfile,
                        allProjects = allProjects,
                        activeProjectDetails = activeProjectDetails,
                        onNavigate = { screen -> viewModel.navigateTo(screen) },
                        onNewEstimateClick = {
                            viewModel.createNewProject(
                                customerName = "New Client",
                                mobileNumber = "",
                                siteName = "New Site",
                                siteAddress = "Kharadi, Pune",
                                flatType = "2BHK",
                                notes = "",
                                reqs = ""
                            )
                        }
                    )
                }

                AppScreen.CUSTOMER_SITE -> {
                    CustomerSiteScreen(
                        language = language,
                        currentProject = activeProjectDetails?.project,
                        onSaveProject = { updatedProject ->
                            viewModel.updateProject(updatedProject)
                        },
                        onNavigateToRooms = {
                            viewModel.navigateTo(AppScreen.ROOM_CALCULATOR)
                        }
                    )
                }

                AppScreen.ROOM_CALCULATOR -> {
                    RoomCalculatorScreen(
                        language = language,
                        projectDetails = activeProjectDetails,
                        onAddRoom = { rName, lFt, lIn, wFt, wIn, hFt, hIn, dCount, dW, dH, winCount, winW, winH, deduct, wall, ceil, notes ->
                            viewModel.addRoom(
                                rName, lFt, lIn, wFt, wIn, hFt, hIn,
                                dCount, dW, dH, winCount, winW, winH,
                                deduct, wall, ceil, notes
                            )
                        },
                        onSaveDetailedRoom = { room -> viewModel.saveDetailedRoom(room) },
                        onUpdateRoom = { room -> viewModel.updateRoom(room) },
                        onDeleteRoom = { roomId -> viewModel.deleteRoom(roomId) },
                        onDuplicateRoom = { room -> viewModel.duplicateRoom(room) },
                        onToggleDeductOpenings = { deduct ->
                            activeProjectDetails?.project?.let {
                                viewModel.updateProject(it.copy(deductOpenings = deduct))
                            }
                        },
                        onUpdateProjectMode = { mode ->
                            activeProjectDetails?.project?.let {
                                viewModel.updateProject(it.copy(estimateMode = mode))
                            }
                        },
                        onNavigateToEstimates = { viewModel.navigateTo(AppScreen.ESTIMATE_BUILDER) }
                    )
                }

                AppScreen.DIRECT_CALCULATOR -> {
                    DirectCalculatorScreen(
                        language = language,
                        projectDetails = activeProjectDetails,
                        onAddDirect = { surface, qty, unit, notes ->
                            viewModel.addDirectMeasurement(surface, qty, unit, notes)
                        },
                        onDeleteDirect = { id -> viewModel.deleteDirectMeasurement(id) }
                    )
                }

                AppScreen.CATALOGUE -> {
                    CatalogueScreen(
                        language = language,
                        catalogueItems = catalogueItems,
                        onAddCatalogueItem = { item -> viewModel.addCatalogueItem(item) },
                        onUpdateCatalogueItem = { item -> viewModel.updateCatalogueItem(item) },
                        onDeleteCatalogueItem = { id -> viewModel.deleteCatalogueItem(id) },
                        onResetCatalogue = { viewModel.resetCatalogue() }
                    )
                }

                AppScreen.ESTIMATE_BUILDER -> {
                    EstimateBuilderScreen(
                        language = language,
                        projectDetails = activeProjectDetails,
                        catalogueItems = catalogueItems,
                        onAddItem = { item -> viewModel.addEstimateItem(item) },
                        onUpdateItem = { item -> viewModel.updateEstimateItem(item) },
                        onDeleteItem = { id -> viewModel.deleteEstimateItem(id) },
                        onUpdateProject = { proj -> viewModel.updateProject(proj) },
                        onAutoPopulate = { selectedItems -> viewModel.autoPopulateEstimateFromMeasurements(selectedItems) },
                        onSaveProductToCatalogue = { item -> viewModel.saveProductToCatalogue(item) },
                        onNavigateToPdf = { viewModel.navigateTo(AppScreen.PDF_QUOTATION) }
                    )
                }

                AppScreen.PDF_QUOTATION -> {
                    PdfQuotationScreen(
                        language = language,
                        projectDetails = activeProjectDetails,
                        businessProfile = businessProfile
                    )
                }

                AppScreen.PAYMENTS -> {
                    PaymentScreen(
                        language = language,
                        projectDetails = activeProjectDetails,
                        businessProfile = businessProfile,
                        onAddPayment = { amt, date, method, ref, remarks ->
                            viewModel.addPayment(amt, date, method, ref, remarks)
                        },
                        onDeletePayment = { id -> viewModel.deletePayment(id) }
                    )
                }

                AppScreen.SAVED_PROJECTS -> {
                    SavedProjectsScreen(
                        language = language,
                        projects = allProjects,
                        selectedProjectId = activeProjectDetails?.project?.id,
                        onSelectProject = { id -> viewModel.selectProject(id) },
                        onDuplicateProject = { id -> viewModel.duplicateProject(id) },
                        onDeleteProject = { id -> viewModel.deleteProject(id) },
                        onUpdateProjectStatus = { id, st -> viewModel.updateProjectStatus(id, st) },
                        onNewProjectClick = {
                            viewModel.createNewProject(
                                customerName = "New Client",
                                mobileNumber = "",
                                siteName = "New Site",
                                siteAddress = "Kharadi, Pune",
                                flatType = "2BHK",
                                notes = "",
                                reqs = ""
                            )
                        }
                    )
                }

                AppScreen.REPORTS -> {
                    ReportsScreen(
                        language = language,
                        allProjects = allProjects,
                        allPayments = allPayments
                    )
                }

                AppScreen.SETTINGS_BACKUP -> {
                    SettingsBackupScreen(
                        language = language,
                        businessProfile = businessProfile,
                        allProjects = allProjects,
                        activeProjectDetails = activeProjectDetails,
                        allCatalogueItems = catalogueItems,
                        allPayments = allPayments,
                        onUpdateBusinessProfile = { updated -> viewModel.updateBusinessProfile(updated) },
                        onResetBusinessProfile = { viewModel.resetBusinessProfile() },
                        onResetCatalogue = { viewModel.resetCatalogue() }
                    )
                }

                AppScreen.MATERIAL_CALCULATOR -> {
                    MaterialCalculatorScreen(
                        language = language,
                        projectDetails = activeProjectDetails
                    )
                }

                AppScreen.COLOR_PALETTE -> {
                    ColorPaletteScreen(
                        language = language,
                        projectDetails = activeProjectDetails
                    )
                }

                AppScreen.LABOUR_ATTENDANCE -> {
                    LabourAttendanceScreen(
                        language = language,
                        projectDetails = activeProjectDetails,
                        workers = allWorkers,
                        attendanceRecords = projectAttendance,
                        onAddWorker = { name, role, rate, mob ->
                            viewModel.addWorker(name, role, rate, mob)
                        },
                        onRecordAttendance = { wId, dt, st, ot, adv, nts ->
                            viewModel.recordAttendance(wId, dt, st, ot, adv, nts)
                        },
                        onDeleteAttendance = { id -> viewModel.deleteAttendance(id) }
                    )
                }

                AppScreen.SITE_PROGRESS -> {
                    SiteProgressScreen(
                        language = language,
                        projectDetails = activeProjectDetails,
                        currentProgress = siteProgress,
                        onSaveProgress = { progress -> viewModel.updateSiteProgress(progress) }
                    )
                }

                AppScreen.WARRANTY_CERTIFICATE -> {
                    WarrantyCertificateScreen(
                        language = language,
                        projectDetails = activeProjectDetails,
                        businessProfile = businessProfile
                    )
                }

                AppScreen.WOOD_POLISH_CALCULATOR -> {
                    WoodPolishCalculatorScreen(
                        language = language,
                        projectDetails = activeProjectDetails,
                        businessProfile = businessProfile,
                        onSaveItem = { item -> viewModel.saveWoodPolishItem(item) },
                        onDeleteItem = { item -> viewModel.deleteWoodPolishItem(item) },
                        onDuplicateItem = { item -> viewModel.duplicateWoodPolishItem(item) }
                    )
                }
            }
        }
    }
}
