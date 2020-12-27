package dishy4j.ui

import SpaceX.API.Device.DeviceCoroutineGrpc
import androidx.compose.desktop.DesktopTheme
import androidx.compose.desktop.Window
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.ui.unit.IntSize
import dishy4j.ui.content.status.StatusViewModel
import dishy4j.ui.view.MainScreen
import io.grpc.ManagedChannelBuilder

fun main() = Window(
    title = "Dishy McFlatface",
    size = IntSize(800, 300)
) {
    val stub =
        DeviceCoroutineGrpc.newStub(ManagedChannelBuilder.forAddress("192.168.100.1", 9200).usePlaintext().build())

    val statusViewModel = StatusViewModel(
        stub
    )

    MaterialTheme(colors = darkColors()) {
        DesktopTheme {
            MainScreen(statusViewModel)

//            Column {
//                Button(onClick = {
//                    status = runBlocking {
//                        stub.handle {
//                            getStatus {  }
//                        }
//                    }.toString()
//                }) {
//                    Text(text)
//                }
//
//                Text(status)
//
//                Status("Desktop!")
//            }
        }
    }
}
