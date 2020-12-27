package dishy4j.ui.content.status

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.Duration

/**
 * Entry point for a status fragment.
 */
@Composable
fun StatusContent(
    viewState: StatusViewState
) {
    Surface {
        Box(modifier = Modifier.fillMaxSize()) {
            when (viewState) {
                StatusViewState.Initial -> Initial()
                StatusViewState.DishyUnavailable -> DishyUnavailable()
                is StatusViewState.Status -> Status(viewState)
            }
        }
    }
}

@Composable
private fun Initial() {
    Text("Connecting to Dishy...")
}

@Composable
private fun DishyUnavailable() {
    Text("Unable to connect to Dishy... is 192.168.100.1 reachable?", color = MaterialTheme.colors.error)
}

@Composable
private fun Status(viewState: StatusViewState.Status) {
    Column(
        Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row {
                Text("Status: ")
                Text("${viewState.state}")
            }
            Row {
                Text("SNR: ")
                Text("${viewState.signalToNoiseRatio}")
            }
            Row {
                Text("Uptime: ")
                Text(viewState.uptime.format())
            }
        }

        Spacer(Modifier.preferredHeight(10.dp))

        Column(
            Modifier.fillMaxWidth()
        ) {
            DeviceInfo(viewState.deviceInfo)
            ObstructionStats(viewState.obstructionStats)
        }
    }
}

@Composable
private fun DeviceInfo(info: DeviceInfo) = with(info) {
    Surface(
        Modifier.padding(2.dp).fillMaxWidth(),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
    ) {
        Column(
            Modifier.fillMaxWidth()
        ) {
            Row {
                Text("Device Info", fontWeight = FontWeight.Bold)
            }

            Column(
                Modifier.fillMaxWidth().padding(start = 10.dp)
            ) {
                Row {
                    Text("ID: ")
                    Text(id)
                }

                Row {
                    Text("Country Code: ")
                    Text(countryCode)
                }

                Text("Version")
                Column(
                    Modifier.fillMaxWidth().padding(start = 10.dp)
                ) {
                    Row {
                        Text("Hardware: ")
                        Text(hardwareVersion)
                    }

                    Row {
                        Text("Software: ")
                        Text(softwareVersion)
                    }
                }
            }
        }
    }
}

@Composable
private fun ObstructionStats(stats: ObstructionStats) = with(stats) {
    val colour = if (currentlyObstructed) {
        MaterialTheme.colors.error
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
    }

    Surface(
        Modifier.padding(2.dp).fillMaxWidth(),
        color = colour
    ) {
        Column(
            Modifier.fillMaxWidth()
        ) {
            Row {
                Text("Obstruction Stats", fontWeight = FontWeight.Bold)
            }

            Column(
                Modifier.fillMaxWidth().padding(start = 10.dp)
            ) {
                Row {
                    Text("Obstructed: ")
                    Text("$currentlyObstructed")
                }

                Row {
                    Text("In Past Day: ")
                    Text("%.3f%%".format(fractionObstructed * 100))
                }

                Row {
                    Text("In Past Day: ")
                    Text(last24HrObstructed.format())
                }
            }
        }
    }
}

private fun Duration.format() = buildString {
    toDaysPart().let { days ->
        if (days > 0) {
            append(days)
            append(" Days ")
        }
    }

    toHoursPart().let { hours ->
        if (hours > 0) {
            append(toHoursPart())
            append(':')
        }
    }
    append("%02d".format(toMinutesPart()))
    append(':')
    append("%02d".format(toSecondsPart()))
}
