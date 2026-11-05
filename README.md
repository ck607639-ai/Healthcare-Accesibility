# AccessCare - Healthcare Accessibility Android Application

AccessCare is an Android application built with **Jetpack Compose**, **Material 3**, and **Room Database**, engineered to eliminate communicative, sensory, and physical barriers during clinical consultations and emergency medical care.

---

## 🌟 Key Features

### 1. 📋 Accessible Medical Passport & Emergency Medical ID
- **Comprehensive Medical Identification**: Full name, Date of Birth, Blood Type, Primary Diagnosis, and critical allergies banner.
- **Clinical Accommodations Checklist**: Standardized accessibility requirements (Power Wheelchair, ASL Interpreter, Quiet / Low-Sensory Exam Room, Accessible Exam Table, Service Animal).
- **Clinical Communication Preferences**: Direct guidance for doctors and nurses on how to communicate with the patient.
- **Key Contacts**: Direct dial buttons for designated emergency advocates and primary physicians.
- **Clinician Presentation Mode**: A high-contrast (OLED black, vivid amber, and pure white) full-screen flashcard that can be handed directly to triage nurses or emergency responders, with voice readout.

### 2. 🗣️ Clinical AAC & Speech Communication Board
- **Native Android Text-to-Speech (TTS)**: Instant voice synthesis for non-verbal, stroke, or high-pain patients.
- **Wong-Baker FACES Pain Scale (0–10)**: Custom vector visual facial indicators, pain location chips, and sensation descriptors with a one-touch *"Announce Pain Report to Clinician"* button.
- **Categorized Clinical Speech Cards**:
  - **Urgent**: Trouble Breathing, Severe Pain, Feeling Faint, Call Companion, Allergic Reaction.
  - **Needs & Comfort**: Water/Ice, Restroom Transfer Assistance, Temperature/Blanket, Nausea, Bed Adjustment, Dim Lighting.
  - **Doctor / Clinician Questions**: Medication identification, simplified explanations, written notes request, procedure duration.
- **Custom Speech Composer**: Type any sentence and announce it immediately or save it as a recurring quick card.

### 3. 🏥 Accessible Healthcare Provider Directory
- Filter hospitals and community clinics by verified accessibility accommodations:
  - Wheelchair Ramps & Zero-Threshold Entries
  - Certified On-Site ASL Interpreters
  - Low-Sensory / Quiet Exam Rooms
  - Tactile & Braille Signage
  - Assistive Listening Telecoil Hearing Loops
  - Motorized Hydraulic Exam Tables & Bariatric Transfer Lifts
- Direct call (`ACTION_DIAL`), map directions (`ACTION_VIEW`), and a copyable **Pre-Visit Accommodation Request Notice** compliant with ADA Title III and Section 504.

### 4. 💊 Accessible Medication Schedule
- Grouped dosage routines (Morning, Afternoon, Evening, Bedtime).
- Visual shape and color pill identifiers (Teal Oval, Yellow Capsule, Amber Round) designed for low-vision patients.
- **Audio Instructions Readout**: Speaks dosages, directions, and warnings aloud.
- One-tap adherence logging with daily progress indicator.

### 5. ♿ Universal Accessibility Ergonomics
- **Text Size Scaler**: Toggle between Standard (100%), Large (120%), and Extra Large (140%).
- **High-Contrast Theme**: WCAG AAA compliant pitch-black palette with high-contrast borders and text.
- **Tactile Haptic Feedback**: Haptic pulses for non-visual tactile interaction.
- **Patient Rights Reference**: ADA Title III and Section 504 Rehabilitation Act healthcare rights summary.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.0+
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Persistence**: Room Database (SQLite with Flow)
- **Audio / Speech**: Android `android.speech.tts.TextToSpeech`
- **Haptics**: Android `Vibrator` / `VibratorManager`
- **Build System**: Gradle Kotlin DSL (.gradle.kts)
- **Target SDK**: Android 14+ (API 34 / 36)

---

## 🚀 Building & Running

1. Clone this repository.
2. Open the project in **Android Studio Hedgehog / Ladybug** or newer.
3. Allow Gradle to sync dependencies.
4. Run on an Android device or emulator with API 24+.
