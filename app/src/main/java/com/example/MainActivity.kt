package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.AccessibilityTopBar
import com.example.ui.screens.AacScreen
import com.example.ui.screens.ClinicsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MedicalPassportScreen
import com.example.ui.screens.MedicationsScreen
import com.example.ui.screens.PresentationModeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.AccessCareTheme

enum class Screen(val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    PASSPORT("Passport", Icons.Filled.Badge, Icons.Outlined.Badge),
    AAC("AAC Speech", Icons.Filled.RecordVoiceOver, Icons.Outlined.RecordVoiceOver),
    CLINICS("Clinics", Icons.Filled.LocalHospital, Icons.Outlined.LocalHospital),
    MEDS("Meds", Icons.Filled.Medication, Icons.Outlined.Medication),
    SETTINGS("Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val highContrastMode by viewModel.highContrastMode.collectAsState()
            val fontScale by viewModel.fontScale.collectAsState()

            AccessCareTheme(highContrastMode = highContrastMode) {
                val currentDensity = LocalDensity.current
                val customDensity = Density(
                    density = currentDensity.density,
                    fontScale = currentDensity.fontScale * fontScale
                )

                CompositionLocalProvider(LocalDensity provides customDensity) {
                    AccessCareApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AccessCareApp(viewModel: MainViewModel) {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    val passport by viewModel.passport.collectAsState()
    val clinics by viewModel.filteredClinics.collectAsState()
    val allClinics by viewModel.allClinics.collectAsState()
    val clinicFilter by viewModel.clinicFilter.collectAsState()
    val aacPhrases by viewModel.filteredAacPhrases.collectAsState()
    val aacCategory by viewModel.aacCategory.collectAsState()
    val medications by viewModel.medications.collectAsState()

    val painLevel by viewModel.painLevel.collectAsState()
    val painLocation by viewModel.painLocation.collectAsState()
    val painQuality by viewModel.painQuality.collectAsState()

    val fontScale by viewModel.fontScale.collectAsState()
    val highContrastMode by viewModel.highContrastMode.collectAsState()
    val hapticEnabled by viewModel.hapticEnabled.collectAsState()
    val isPresentationMode by viewModel.isPresentationMode.collectAsState()

    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val currentPhrase by viewModel.currentPhrase.collectAsState()

    // Presentation Mode (SOS full-screen view) takes precedence
    if (isPresentationMode) {
        PresentationModeScreen(
            passport = passport,
            onClose = { viewModel.togglePresentationMode() },
            onSpeakPassport = {
                val speech = "Emergency Medical Identity. Patient: ${passport.fullName}. Blood Type: ${passport.bloodType}. Primary Condition: ${passport.primaryDiagnosis}. Critical Allergies: ${passport.allergies}. Accessibility Accommodations: ${passport.accommodations.joinToString(", ")}. ${passport.clinicalCommunicationNotes}"
                viewModel.speakText(speech, alertHaptic = true)
            }
        )
        return
    }

    // Secondary Screen back navigation returns to Home
    if (currentScreen != Screen.HOME) {
        BackHandler {
            currentScreen = Screen.HOME
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AccessibilityTopBar(
                currentFontScale = fontScale,
                highContrastMode = highContrastMode,
                isSpeaking = isSpeaking,
                currentSpokenText = currentPhrase,
                onFontScaleCycle = {
                    val nextScale = when (fontScale) {
                        1.0f -> 1.2f
                        1.2f -> 1.4f
                        else -> 1.0f
                    }
                    viewModel.setFontScale(nextScale)
                },
                onToggleHighContrast = { viewModel.toggleHighContrast() },
                onLaunchPresentationMode = { viewModel.togglePresentationMode() },
                onStopSpeaking = { viewModel.stopSpeaking() }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_navigation_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Screen.values().forEach { screen ->
                    val selected = currentScreen == screen
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            currentScreen = screen
                            if (hapticEnabled) viewModel.hapticHelper.clickTap()
                        },
                        icon = {
                            Icon(
                                imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 10.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.HOME -> HomeScreen(
                    passport = passport,
                    medications = medications,
                    clinics = allClinics,
                    painLevel = painLevel,
                    painLocation = painLocation,
                    painQuality = painQuality,
                    onPainLevelChanged = { viewModel.setPainLevel(it) },
                    onPainLocationChanged = { viewModel.setPainLocation(it) },
                    onPainQualityChanged = { viewModel.setPainQuality(it) },
                    onSpeakPain = { viewModel.speakPainAssessment() },
                    onSpeakQuickPhrase = { viewModel.speakText(it) },
                    onToggleMedication = { viewModel.toggleMedicationTaken(it) },
                    onOpenPassport = { currentScreen = Screen.PASSPORT },
                    onOpenClinics = { currentScreen = Screen.CLINICS },
                    onOpenAac = { currentScreen = Screen.AAC },
                    onOpenMeds = { currentScreen = Screen.MEDS },
                    onLaunchPresentationMode = { viewModel.togglePresentationMode() },
                    highContrastMode = highContrastMode
                )

                Screen.PASSPORT -> MedicalPassportScreen(
                    passport = passport,
                    onUpdatePassport = { viewModel.updatePassport(it) },
                    onSpeakSummary = {
                        val speech = "Clinical Communication Notes for ${passport.fullName}: ${passport.clinicalCommunicationNotes}"
                        viewModel.speakText(speech)
                    },
                    onLaunchPresentationMode = { viewModel.togglePresentationMode() },
                    highContrastMode = highContrastMode
                )

                Screen.AAC -> AacScreen(
                    phrases = aacPhrases,
                    selectedCategory = aacCategory,
                    onCategorySelected = { viewModel.setAacCategory(it) },
                    currentSpokenPhrase = currentPhrase,
                    isSpeaking = isSpeaking,
                    onSpeakPhrase = { viewModel.speakText(it.spokenText, alertHaptic = it.category == "Urgent") },
                    onSpeakCustomText = { viewModel.speakText(it) },
                    onSaveCustomPhrase = { label, text -> viewModel.addCustomAacPhrase(label, text) },
                    onDeletePhrase = { viewModel.deleteAacPhrase(it) },
                    painLevel = painLevel,
                    painLocation = painLocation,
                    painQuality = painQuality,
                    onPainLevelChanged = { viewModel.setPainLevel(it) },
                    onPainLocationChanged = { viewModel.setPainLocation(it) },
                    onPainQualityChanged = { viewModel.setPainQuality(it) },
                    onSpeakPain = { viewModel.speakPainAssessment() },
                    highContrastMode = highContrastMode
                )

                Screen.CLINICS -> ClinicsScreen(
                    clinics = clinics,
                    selectedFilter = clinicFilter,
                    onFilterSelected = { viewModel.setClinicFilter(it) },
                    highContrastMode = highContrastMode
                )

                Screen.MEDS -> MedicationsScreen(
                    medications = medications,
                    onToggleTaken = { viewModel.toggleMedicationTaken(it) },
                    onAddMedication = { viewModel.addMedication(it) },
                    onDeleteMedication = { viewModel.deleteMedication(it) },
                    onSpeakInstructions = { viewModel.speakText(it) },
                    highContrastMode = highContrastMode
                )

                Screen.SETTINGS -> SettingsScreen(
                    currentFontScale = fontScale,
                    onFontScaleSelected = { viewModel.setFontScale(it) },
                    highContrastMode = highContrastMode,
                    onToggleHighContrast = { viewModel.toggleHighContrast() },
                    hapticEnabled = hapticEnabled,
                    onToggleHaptic = { viewModel.toggleHaptic() },
                    ttsHelper = viewModel.ttsHelper,
                    onResetData = { viewModel.resetData() }
                )
            }
        }
    }
}
