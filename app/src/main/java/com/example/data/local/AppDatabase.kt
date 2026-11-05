package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AacPhrase
import com.example.data.model.AccessibleClinic
import com.example.data.model.Converters
import com.example.data.model.MedicalPassport
import com.example.data.model.MedicationItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Dao
interface PassportDao {
    @Query("SELECT * FROM medical_passport WHERE id = 1 LIMIT 1")
    fun getPassport(): Flow<MedicalPassport?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePassport(passport: MedicalPassport)
}

@Dao
interface ClinicDao {
    @Query("SELECT * FROM accessible_clinics ORDER BY distanceMiles ASC")
    fun getAllClinics(): Flow<List<AccessibleClinic>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(clinics: List<AccessibleClinic>)
}

@Dao
interface AacDao {
    @Query("SELECT * FROM aac_phrases ORDER BY id ASC")
    fun getAllPhrases(): Flow<List<AacPhrase>>

    @Query("SELECT * FROM aac_phrases WHERE category = :category ORDER BY id ASC")
    fun getPhrasesByCategory(category: String): Flow<List<AacPhrase>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(phrase: AacPhrase): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(phrases: List<AacPhrase>)

    @Delete
    suspend fun delete(phrase: AacPhrase)
}

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medications ORDER BY id ASC")
    fun getAllMedications(): Flow<List<MedicationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: MedicationItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MedicationItem>)

    @Update
    suspend fun update(item: MedicationItem)

    @Delete
    suspend fun delete(item: MedicationItem)

    @Query("UPDATE medications SET isTaken = :isTaken, lastTakenTimestamp = :timestamp WHERE id = :id")
    suspend fun setTakenStatus(id: Long, isTaken: Boolean, timestamp: Long)
}

@Database(
    entities = [
        MedicalPassport::class,
        AccessibleClinic::class,
        AacPhrase::class,
        MedicationItem::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun passportDao(): PassportDao
    abstract fun clinicDao(): ClinicDao
    abstract fun aacDao(): AacDao
    abstract fun medicationDao(): MedicationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "access_care_db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                INSTANCE?.let { database ->
                    populateInitialData(database)
                }
            }
        }
    }
}

suspend fun populateInitialData(db: AppDatabase) {
    // 1. Initial Passport
    db.passportDao().insertOrUpdatePassport(MedicalPassport())

    // 2. Realistic Accessible Clinics with detailed accommodation tags
    val initialClinics = listOf(
        AccessibleClinic(
            name = "Metro Inclusive Health Center",
            facilityType = "Community Health Center",
            address = "742 Beacon Ave, Suite 100",
            phone = "(555) 349-2000",
            distanceMiles = 0.8,
            rating = 4.9,
            openHours = "Mon-Sat: 7:00 AM - 8:00 PM",
            hasWheelchairRamp = true,
            hasAslInterpreter = true,
            hasLowSensoryRoom = true,
            hasBrailleSignage = true,
            hasHearingLoop = true,
            hasAccessibleExamTable = true,
            hasBariatricEquipment = true,
            hasAccessibleRestroom = true,
            hasServiceAnimalReliefArea = true,
            notes = "Full accessibility campus with zero-threshold sliding glass doors, motorized hydraulic exam tables, on-site ASL coordinators, and dimmable quiet rooms."
        ),
        AccessibleClinic(
            name = "Harmony Sensory & Pediatric Care",
            facilityType = "Sensory-Friendly Clinic",
            address = "120 Elmwood Blvd, Bldg B",
            phone = "(555) 892-1144",
            distanceMiles = 1.6,
            rating = 4.8,
            openHours = "Mon-Fri: 8:00 AM - 6:00 PM",
            hasWheelchairRamp = true,
            hasAslInterpreter = false,
            hasLowSensoryRoom = true,
            hasBrailleSignage = true,
            hasHearingLoop = false,
            hasAccessibleExamTable = true,
            hasBariatricEquipment = false,
            hasAccessibleRestroom = true,
            hasServiceAnimalReliefArea = true,
            notes = "Designed specifically for neurodivergent and sensory-sensitive individuals. Soundproof rooms, weighted blankets, noise-canceling headsets, and soft lighting available."
        ),
        AccessibleClinic(
            name = "St. Jude Regional Hospital & Trauma Center",
            facilityType = "General Hospital & ER",
            address = "400 Medical Center Parkway",
            phone = "(555) 911-0400",
            distanceMiles = 2.4,
            rating = 4.7,
            openHours = "24 / 7 Emergency Care",
            hasWheelchairRamp = true,
            hasAslInterpreter = true,
            hasLowSensoryRoom = true,
            hasBrailleSignage = true,
            hasHearingLoop = true,
            hasAccessibleExamTable = true,
            hasBariatricEquipment = true,
            hasAccessibleRestroom = true,
            hasServiceAnimalReliefArea = true,
            notes = "Certified Universal Access Hospital. Level 1 Trauma Center with 24/7 tele-interpreter tablets in 40+ sign languages, bariatric transfer lifts, and accessible parking."
        ),
        AccessibleClinic(
            name = "Valley Vision & Hearing Specialty Institute",
            facilityType = "Sensory Specialty Care",
            address = "889 Grandview Terrace",
            phone = "(555) 604-3320",
            distanceMiles = 3.7,
            rating = 4.9,
            openHours = "Mon-Fri: 8:30 AM - 5:00 PM",
            hasWheelchairRamp = true,
            hasAslInterpreter = true,
            hasLowSensoryRoom = false,
            hasBrailleSignage = true,
            hasHearingLoop = true,
            hasAccessibleExamTable = true,
            hasBariatricEquipment = false,
            hasAccessibleRestroom = true,
            hasServiceAnimalReliefArea = false,
            notes = "Equipped with wide-area assistive listening telecoils, high-contrast digital wayfinding kiosks, and tactile floor pathways."
        ),
        AccessibleClinic(
            name = "Apex Mobility Physical Therapy & Rehab",
            facilityType = "Specialty Rehabilitation",
            address = "315 River Road, Suite 4",
            phone = "(555) 774-8899",
            distanceMiles = 4.2,
            rating = 4.8,
            openHours = "Mon-Thu: 7:30 AM - 6:30 PM",
            hasWheelchairRamp = true,
            hasAslInterpreter = false,
            hasLowSensoryRoom = false,
            hasBrailleSignage = true,
            hasHearingLoop = false,
            hasAccessibleExamTable = true,
            hasBariatricEquipment = true,
            hasAccessibleRestroom = true,
            hasServiceAnimalReliefArea = true,
            notes = "100% barrier-free facility with ceiling track hoists, roll-in hydrotherapy pools, and extra-wide ADA exam suites."
        )
    )
    db.clinicDao().insertAll(initialClinics)

    // 3. Clinical AAC & Speech Communication Cards
    val initialPhrases = listOf(
        // Urgent
        AacPhrase(category = "Urgent", label = "Trouble Breathing", spokenText = "I am having trouble breathing. Please assist me immediately.", iconType = "breathe"),
        AacPhrase(category = "Urgent", label = "Severe Pain", spokenText = "I am experiencing severe pain right now.", iconType = "pain"),
        AacPhrase(category = "Urgent", label = "Feeling Faint / Dizzy", spokenText = "I feel very dizzy and might faint. Please let me lie down safely.", iconType = "faint"),
        AacPhrase(category = "Urgent", label = "Call My Companion", spokenText = "Please contact my designated emergency support companion immediately.", iconType = "family"),
        AacPhrase(category = "Urgent", label = "Allergic Reaction", spokenText = "I feel like I am having an allergic reaction. Please check my allergy list.", iconType = "help"),

        // Needs & Comfort
        AacPhrase(category = "Needs", label = "Need Water", spokenText = "May I please have some water or ice chips?", iconType = "water"),
        AacPhrase(category = "Needs", label = "Restroom Assist", spokenText = "I need assistance transferring to the accessible restroom.", iconType = "restroom"),
        AacPhrase(category = "Needs", label = "Too Cold / Warm", spokenText = "I am feeling very cold. Could I please have an extra warm blanket?", iconType = "cold"),
        AacPhrase(category = "Needs", label = "Feeling Nauseous", spokenText = "I am feeling very nauseous and need an emesis basin or nausea medicine.", iconType = "nausea"),
        AacPhrase(category = "Needs", label = "Reposition Bed", spokenText = "Could you please help me adjust and reposition my bed angle?", iconType = "bed"),
        AacPhrase(category = "Needs", label = "Dim the Lights", spokenText = "The room lighting is overwhelming. Please dim the lights.", iconType = "lights"),

        // Doctor / Clinician Interaction
        AacPhrase(category = "Questions", label = "What Is This Medication?", spokenText = "What medication are you giving me, and what are its potential side effects?", iconType = "medicine"),
        AacPhrase(category = "Questions", label = "Explain Simply", spokenText = "Could you please explain that diagnosis and procedure in simpler terms?", iconType = "explain"),
        AacPhrase(category = "Questions", label = "Please Write It Down", spokenText = "Please write that down for me so I can read and remember it accurately.", iconType = "write"),
        AacPhrase(category = "Questions", label = "How Long Will It Take?", spokenText = "How long is this test or procedure expected to take?", iconType = "time"),
        AacPhrase(category = "Questions", label = "Where Is My Companion?", spokenText = "Where is my support companion? I need them to be in the room with me.", iconType = "companion"),
        AacPhrase(category = "Questions", label = "Can I Have A Moment?", spokenText = "Please give me a moment to process before we continue.", iconType = "custom")
    )
    db.aacDao().insertAll(initialPhrases)

    // 4. Initial Medications
    val initialMeds = listOf(
        MedicationItem(
            name = "Baclofen",
            dosage = "10 mg",
            timeOfDay = "Morning",
            scheduledTime = "08:00 AM",
            instructions = "Take with food or milk to prevent stomach upset. Muscle spasm management.",
            pillColor = "Teal",
            pillShape = "Oval",
            isTaken = true,
            lastTakenTimestamp = System.currentTimeMillis() - 3600000
        ),
        MedicationItem(
            name = "Gabapentin",
            dosage = "300 mg",
            timeOfDay = "Afternoon",
            scheduledTime = "01:00 PM",
            instructions = "Do not crush. Take with a full glass of water. Neuropathic nerve relief.",
            pillColor = "Yellow",
            pillShape = "Capsule",
            isTaken = false
        ),
        MedicationItem(
            name = "Vitamin D3 + Calcium",
            dosage = "1000 IU",
            timeOfDay = "Morning",
            scheduledTime = "08:30 AM",
            instructions = "Supports bone density and mobility health. Take with breakfast.",
            pillColor = "Amber",
            pillShape = "Round",
            isTaken = true,
            lastTakenTimestamp = System.currentTimeMillis() - 3200000
        ),
        MedicationItem(
            name = "Lisinopril",
            dosage = "10 mg",
            timeOfDay = "Evening",
            scheduledTime = "08:00 PM",
            instructions = "Blood pressure regulation. Check blood pressure if feeling lightheaded.",
            pillColor = "White",
            pillShape = "Round",
            isTaken = false
        )
    )
    db.medicationDao().insertAll(initialMeds)
}
