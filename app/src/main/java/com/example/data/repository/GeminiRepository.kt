package com.example.data.repository

import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.GenerationConfig
import com.example.data.api.GoogleMapsTool
import com.example.data.api.InlineData
import com.example.data.api.Part
import com.example.data.api.ResponseSchema
import com.example.data.api.RetrofitClient
import com.example.data.api.Tool
import com.example.data.database.Lead
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiRepository {

    private val apiService = RetrofitClient.service
    private val modelName = "gemini-3.5-flash"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Structuring JSON response for Leads
    private val leadSchema = ResponseSchema(
        type = "ARRAY",
        description = "List of local business leads optimized for maps details",
        items = ResponseSchema(
            type = "OBJECT",
            properties = mapOf(
                "name" to ResponseSchema(type = "STRING", description = "The business title"),
                "category" to ResponseSchema(type = "STRING", description = "Specific niche or business profile, e.g. Dental Office, Auto Repair, Coffee Shop"),
                "address" to ResponseSchema(type = "STRING", description = "Exact mailing address listed"),
                "phone" to ResponseSchema(type = "STRING", description = "Phone number with area code, or 'None' if unavailable"),
                "website" to ResponseSchema(type = "STRING", description = "Web address, or 'None' if unavailable"),
                "rating" to ResponseSchema(type = "STRING", description = "Review rating average (0.0 to 5.0)"),
                "reviewCount" to ResponseSchema(type = "STRING", description = "Review volume, or '0' if unranked"),
                "notes" to ResponseSchema(type = "STRING", description = "A detailed local strategy assessment. Pinpoint exactly what services they need (e.g., 'Missing a website completely - perfect for premium landing page pitch', 'Low 3.1 rating - needs Google Review booster campaigns', 'No listing optimizations - pitch maps listing service')")
            ),
            required = listOf("name", "category", "address", "phone", "website", "rating", "reviewCount", "notes")
        )
    )

    private val responseAdapter = moshi.adapter<List<Map<String, String>>>(
        Types.newParameterizedType(List::class.java, Map::class.java, String::class.java, String::class.java)
    )

    suspend fun searchMapLeads(query: String): List<Lead> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e("GeminiRepository", "API Key is missing or default placeholder!")
            return@withContext emptyList()
        }

        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(
                            text = "Search Google Maps for local businesses matching: '$query'. " +
                                    "Utilize Grounding with Google Maps to compile authentic listings, rating scores, review counts, websites, and phones. " +
                                    "Generate highly helpful strategy audits with actionable sales recommendations for each listing."
                        )
                    )
                )
            ),
            tools = listOf(Tool(googleMaps = GoogleMapsTool())),
            generationConfig = GenerationConfig(
                responseMimeType = "application/json",
                responseSchema = leadSchema,
                temperature = 0.2
            ),
            systemInstruction = Content(
                parts = listOf(
                    Part(
                        text = "You are an expert sales intelligence assistant. Your goal is to gather valid, authentic Google Maps listings " +
                                "and output them as a JSON array matching the specified layout. Ensure phone numbers, websites, " +
                                "and review states are exact as grounded in Google Maps."
                    )
                )
            )
        )

        try {
            val response = apiService.generateContent(modelName, apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (jsonText != null) {
                Log.d("GeminiRepository", "Raw JSON Response: $jsonText")
                val parsedList = responseAdapter.fromJson(jsonText)
                if (parsedList != null) {
                    return@withContext parsedList.map { map ->
                        val rate = map["rating"]?.toDoubleOrNull() ?: 0.0
                        val cnt = map["reviewCount"]?.toIntOrNull() ?: 0
                        val estRevenue = when {
                            map["website"] == "None" || map["website"].isNullOrEmpty() -> 1800.0 // higher value for missing website design
                            rate < 4.0 -> 950.0 // rating repair/growth value
                            else -> 600.0 // generic Google search optimizer retainer value
                        }
                        Lead(
                            name = map["name"] ?: "Unknown Business",
                            category = map["category"] ?: "General Business",
                            address = map["address"] ?: "Local Address",
                            phone = if (map["phone"] == "None") "" else (map["phone"] ?: ""),
                            website = if (map["website"] == "None") "" else (map["website"] ?: ""),
                            rating = rate,
                            reviewCount = cnt,
                            notes = map["notes"] ?: "Strategy audit ready.",
                            status = "NEW",
                            revenueEstimate = estRevenue,
                            timestamp = System.currentTimeMillis()
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiRepository", "Error compiling Maps Leads: ${e.message}", e)
        }
        return@withContext emptyList()
    }

    suspend fun transcribeAudio(audioBytes: ByteArray, mimeType: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Configuration Error: Gemini API key is missing. Please verify in the AI Studio Secrets panel."
        }

        val base64Audio = Base64.encodeToString(audioBytes, Base64.NO_WRAP)
        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = "Transcribe this audio memo. Do NOT summarize or add labels. Provide ONLY the transcribed text verbatim."),
                        Part(inlineData = InlineData(mimeType = mimeType, data = base64Audio))
                    )
                )
            ),
            generationConfig = GenerationConfig(temperature = 0.0)
        )

        try {
            val response = apiService.generateContent(modelName, apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "No text transcribed."
        } catch (e: Exception) {
            "Transcription failed: ${e.localizedMessage ?: "Unknown error"}"
        }
    }
}
