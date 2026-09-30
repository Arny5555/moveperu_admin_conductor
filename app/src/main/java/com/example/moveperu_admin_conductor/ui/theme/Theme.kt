package com.example.moveperu_admin_conductor.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MoveColors = lightColorScheme(
    primary = Petroleo,
    onPrimary = Color.White,
    primaryContainer = AguaClara,
    onPrimaryContainer = PetroleoOsc,
    secondary = Senal,
    onSecondary = Tinta,
    secondaryContainer = SenalSuave,
    onSecondaryContainer = Color(0xFF473400),
    tertiary = Turquesa,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD5EEF1),
    onTertiaryContainer = Color(0xFF07343A),
    background = Neblina,
    onBackground = Tinta,
    surface = Color.White,
    onSurface = Tinta,
    surfaceVariant = Color(0xFFE3EAE9),
    onSurfaceVariant = Color(0xFF475856),
    outline = Borde,
    outlineVariant = Color(0xFFDDE5E4),
    error = RojoAlerta
)

@Composable
fun Moveperu_admin_conductorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MoveColors,
        typography = Typography,
        content = content
    )
}
