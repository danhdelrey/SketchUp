import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import sketchup.composeapp.generated.resources.Res
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import sketchup.composeapp.generated.resources.playpensans_bold
import sketchup.composeapp.generated.resources.playpensans_medium


@Composable
fun getPlaypenSansFontFamily(): FontFamily {
    return FontFamily(
        Font(resource = Res.font.playpensans_medium, weight = FontWeight.Medium), // Weight 500
        Font(resource = Res.font.playpensans_bold, weight = FontWeight.Bold)      // Weight 700
    )
}

@Composable
fun getAppTypography(): Typography {
    // Lấy FontFamily đã tạo ở bước trước
    val playpenSansFamily = getPlaypenSansFontFamily()

    return Typography(
        // --- DISPLAY: Dùng cho text rất lớn (màn hình chào, số liệu to) ---
        displayLarge = TextStyle(
            fontFamily = playpenSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = (-0.25).sp
        ),
        displayMedium = TextStyle(
            fontFamily = playpenSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 45.sp,
            lineHeight = 52.sp,
            letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontFamily = playpenSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = 0.sp
        ),

        // --- HEADLINE: Dùng cho tiêu đề chính của màn hình ---
        headlineLarge = TextStyle(
            fontFamily = playpenSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = playpenSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = playpenSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = 0.sp
        ),

        // --- TITLE: Dùng cho tiêu đề các section nhỏ hơn ---
        titleLarge = TextStyle(
            fontFamily = playpenSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = playpenSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = playpenSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),

        // --- BODY: Dùng cho nội dung văn bản chính ---
        bodyLarge = TextStyle(
            fontFamily = playpenSansFamily,
            fontWeight = FontWeight.Medium, // Body dùng Medium cho dễ đọc
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = playpenSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = playpenSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),

        // --- LABEL: Dùng cho Button, Caption, Label nhỏ ---
        labelLarge = TextStyle(
            fontFamily = playpenSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = playpenSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = playpenSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
    )
}