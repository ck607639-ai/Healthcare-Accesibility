package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.populateInitialData
import com.example.data.model.AacPhrase
import com.example.data.model.AccessibleClinic
import com.example.data.model.MedicalPassport
import com.example.data.model.MedicationItem
import com.example.util.HapticHelper
import com.example.util.TextToSpeechHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val passportDao = db.passportDao()
    private val clinicDao = db.clinicDao()
    private val aacDao = db.aacDao()
    private val medicationDao = db.medicationDao()

    val ttsHelper = TextToSpeechHelper(application)
    val hapticHelper = HapticHelper(application)

    // Data Flows from Room
    val passport: StateFlow<MedicalPassport> = passportDao.getPassport()
        .map { it ?: MedicalPassport() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            MedicalPassport()
        )

    val allClinics: StateFlow<List<AccessibleClinic>> = clinicDao.getAllClinics()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val allAacPhrases: StateFlow<List<AacPhrase>> = aacDao.getAllPhrases()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val medications: StateFlow<List<MedicationItem>> = medicationDao.getAllMedications()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // Filtering State
    private val _clinicFilter = MutableStateFlow("All")
    val clinicFilter: StateFlow<String> = _clinicFilter.asStateFlow()

    val filteredClinics: StateFlow<List<AccessibleClinic>> = combine(allClinics, _clinicFilter) { list, filter ->
        when (filter) {
            "Wheelchair Ramp" -> list.filter { it.hasWheelchairRamp }
            "ASL Interpreter" -> list.filter { it.hasAslInterpreter }
            "Low-Sensory Room" -> list.filter { it.hasLowSensoryRoom }
            "Braille / Tactile" -> list.filter { it.hasBrailleSignage }
            "Hearing Loop" -> list.filter { it.hasHearingLoop }
            "Accessible Exam Table" -> list.filter { it.hasAccessibleExamTable }
            "Bariatric Equipped" -> list.filter { it.hasBariatricEquipment }
            else -> list
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _aacCategory = MutableStateFlow("All")
    val aacCategory: StateFlow<String> = _aacCategory.asStateFlow()

    val filteredAacPhrases: StateFlow<List<AacPhrase>> = combine(allAacPhrases, _aacCategory) { list, cat ->
        if (cat == "All") list else list.filter { it.category == cat }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pain Assessment Scale
    private val _painLevel = MutableStateFlow(4)
    val painLevel: StateFlow<Int> = _painLevel.asStateFlow()

    private val _painLocation = MutableStateFlow("Lower Back & Joint")
    val painLocation: StateFlow<String> = _painLocation.asStateFlow()

    private val _painQuality = MutableStateFlow("Throbbing & Aching")
    val painQuality: StateFlow<String> = _painQuality.asStateFlow()

    // Accessibility Configuration State
    private val _fontScale = MutableStateFlow(1.0f)
    val fontScale: StateFlow<Float> = _fontScale.asStateFlow()

    private val _highContrastMode = MutableStateFlow(false)
    val highContrastMode: StateFlow<Boolean> = _highContrastMode.asStateFlow()

    private val _hapticEnabled = MutableStateFlow(true)
    val hapticEnabled: StateFlow<Boolean> = _hapticEnabled.asStateFlow()

    private val _isPresentationMode = MutableStateFlow(false)
    val isPresentationMode: StateFlow<Boolean> = _isPresentationMode.asStateFlow()

    val isSpeaking = ttsHelper.isSpeaking
    val currentPhrase = ttsHelper.currentPhrase

    init {
        // Ensure database is populated in case of initial run
        viewModelScope.launch(Dispatchers.IO) {
            val existing = passportDao.getPassport()
            // triggers lazy init or callback
        }
    }

    fun speakText(text: String, alertHaptic: Boolean = false) {
        if (_hapticEnabled.value) {
            if (alertHaptic) hapticHelper.alertPulse() else hapticHelper.clickTap()
        }
        ttsHelper.speak(text)
    }

    fun stopSpeaking() {
        ttsHelper.stop()
    }

    fun setClinicFilter(filter: String) {
        _clinicFilter.value = filter
        if (_hapticEnabled.value) hapticHelper.clickTap()
    }

    fun setAacCategory(category: String) {
        _aacCategory.value = category
        if (_hapticEnabled.value) hapticHelper.clickTap()
    }

    fun setPainLevel(level: Int) {
        _painLevel.value = level
        if (_hapticEnabled.value) hapticHelper.clickTap()
    }

    fun setPainLocation(location: String) {
        _painLocation.value = location
    }

    fun setPainQuality(quality: String) {
        _painQuality.value = quality
    }

    fun speakPainAssessment() {
        val level = _painLevel.value
        val loc = _painLocation.value
        val qual = _painQuality.value
        val descriptor = when (level) {
            0 -> "Zero, no hurt at all"
            in 1..2 -> "Level $level, mild hurt"
            in 3..4 -> "Level $level, moderate, aching hurt"
            in 5..6 -> "Level $level, severe and interfering hurt"
            in 7..8 -> "Level $level, very severe, excruciating hurt"
            else -> "Level 10, worst possible unbearable hurt"
        }
        val speech = "My reported pain level is $descriptor. The pain is located in my $loc, and feels $qual. Please evaluate my treatment options."
        speakText(speech, alertHaptic = level >= 7)
    }

    fun toggleMedicationTaken(item: MedicationItem) {
        if (_hapticEnabled.value) hapticHelper.clickTap()
        viewModelScope.launch(Dispatchers.IO) {
            val newStatus = !item.isTaken
            val timestamp = if (newStatus) System.currentTimeMillis() else 0L
            medicationDao.setTakenStatus(item.id, newStatus, timestamp)
        }
    }

    fun addMedication(item: MedicationItem) {
        viewModelScope.launch(Dispatchers.IO) {
            medicationDao.insert(item)
        }
    }

    fun deleteMedication(item: MedicationItem) {
        viewModelScope.launch(Dispatchers.IO) {
            medicationDao.delete(item)
        }
    }

    fun addCustomAacPhrase(label: String, spokenText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            aacDao.insert(
                AacPhrase(
                    category = "Custom",
                    label = label,
                    spokenText = spokenText,
                    iconType = "custom",
                    isCustom = true
                )
            )
        }
    }

    fun deleteAacPhrase(phrase: AacPhrase) {
        viewModelScope.launch(Dispatchers.IO) {
            aacDao.delete(phrase)
        }
    }

    fun updatePassport(updated: MedicalPassport) {
        viewModelScope.launch(Dispatchers.IO) {
            passportDao.insertOrUpdatePassport(updated)
        }
    }

    fun setFontScale(scale: Float) {
        _fontScale.value = scale
        if (_hapticEnabled.value) hapticHelper.clickTap()
    }

    fun toggleHighContrast() {
        _highContrastMode.value = !_highContrastMode.value
        if (_hapticEnabled.value) hapticHelper.clickTap()
    }

    fun toggleHaptic() {
        _hapticEnabled.value = !_hapticEnabled.value
    }

    fun togglePresentationMode() {
        _isPresentationMode.value = !_isPresentationMode.value
        if (_hapticEnabled.value) hapticHelper.alertPulse()
    }

    fun resetData() {
        viewModelScope.launch(Dispatchers.IO) {
            populateInitialData(db)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsHelper.shutdown()
    }
}
