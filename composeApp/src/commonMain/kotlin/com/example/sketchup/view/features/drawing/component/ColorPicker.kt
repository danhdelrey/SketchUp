
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

@Composable
fun ColorPicker(
    modifier: Modifier = Modifier,
    initialColor: Color = Color.Black,
    onColorSelected: (Color) -> Unit
) {
    var currentColor by remember { mutableStateOf(initialColor) }
    var showDialog by remember { mutableStateOf(false) }

    // Button hình tròn hiển thị màu
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(currentColor)
            .border(2.dp, Color.LightGray, CircleShape)
            .clickable { showDialog = true }
    )

    if (showDialog) {
        GradientColorPickerDialog(
            initialColor = currentColor,
            onDismiss = { showDialog = false },
            onColorConfirm = { selectedColor ->
                currentColor = selectedColor
                onColorSelected(selectedColor)
                showDialog = false
            }
        )
    }
}

@Composable
private fun GradientColorPickerDialog(
    initialColor: Color,
    onDismiss: () -> Unit,
    onColorConfirm: (Color) -> Unit
) {
    // Chuyển đổi màu ban đầu sang HSV để khởi tạo vị trí con trỏ
    val hsv = remember { rgbToHsv(initialColor) }
    var hue by remember { mutableStateOf(hsv[0]) }       // 0..360
    var saturation by remember { mutableStateOf(hsv[1]) } // 0..1
    var value by remember { mutableStateOf(hsv[2]) }      // 0..1

    // Màu hiện tại được tính toán từ H, S, V
    val currentColor = remember(hue, saturation, value) {
        Color.hsv(hue, saturation, value)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chọn màu") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 1. Bảng chọn Saturation & Value (Vùng hình vuông lớn)
                SaturationValuePanel(
                    hue = hue,
                    saturation = saturation,
                    value = value,
                    onSatValChanged = { s, v ->
                        saturation = s
                        value = v
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Thanh chọn Hue (Dải màu cầu vồng)
                HueBar(
                    hue = hue,
                    onHueChanged = { newHue ->
                        hue = newHue
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Preview màu đang chọn
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Màu đã chọn:")
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(currentColor)
                            .border(1.dp, Color.Gray, CircleShape)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onColorConfirm(currentColor) }) {
                Text("Chọn")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

@Composable
private fun SaturationValuePanel(
    hue: Float,
    saturation: Float,
    value: Float,
    onSatValChanged: (Float, Float) -> Unit
) {
    val density = LocalDensity.current.density

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Hình vuông
            .clip(RoundedCornerShape(4.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
    ) {
        val width = maxWidth
        val height = maxHeight

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    // Xử lý chạm và kéo để lấy tọa độ
                    detectTapGestures(
                        onPress = { offset ->
                            val s = (offset.x / size.width).coerceIn(0f, 1f)
                            val v = (1f - (offset.y / size.height)).coerceIn(0f, 1f)
                            onSatValChanged(s, v)
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        val s = (change.position.x / size.width).coerceIn(0f, 1f)
                        val v = (1f - (change.position.y / size.height)).coerceIn(0f, 1f)
                        onSatValChanged(s, v)
                    }
                }
        ) {
            // Lớp 1: Màu nền theo Hue
            drawRect(color = Color.hsv(hue, 1f, 1f))

            // Lớp 2: Gradient Trắng -> Trong suốt (Ngang) - Saturation
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.White, Color.Transparent)
                )
            )

            // Lớp 3: Gradient Trong suốt -> Đen (Dọc) - Value/Brightness
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black)
                )
            )

            // Vẽ vòng tròn selector
            val selectorX = saturation * size.width
            val selectorY = (1f - value) * size.height

            // Vòng tròn trắng viền đen để dễ nhìn trên mọi nền
            drawCircle(
                color = Color.White,
                radius = 8.dp.toPx(),
                center = Offset(selectorX, selectorY),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = Color.Black,
                radius = 8.dp.toPx(),
                center = Offset(selectorX, selectorY),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
            )
        }
    }
}

@Composable
private fun HueBar(
    hue: Float,
    onHueChanged: (Float) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .clip(RoundedCornerShape(15.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(15.dp))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val newHue = ((offset.x / size.width) * 360f).coerceIn(0f, 360f)
                        onHueChanged(newHue)
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        val newHue = ((change.position.x / size.width) * 360f).coerceIn(0f, 360f)
                        onHueChanged(newHue)
                    }
                }
        ) {
            // Vẽ dải màu cầu vồng
            val rainbowColors = listOf(
                Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red
            )
            drawRect(
                brush = Brush.horizontalGradient(rainbowColors)
            )

            // Vẽ thanh chỉ thị vị trí Hue
            val selectorX = (hue / 360f) * size.width
            drawLine(
                color = Color.White,
                start = Offset(selectorX, 0f),
                end = Offset(selectorX, size.height),
                strokeWidth = 3.dp.toPx()
            )
            drawLine(
                color = Color.Black,
                start = Offset(selectorX, 0f),
                end = Offset(selectorX, size.height),
                strokeWidth = 1.dp.toPx() // Viền đen mỏng
            )
        }
    }
}

// Hàm tiện ích: Chuyển đổi Color RGB sang HSV (vì Color.toArgb() không có sẵn trong commonMain một cách dễ dàng để tính toán)
fun rgbToHsv(color: Color): FloatArray {
    val r = color.red
    val g = color.green
    val b = color.blue

    val max = max(r, max(g, b))
    val min = min(r, min(g, b))
    val delta = max - min

    var h = 0f
    val s = if (max == 0f) 0f else delta / max
    val v = max

    if (delta != 0f) {
        if (max == r) {
            h = (g - b) / delta
        } else if (max == g) {
            h = 2f + (b - r) / delta
        } else {
            h = 4f + (r - g) / delta
        }
        h *= 60f
        if (h < 0) h += 360f
    }

    return floatArrayOf(h, s, v)
}