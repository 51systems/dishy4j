package dishy4j.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dishy4j.ui.content.status.StatusContent
import dishy4j.ui.content.status.StatusViewModel
import dishy4j.ui.content.status.StatusViewState

@Composable
fun MainScreen(statusViewModel: StatusViewModel) {

    val statusViewState: StatusViewState by statusViewModel.state.collectAsState()
    StatusContent(statusViewState)
}