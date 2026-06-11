package com.example.ui

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.Lead
import com.example.data.repository.GeminiRepository
import com.example.data.repository.LeadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

sealed interface SearchUiState {
    object Idle : SearchUiState
    object Searching : SearchUiState
    data class Success(val results: List<Lead>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

sealed interface TranscriptionUiState {
    object Idle : TranscriptionUiState
    object Recording : TranscriptionUiState
    object Transcribing : TranscriptionUiState
    data class Success(val text: String) : TranscriptionUiState
    data class Error(val message: String) : TranscriptionUiState
}

class LeadViewModel(
    private val leadRepository: LeadRepository,
    private val geminiRepository: GeminiRepository
) : ViewModel() {

    // --- Database Exposure ---
    val allLeads: StateFlow<List<Lead>> = leadRepository.allLeads
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Search State ---
    val searchQuery = MutableStateFlow("")
    val searchUiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)

    // --- Audio Transcription State ---
    val transcriptionUiState = MutableStateFlow<TranscriptionUiState>(TranscriptionUiState.Idle)
    private var mediaRecorder: MediaRecorder? = null
    private var recordingFile: File? = null

    // --- Simulated Demo State injection for Empty Database ---
    init {
        viewModelScope.launch {
            // Check if DB is empty, then populate representative demo leads
            leadRepository.allLeads.collect { list ->
                if (list.isEmpty()) {
                    populateDemoData()
                }
            }
        }
    }

    private suspend fun populateDemoData() {
        val demoLeads = listOf(
            Lead(
                name = "Apex Dental Care",
                category = "Dentist",
                address = "124 Park Ave, New York, NY",
                phone = "(212) 555-0192",
                website = "",
                rating = 3.6,
                reviewCount = 14,
                notes = "Vulnerable Google rating (3.6) with zero recent reviews. Website is completely missing. Excellent pitch candidate for Web Design and automated review expansion program.",
                status = "NEW",
                revenueEstimate = 1800.0,
                mapClicksCall = 0,
                mapClicksWebsite = 0,
                mapClicksDirections = 0
            ),
            Lead(
                name = "Downtown Auto Body",
                category = "Auto Repair",
                address = "789 Broadway, New York, NY",
                phone = "(212) 555-0841",
                website = "http://downtownautobody-dummy.com",
                rating = 4.8,
                reviewCount = 280,
                notes = "High-performing service business. Active review profile. Pitch local AdWords optimizer retainer and Maps Local-Pack citation boosting packages.",
                status = "CONTACTED",
                revenueEstimate = 800.0,
                mapClicksCall = 12,
                mapClicksWebsite = 22,
                mapClicksDirections = 8
            ),
            Lead(
                name = "Zen Oasis Salon",
                category = "Beauty & Wellness",
                address = "42 Madison Ave, New York, NY",
                phone = "(212) 555-9988",
                website = "http://zenoasissalon-dummy.com",
                rating = 4.2,
                reviewCount = 67,
                notes = "Valid website but completely unoptimized for mobile. Slow load speed. Conversions are leaking. Offer mobile speed optimization or headless revamp proposal.",
                status = "FOLLOW_UP",
                revenueEstimate = 1200.0,
                mapClicksCall = 4,
                mapClicksWebsite = 15,
                mapClicksDirections = 2
            ),
            Lead(
                name = "The Local Grind Studio",
                category = "Coffee Shop & Co-Working",
                address = "56 Greenwich St, New York, NY",
                phone = "(212) 555-4321",
                website = "",
                rating = 4.1,
                reviewCount = 38,
                notes = "No listed webpage and missing claim tags. Pitch maps listing overhaul, standard reservation integration, and local newsletter campaign starter.",
                status = "CONVERTED",
                revenueEstimate = 1500.0,
                mapClicksCall = 18,
                mapClicksWebsite = 0,
                mapClicksDirections = 45
            )
        )
        leadRepository.insertLeads(demoLeads)
    }

    // --- Search Google Maps (Grounded via Gemini-3.5-flash with maps tool) ---
    fun searchMaps(query: String) {
        if (query.trim().isEmpty()) {
            searchUiState.value = SearchUiState.Error("Please enter a search topic or location.")
            return
        }

        searchUiState.value = SearchUiState.Searching
        viewModelScope.launch {
            try {
                val results = geminiRepository.searchMapLeads(query)
                if (results.isEmpty()) {
                    searchUiState.value = SearchUiState.Error(
                        "No businesses found or API Key configuration error. Make sure your GEMINI_API_KEY is active."
                    )
                } else {
                    searchUiState.value = SearchUiState.Success(results)
                }
            } catch (e: Exception) {
                Log.e("LeadViewModel", "Error searching maps: ${e.localizedMessage}", e)
                searchUiState.value = SearchUiState.Error("Search failure: ${e.localizedMessage ?: "Unknown network issue"}")
            }
        }
    }

    // --- Lead Management Actions ---
    fun importLeads(leads: List<Lead>) {
        viewModelScope.launch {
            leadRepository.insertLeads(leads)
            // Reset search states
            searchUiState.value = SearchUiState.Idle
            searchQuery.value = ""
        }
    }

    fun addLeadDirect(lead: Lead) {
        viewModelScope.launch {
            leadRepository.insertLead(lead)
        }
    }

    fun updateStatus(lead: Lead, newStatus: String) {
        viewModelScope.launch {
            leadRepository.updateLeadStatus(lead, newStatus)
        }
    }

    fun saveLeadNotes(lead: Lead, notes: String) {
        viewModelScope.launch {
            leadRepository.updateLeadNotes(lead, notes)
        }
    }

    fun trackInteraction(lead: Lead, type: String) {
        viewModelScope.launch {
            leadRepository.incrementInteraction(lead, type)
        }
    }

    fun deleteLead(id: Int) {
        viewModelScope.launch {
            leadRepository.deleteLeadById(id)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            leadRepository.deleteAllLeads()
        }
    }

    // --- Audio Dictation and Recording ---
    fun startVoiceRecording(context: Context) {
        transcriptionUiState.value = TranscriptionUiState.Recording
        try {
            recordingFile = File(context.cacheDir, "voice_note_dictation.m4a").apply {
                if (exists()) delete()
            }

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(44100)
                setAudioEncodingBitRate(96000)
                setOutputFile(recordingFile?.absolutePath)
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e("LeadViewModel", "Failed to start MediaRecorder", e)
            transcriptionUiState.value = TranscriptionUiState.Error("Microphone access failed: ${e.localizedMessage}")
        }
    }

    fun stopVoiceRecording() {
        if (transcriptionUiState.value != TranscriptionUiState.Recording) return
        transcriptionUiState.value = TranscriptionUiState.Transcribing

        viewModelScope.launch {
            try {
                mediaRecorder?.apply {
                    stop()
                    release()
                }
                mediaRecorder = null

                val file = recordingFile
                if (file != null && file.exists()) {
                    val bytes = file.readBytes()
                    val transcript = geminiRepository.transcribeAudio(bytes, "audio/m4a")
                    transcriptionUiState.value = TranscriptionUiState.Success(transcript)
                } else {
                    transcriptionUiState.value = TranscriptionUiState.Error("Recorded file could not be accessed.")
                }
            } catch (e: Exception) {
                Log.e("LeadViewModel", "Failed to stop recording & transcribe", e)
                transcriptionUiState.value = TranscriptionUiState.Error("Recording stop/transcription error: ${e.localizedMessage}")
            } finally {
                mediaRecorder = null
            }
        }
    }

    fun resetVoiceMemo() {
        transcriptionUiState.value = TranscriptionUiState.Idle
        recordingFile?.delete()
        recordingFile = null
    }

    // --- Factory ---
    class Factory(
        private val leadRepository: LeadRepository,
        private val geminiRepository: GeminiRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LeadViewModel::class.java)) {
                return LeadViewModel(leadRepository, geminiRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
