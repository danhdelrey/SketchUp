package com.example.sketchup.view.features.drawing.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sketchup.domain.model.Brush

/**
 * Brush size picker component
 * Now uses domain model for better separation of concerns
 */
@Composable
fun BrushSizePicker(
    modifier: Modifier = Modifier,
    brush: Brush = Brush.DEFAULT,
    onSizeSelected: (Float) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    // Button hình tròn hiển thị preview của brush
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(2.dp, Color.Gray, CircleShape)
            .clickable { showDialog = true },
        contentAlignment = Alignment.Center
    ) {
        // Preview dot với kích thước hiện tại
        Canvas(modifier = Modifier.size(40.dp)) {
            drawCircle(
                color = brush.color.copy(alpha = brush.opacity),
                radius = (brush.size / 2).coerceIn(2f, 15f)
            )
        }
    }

    if (showDialog) {
        BrushSizeDialog(
            brush = brush,
            onDismiss = { showDialog = false },
            onSizeConfirm = { selectedSize ->
                onSizeSelected(selectedSize)
                showDialog = false
            }
        )
    }
}

@Composable
private fun BrushSizeDialog(
    brush: Brush,
    onDismiss: () -> Unit,
    onSizeConfirm: (Float) -> Unit
) {
    var size by remember { mutableStateOf(brush.size) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chọn kích thước cọ") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Preview của brush size
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(60.dp)) {
                        drawCircle(
                            color = brush.color.copy(alpha = brush.opacity),
                            radius = size / 2
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Slider để chọn size
                Text(
                    text = "Kích thước: ${size.toInt()}px",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = size,
                    onValueChange = { size = it },
                    valueRange = 2f..50f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSizeConfirm(size) }) {
                Text("Xác nhận")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

