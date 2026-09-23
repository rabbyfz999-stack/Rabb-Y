package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class GeminiTaskTier(
    val modelId: String,
    val label: String,
    val subhead: String,
    val badge: String
) {
    FAST(
        modelId = "gemini-3.1-flash-lite-preview",
        label = "Flash-Lite 3.1",
        subhead = "Ultra-low latency for instant edits, grammar, & quick summaries",
        badge = "FAST"
    ),
    GENERAL(
        modelId = "gemini-3.5-flash",
        label = "Gemini 3.5 Flash",
        subhead = "Versatile intelligence for writing, multimodal vision & daily tasks",
        badge = "BALANCED"
    ),
    GENERAL_38(
        modelId = "gemini-3.8-flash",
        label = "Gemini 3.8 Flash",
        subhead = "Next-generation model for speed, multimodal understanding & code",
        badge = "3.8 FLASH"
    ),
    DEEP(
        modelId = "gemini-3.1-pro-preview",
        label = "Gemini 3.1 Pro",
        subhead = "Deep multi-step reasoning, comprehensive research & complex synthesis",
        badge = "DEEP PRO"
    )
}

@JsonClass(generateAdapter = true)
data class GeminiGenerateRequest(
    val contents: List<ContentItem>,
    val generationConfig: GenerationConfigItem? = null
)

@JsonClass(generateAdapter = true)
data class ContentItem(
    val role: String? = null,
    val parts: List<PartItem>
)

@JsonClass(generateAdapter = true)
data class PartItem(
    val text: String? = null,
    val inlineData: InlineDataItem? = null
)

@JsonClass(generateAdapter = true)
data class InlineDataItem(
    @Json(name = "mimeType") val mimeType: String,
    val data: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfigItem(
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null,
    val maxOutputTokens: Int? = null
)

@JsonClass(generateAdapter = true)
data class GeminiGenerateResponse(
    val candidates: List<CandidateItem>? = null,
    val error: GeminiErrorDetails? = null
)

@JsonClass(generateAdapter = true)
data class CandidateItem(
    val content: ContentItem? = null,
    val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiErrorDetails(
    val code: Int? = null,
    val message: String? = null,
    val status: String? = null
)
