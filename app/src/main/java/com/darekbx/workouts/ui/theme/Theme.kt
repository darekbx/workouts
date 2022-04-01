package com.darekbx.workouts.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorPalette = darkColors(
    primary = Teal500,
    primaryVariant = Teal200,
    secondary = Purple500,
    onPrimary = Color.White,
    onSurface = Color.White
)

@Composable
fun WorkoutsTheme(content: @Composable() () -> Unit) {
    MaterialTheme(
        colors = ColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
