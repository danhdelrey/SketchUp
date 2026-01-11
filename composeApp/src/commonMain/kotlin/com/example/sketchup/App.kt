package com.example.sketchup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.sketchup.core.theme.AppTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import sketchup.composeapp.generated.resources.Res
import sketchup.composeapp.generated.resources.compose_multiplatform

@Composable
fun App() {
    AppTheme {
        Scaffold { paddingValues ->
            Column {
                Text(
                    "Không ai bị bắt, giam giữ hay đày đi nơi khác một cách độc đoán.\n" +
                            "Mọi người, với tư cách bình đẳng về mọi phương diện, đều có quyền được một toà án độc lập và vô tư phân xử công bằng và công khai để xác định quyền, nghĩa vụ hoặc bất cứ một lời buộc tội nào đối với người đó.\n" +
                            "Mọi người đều có quyền nghỉ ngơi và giải trí, kể cả quyền được hạn chế hợp lý về số giờ làm việc và hưởng những ngày nghỉ định kỳ được trả lương.",
                    modifier = Modifier.padding(paddingValues),
                    style = MaterialTheme.typography.bodyMedium

                )
                Button(
                    onClick = {}
                ){
                    Text("Click me")
                }
            }
        }
    }
}