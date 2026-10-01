package io.github.aedev.flow.ui.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Using system default (Roboto on Android)
val InterFontFamily = FontFamily.Default

private val BaseTypography =
    Typography(
        // Display - Large titles
        displayLarge =
            TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 38.sp,
                lineHeight = 44.sp,
                letterSpacing = 0.sp,
            ),
        displayMedium =
            TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                lineHeight = 42.sp,
                letterSpacing = 0.sp,
            ),
        // Headline - Screen titles
        headlineLarge =
            TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 30.sp,
                lineHeight = 38.sp,
                letterSpacing = 0.sp,
            ),
        headlineMedium =
            TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 26.sp,
                lineHeight = 34.sp,
                letterSpacing = 0.sp,
            ),
        // Title - Card titles, section headers
        titleLarge =
            TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 24.sp,
                lineHeight = 32.sp,
                letterSpacing = 0.sp,
            ),
        titleMedium =
            TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 22.sp,
                lineHeight = 30.sp,
                letterSpacing = 0.1.sp,
            ),
        titleSmall =
            TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.1.sp,
            ),
        // Body - Main content
        bodyLarge =
            TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                lineHeight = 30.sp,
                letterSpacing = 0.5.sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.25.sp,
            ),
        bodySmall =
            TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                lineHeight = 26.sp,
                letterSpacing = 0.4.sp,
            ),
        // Label - Buttons, tabs
        labelLarge =
            TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.1.sp,
            ),
        labelMedium =
            TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                lineHeight = 26.sp,
                letterSpacing = 0.5.sp,
            ),
        labelSmall =
            TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp,
            ),
    )

private fun TextStyle.emphasized(weight: FontWeight = FontWeight.Bold): TextStyle = copy(fontWeight = weight)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val Typography =
    BaseTypography.copy(
        displayLargeEmphasized = BaseTypography.displayLarge.emphasized(FontWeight.ExtraBold),
        displayMediumEmphasized = BaseTypography.displayMedium.emphasized(FontWeight.ExtraBold),
        displaySmallEmphasized = BaseTypography.displaySmall.emphasized(),
        headlineLargeEmphasized = BaseTypography.headlineLarge.emphasized(),
        headlineMediumEmphasized = BaseTypography.headlineMedium.emphasized(),
        headlineSmallEmphasized = BaseTypography.headlineSmall.emphasized(),
        titleLargeEmphasized = BaseTypography.titleLarge.emphasized(),
        titleMediumEmphasized = BaseTypography.titleMedium.emphasized(),
        titleSmallEmphasized = BaseTypography.titleSmall.emphasized(),
        bodyLargeEmphasized = BaseTypography.bodyLarge.emphasized(FontWeight.Medium),
        bodyMediumEmphasized = BaseTypography.bodyMedium.emphasized(FontWeight.Medium),
        bodySmallEmphasized = BaseTypography.bodySmall.emphasized(FontWeight.Medium),
        labelLargeEmphasized = BaseTypography.labelLarge.emphasized(),
        labelMediumEmphasized = BaseTypography.labelMedium.emphasized(),
        labelSmallEmphasized = BaseTypography.labelSmall.emphasized(),
    )
