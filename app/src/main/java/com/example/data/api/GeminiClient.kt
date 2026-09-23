package com.example.data.api

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import com.example.data.model.ContentItem
import com.example.data.model.GeminiGenerateRequest
import com.example.data.model.GenerationConfigItem
import com.example.data.model.InlineDataItem
import com.example.data.model.PartItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

data class GeminiExecutionResult(
    val text: String,
    val modelUsed: String,
    val latencyMs: Long,
    val isSimulation: Boolean = false,
    val errorNotice: String? = null
)

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val apiService: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    fun getApiKey(): String {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key.isNullOrBlank() || key == "MY_GEMINI_API_KEY") "" else key
        } catch (_: Exception) {
            ""
        }
    }

    fun isApiKeyConfigured(): Boolean {
        return getApiKey().isNotBlank()
    }

    suspend fun executeGenerate(
        modelId: String,
        prompt: String,
        base64Image: String? = null,
        mimeType: String = "image/jpeg",
        systemInstruction: String? = null
    ): Result<GeminiExecutionResult> = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val apiKey = getApiKey()

        // If no real API key is configured in Secrets panel, provide realistic preview response
        if (apiKey.isBlank()) {
            val previewText = generateSimulatedResponse(modelId, prompt, base64Image != null)
            val duration = (System.currentTimeMillis() - startTime).coerceAtLeast(350)
            return@withContext Result.success(
                GeminiExecutionResult(
                    text = previewText,
                    modelUsed = modelId,
                    latencyMs = duration,
                    isSimulation = true,
                    errorNotice = "Using preview mode. Add your GEMINI_API_KEY in the AI Studio Secrets panel for live cloud generation."
                )
            )
        }

        try {
            val parts = mutableListOf<PartItem>()
            if (!systemInstruction.isNullOrBlank()) {
                parts.add(PartItem(text = "[Instruction: $systemInstruction]\n\n"))
            }
            if (base64Image != null) {
                parts.add(PartItem(inlineData = InlineDataItem(mimeType = mimeType, data = base64Image)))
            }
            parts.add(PartItem(text = prompt))

            val request = GeminiGenerateRequest(
                contents = listOf(ContentItem(parts = parts)),
                generationConfig = GenerationConfigItem(
                    temperature = 0.7f,
                    maxOutputTokens = 2048
                )
            )

            val response = apiService.generateContent(
                model = modelId,
                apiKey = apiKey,
                request = request
            )

            val duration = System.currentTimeMillis() - startTime
            val output = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

            if (!output.isNullOrBlank()) {
                Result.success(
                    GeminiExecutionResult(
                        text = output,
                        modelUsed = modelId,
                        latencyMs = duration,
                        isSimulation = false
                    )
                )
            } else {
                val err = response.error?.message ?: "Model returned an empty candidate."
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            // Fallback gracefully if network fails or quota limit is reached
            val fallback = generateSimulatedResponse(modelId, prompt, base64Image != null)
            Result.success(
                GeminiExecutionResult(
                    text = fallback,
                    modelUsed = modelId,
                    latencyMs = duration,
                    isSimulation = true,
                    errorNotice = "Live call failed (${e.localizedMessage ?: "Network error"}). Displaying local preview."
                )
            )
        }
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    private fun generateSimulatedResponse(modelId: String, prompt: String, hasImage: Boolean): String {
        val lower = prompt.lowercase()
        return when {
            hasImage -> {
                "### Multimodal Vision Analysis ($modelId)\n\n" +
                "**Detected Elements:**\n" +
                "• Visual Content: High-contrast subject captured on device camera/picker\n" +
                "• Primary Focus: Identified relevant scene attributes, foreground patterns, and key text labels\n" +
                "• Key Insights: Document/Object detected with high fidelity.\n\n" +
                "**Analysis for:** \"$prompt\"\n" +
                "Based on the visual composition, the scene contains structured details and clean visual edges. All visible indicators are categorized and ready for export."
            }
            lower.contains("summar") || lower.contains("tl;dr") || lower.contains("brief") -> {
                "### Quick Summary\n\n" +
                "• **Core Objective:** The provided context emphasizes streamlined execution and clarity.\n" +
                "• **Key Insight:** Direct actionable components can be leveraged immediately.\n" +
                "• **Conclusion:** Optimized for mobile productivity with rapid turnaround."
            }
            lower.contains("polish") || lower.contains("grammar") || lower.contains("rewrite") || lower.contains("tone") -> {
                "Here is the refined version:\n\n" +
                "\"${prompt.replace(Regex("(?i)rewrite|polish|tone|in a.*tone"), "").trim().ifEmpty { "Thank you for the update. I have reviewed the details and will proceed with the recommended milestones immediately." }}\"\n\n" +
                "**Improvements made:** Enhanced active voice, tightened phrasing, and eliminated unnecessary passive markers."
            }
            lower.contains("translat") -> {
                "**Translation Result:**\n\n" +
                "\"Merci pour votre message. Nous avançons avec le plan prévu et vous tiendrons informé sous peu.\"\n\n" +
                "(Language detected & translated preserving conversational nuance)."
            }
            lower.contains("code") || lower.contains("kotlin") || lower.contains("debug") -> {
                "```kotlin\n// Optimized Kotlin solution\nfun processTask(input: String): Result<String> {\n    return runCatching {\n        input.trim().takeIf { it.isNotEmpty() } ?: error(\"Empty payload\")\n    }\n}\n```\n\n**Key Takeaways:**\n1. Thread-safe execution using idiomatic Kotlin coroutines.\n2. Exception boundaries captured via `runCatching`."
            }
            modelId.contains("pro") -> {
                "### Deep Strategic Analysis (Gemini 3.1 Pro)\n\n" +
                "**1. Core Architecture & Problem Decomposition**\n" +
                "Analyzing \"$prompt\" through a multi-dimensional perspective reveals three primary pillars: architectural feasibility, execution velocity, and long-term maintainability.\n\n" +
                "**2. Comparative Trade-offs**\n" +
                "• *Pillar A (Low Latency):* Leveraging specialized lightweight inference models for high-frequency interactions.\n" +
                "• *Pillar B (Deep Synthesis):* Routing complex multi-hop reasoning to high-parameter Pro models.\n\n" +
                "**3. Recommended Action Plan**\n" +
                "1. Implement layered caching for frequent prompts.\n" +
                "2. Standardize structured output schemas to prevent parsing drift.\n" +
                "3. Continuously benchmark edge-to-cloud round-trip latencies."
            }
            else -> {
                "**AI Mobile Intelligence Response ($modelId):**\n\n" +
                "Here is the breakdown for your inquiry:\n\n" +
                "1. **Strategic Overview:** For \"$prompt\", modern workflows benefit from iterative refinement and structured checkpoints.\n" +
                "2. **Immediate Action:** Focus on high-impact steps first, validating output quality at each interval.\n" +
                "3. **Next Steps:** You can copy this insight, save it to your local AI Mobile workspace, or request further expansion."
            }
        }
    }
}
