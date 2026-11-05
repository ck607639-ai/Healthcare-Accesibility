package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AacPhrase
import com.example.ui.components.AacCard
import com.example.ui.components.PainScaleVisualizer

@Composable
fun AacScreen(
    phrases: List<AacPhrase>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    currentSpokenPhrase: String,
    isSpeaking: Boolean,
    onSpeakPhrase: (AacPhrase) -> Unit,
    onSpeakCustomText: (String) -> Unit,
    onSaveCustomPhrase: (String, String) -> Unit,
    onDeletePhrase: (AacPhrase) -> Unit,
    painLevel: Int,
    painLocation: String,
    painQuality: String,
    onPainLevelChanged: (Int) -> Unit,
    onPainLocationChanged: (String) -> Unit,
    onPainQualityChanged: (String) -> Unit,
    onSpeakPain: () -> Unit,
    highContrastMode: Boolean,
    modifier: Modifier = Modifier
) {
    val categories = listOf("All", "Urgent", "Needs", "Questions", "Custom")
    var customInputText by remember { mutableStateOf("") }
    var customLabelText by remember { mutableStateOf("") }
    var showCustomComposer by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("aac_screen")
    ) {
        // Screen Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Clinical AAC & Speech Board",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "High-contrast communicative cards & voice synthesis",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Custom Phrase Quick Speak Bar
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth().testTag("custom_speech_card")
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                OutlinedTextField(
                    value = customInputText,
                    onValueChange = { customInputText = it },
                    placeholder = { Text("Type any message to speak aloud...") },
                    modifier = Modifier.fillMaxWidth().testTag("custom_phrase_input"),
                    shape = RoundedCornerShape(10.dp),
                    trailingIcon = {
                        if (customInputText.isNotBlank()) {
                            IconButton(onClick = {
                                onSpeakCustomText(customInputText)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Speak now",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (customInputText.isNotBlank()) {
                                onSpeakCustomText(customInputText)
                            }
                        },
                        enabled = customInputText.isNotBlank(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(42.dp).testTag("speak_custom_btn")
                    ) {
                        Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Speak Aloud", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (customInputText.isNotBlank()) {
                                val label = customInputText.take(20)
                                onSaveCustomPhrase(label, customInputText)
                                customInputText = ""
                            }
                        },
                        enabled = customInputText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(42.dp)
                    ) {
                        Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { cat ->
                val isSelected = cat == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelected(cat) },
                    label = { Text(cat, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.testTag("aac_category_$cat")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Phrases List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Optional Wong-Baker Pain Scale card if viewing All or Urgent
            if (selectedCategory == "All" || selectedCategory == "Urgent") {
                item {
                    PainScaleVisualizer(
                        currentLevel = painLevel,
                        currentLocation = painLocation,
                        currentQuality = painQuality,
                        onLevelSelected = onPainLevelChanged,
                        onLocationSelected = onPainLocationChanged,
                        onQualitySelected = onPainQualityChanged,
                        onSpeakAssessment = onSpeakPain,
                        highContrastMode = highContrastMode
                    )
                }
            }

            items(phrases, key = { it.id }) { phrase ->
                val isCurrentlySpeaking = isSpeaking && currentSpokenPhrase == phrase.spokenText
                AacCard(
                    phrase = phrase,
                    isCurrentlySpeaking = isCurrentlySpeaking,
                    onSpeak = { onSpeakPhrase(phrase) },
                    onDeleteCustom = if (phrase.isCustom) { { onDeletePhrase(phrase) } } else null,
                    highContrastMode = highContrastMode
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
