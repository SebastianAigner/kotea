import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotea.composeapp.generated.resources.Res
import kotea.composeapp.generated.resources.ipad
import kotea.composeapp.generated.resources.ipad2
import org.jetbrains.compose.resources.imageResource

fun main() = application {
    val state = rememberWindowState()
    var squashed by remember { mutableStateOf(false) }

    val zeroHeight by derivedStateOf {
        state.size.height <= 30.dp
    }
    LaunchedEffect(zeroHeight) {
        if (zeroHeight) squashed = true
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = if (squashed) "iPad Pro 2024 ad" else "kotea",
        state = state,
    ) {

        MaterialTheme(colors = lightColors()) {
            Surface(Modifier.fillMaxSize()) {
                Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
                    if (squashed) {
//                iPad()
                        Image(imageResource(Res.drawable.ipad2), "iPad Pro 2024")
                    } else {
                        App(getDatabaseBuilder())
                    }
                }
            }
        }
    }
}

@Composable
fun iPad() {
    Box(
        modifier = Modifier
            .size(300.dp, 200.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(2.dp, Color.Gray)
            .background(Color.Black)
    )
}
