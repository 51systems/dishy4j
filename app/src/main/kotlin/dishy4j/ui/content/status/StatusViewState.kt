package dishy4j.ui.content.status

import androidx.compose.runtime.Immutable
import java.time.Duration

/** View model for dishy status. */
sealed class StatusViewState {

    /** Initial unknown status */
    @Immutable
    object Initial : StatusViewState()

    /** Dishy cannot be reached. */
    @Immutable
    object DishyUnavailable : StatusViewState()

    @Immutable
    data class Status(
        val state: DishState,
        val deviceInfo: DeviceInfo,
        val obstructionStats: ObstructionStats,
        val uptime: Duration,
        val signalToNoiseRatio: Float,
    ) : StatusViewState()
}

enum class DishState {
    UNKNOWN,
    CONNECTED,
    SEARCHING,
    BOOTING
}

data class DeviceInfo(
    val id: String,
    val hardwareVersion: String,
    val softwareVersion: String,
    val countryCode: String
)

data class ObstructionStats(
    val currentlyObstructed: Boolean,
    val fractionObstructed: Float,
    val last24HrObstructed: Duration
)
