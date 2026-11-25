package dev.west.byteverse.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ByteverseExtendedColors(
    val glowPrimary: Color = NeonViolet,
    val glowSecondary: Color = ElectricPurple,
    val cardBackground: Color = NebulaCard,
    val elevatedBackground: Color = ElevatedCard,
    val borderSubtle: Color = SubtleBorder,
    val accentCyan: Color = CyberCyan,
    val accentEmerald: Color = EmeraldPulse,
    val accentAmber: Color = AmberFlare,
    val textMuted: Color = TextMuted
)

val LocalByteverseColors = staticCompositionLocalOf { ByteverseExtendedColors() }

private val DarkColorScheme = darkColorScheme(
    primary = NeonViolet,
    onPrimary = Color.White,
    primaryContainer = DeepAmethyst,
    onPrimaryContainer = PlasmaGlow,
    secondary = BrightOrchid,
    onSecondary = VoidBlack,
    secondaryContainer = ElevatedCard,
    onSecondaryContainer = TextPrimary,
    tertiary = CyberCyan,
    onTertiary = VoidBlack,
    background = VoidBlack,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = NebulaCard,
    onSurfaceVariant = TextSecondary,
    outline = SubtleBorder,
    error = CrimsonAlert,
    onError = Color.White
)

@Composable
fun ByteverseTheme(
    content: @Composable () -> Unit
) {
    val extendedColors = ByteverseExtendedColors()
    CompositionLocalProvider(LocalByteverseColors provides extendedColors) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = ByteverseTypography,
            content = content
        )
    }
}
