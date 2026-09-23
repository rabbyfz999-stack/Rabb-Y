package com.example.data.model

data class DeviceSystemSpecs(
    val deviceName: String = "AI Mobile Phone",
    val modelNumber: String = "AIM-8128X",
    val osVersion: String = "AI Mobile OS 16 (Android 16)",
    val processor: String = "Octa-Core AI Engine (3.3 GHz)",
    val npu: String = "Gemini Neural Processing Unit (NPU)",

    // RAM Specs: 8 GB
    val totalRamGb: Double = 8.0,
    val usedRamGb: Double = 3.6,
    val ramType: String = "LPDDR5X Ultra-Fast",
    val systemRamGb: Double = 1.6,
    val aiEngineRamGb: Double = 1.2,
    val appsRamGb: Double = 0.8,

    // Storage Specs: 128 GB
    val totalStorageGb: Double = 128.0,
    val usedStorageGb: Double = 41.2,
    val storageType: String = "UFS 3.1 High-Speed",
    val systemStorageGb: Double = 14.2,
    val aiCacheStorageGb: Double = 8.6,
    val appsStorageGb: Double = 12.4,
    val mediaStorageGb: Double = 5.2,
    val docsStorageGb: Double = 0.8,

    // Status
    val batteryPct: Int = 88,
    val temperatureC: Double = 34.2,
    val performanceMode: String = "AI Turbo (8GB Boosted)"
) {
    val freeRamGb: Double get() = (totalRamGb - usedRamGb).coerceAtLeast(0.1)
    val ramPercentage: Float get() = (usedRamGb / totalRamGb).toFloat().coerceIn(0f, 1f)

    val freeStorageGb: Double get() = (totalStorageGb - usedStorageGb).coerceAtLeast(0.1)
    val storagePercentage: Float get() = (usedStorageGb / totalStorageGb).toFloat().coerceIn(0f, 1f)
}
