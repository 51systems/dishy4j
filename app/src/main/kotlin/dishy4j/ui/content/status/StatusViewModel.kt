package dishy4j.ui.content.status

import SpaceX.API.Device.*
import SpaceX.API.Device.DeviceOuterClassBuilders.ToDevice
import com.github.marcoferrer.krotoplus.coroutines.launchProducerJob
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import mu.KLogging
import java.time.Duration
import java.time.Instant
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

private const val POLLING_INTERVAL = 1_000L
private const val POLLING_TIMEOUT = 5_000L

/**
 * Dishy status view model.
 */
class StatusViewModel(
    stub: DeviceCoroutineGrpc.DeviceCoroutineStub,
    context: CoroutineContext = EmptyCoroutineContext
) {
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + context + CoroutineName("StatusViewModel"))

    init {
        val (requestChannel, responseChannel) = stub.stream()

        var lastStatusUpdate = Instant.now()
        // consume responses
        scope.launch {
            responseChannel
                .consumeAsFlow()
                .collect { fromDevice ->
                    lastStatusUpdate = Instant.now()
                    _state.value = fromDevice.response.dishGetStatus.toViewState()
                }
        }

        // If we haven't updated the status in a while, show an error
        scope.launch {
            while (isActive) {
                delay(POLLING_TIMEOUT)
                if (Duration.between(lastStatusUpdate, Instant.now()).toMillis() > POLLING_TIMEOUT) {
                    logger.warn { "Unable to connect to dishy in last $POLLING_TIMEOUT ms" }
                    _state.value = StatusViewState.DishyUnavailable
                }
            }
        }

        // poll dishy for status changes
        scope.launchProducerJob(requestChannel) {
            while (isActive) {
                try {
                    withTimeout(POLLING_TIMEOUT) {
                        logger.info { "Request dishy status" }
                        requestChannel.send(ToDevice {
                            request {
                                getStatus = DeviceOuterClass.GetStatusRequest.getDefaultInstance()
                            }
                        })
                    }
                } catch (e: Exception) {
                    logger.error(e) { "Error getting dishy status" }
                    _state.value = StatusViewState.DishyUnavailable
                }

                delay(POLLING_INTERVAL)
            }
        }
    }

    private val _state = MutableStateFlow<StatusViewState>(StatusViewState.Initial)
    val state = _state.asStateFlow()

    private companion object : KLogging()
}

private fun Dish.DishGetStatusResponse.toViewState(): StatusViewState.Status {
    return StatusViewState.Status(
        state.toViewState(),
        deviceInfo.toViewState(),
        obstructionStats.toViewState(),
        Duration.ofSeconds(deviceState.uptimeS),
        snr
    )
}

private fun Dish.DishObstructionStats.toViewState(): ObstructionStats {
    return ObstructionStats(
        currentlyObstructed,
        fractionObstructed,
        Duration.ofSeconds(last24HObstructedS.toLong())
    )
}

private fun Common.DeviceInfo.toViewState(): DeviceInfo {
    return DeviceInfo(
        id,
        hardwareVersion,
        softwareVersion,
        countryCode
    )
}

private fun Dish.DishState.toViewState(): DishState {
    return when (this) {
        Dish.DishState.UNKNOWN -> DishState.UNKNOWN
        Dish.DishState.CONNECTED -> DishState.CONNECTED
        Dish.DishState.SEARCHING -> DishState.SEARCHING
        Dish.DishState.BOOTING -> DishState.BOOTING
        Dish.DishState.UNRECOGNIZED -> DishState.UNKNOWN
    }
}

