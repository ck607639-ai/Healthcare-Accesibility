package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString("||")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        if (value.isBlank()) return emptyList()
        return value.split("||").filter { it.isNotBlank() }
    }
}

@Entity(tableName = "medical_passport")
@TypeConverters(Converters::class)
data class MedicalPassport(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "Jordan Taylor",
    val dateOfBirth: String = "1994-08-14",
    val bloodType: String = "O+",
    val primaryDiagnosis: String = "Spinal Cord Injury (T6) & Sensory Sensitivity",
    val emergencyContactName: String = "Morgan Taylor",
    val emergencyContactPhone: String = "+1 (555) 234-5678",
    val emergencyContactRelation: String = "Spouse",
    val primaryPhysician: String = "Dr. Elena Rostova, MD (Metro Inclusive Health)",
    val allergies: String = "Penicillin (Anaphylaxis), Latex (Contact Dermatitis)",
    val accommodations: List<String> = listOf(
        "Power Wheelchair User",
        "Sensory Sensitive / Autism Spectrum",
        "Needs Low-Sensory Exam Room",
        "Explain Procedures Before Touching",
        "Accessible Exam Table Required"
    ),
    val clinicalCommunicationNotes: String = "Patient is non-verbal during high-pain flares; utilizes AAC speech app or written notes. Please allow companion Morgan in room at all times. Do not rush questions.",
    val isOrganDonor: Boolean = true,
    val hasDnrOrder: Boolean = false
)

@Entity(tableName = "accessible_clinics")
data class AccessibleClinic(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val facilityType: String,
    val address: String,
    val phone: String,
    val distanceMiles: Double,
    val rating: Double,
    val openHours: String,
    val hasWheelchairRamp: Boolean = true,
    val hasAslInterpreter: Boolean = false,
    val hasLowSensoryRoom: Boolean = false,
    val hasBrailleSignage: Boolean = false,
    val hasHearingLoop: Boolean = false,
    val hasAccessibleExamTable: Boolean = false,
    val hasBariatricEquipment: Boolean = false,
    val hasAccessibleRestroom: Boolean = true,
    val hasServiceAnimalReliefArea: Boolean = false,
    val notes: String = ""
)

@Entity(tableName = "aac_phrases")
data class AacPhrase(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String, // "Urgent", "Needs", "Symptoms", "Questions", "Custom"
    val label: String,
    val spokenText: String,
    val iconType: String,
    val isCustom: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "medications")
data class MedicationItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val dosage: String,
    val timeOfDay: String, // "Morning", "Afternoon", "Evening", "Bedtime", "As Needed"
    val scheduledTime: String, // e.g. "08:00 AM"
    val instructions: String,
    val pillColor: String, // "Teal", "Blue", "White", "Amber", "Red"
    val pillShape: String, // "Capsule", "Round", "Oval", "Liquid"
    val isTaken: Boolean = false,
    val lastTakenTimestamp: Long = 0L
)
