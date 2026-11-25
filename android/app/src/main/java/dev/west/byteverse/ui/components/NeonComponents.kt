package dev.west.byteverse.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.west.byteverse.domain.model.LanguageStat
import dev.west.byteverse.ui.theme.BrightOrchid
import dev.west.byteverse.ui.theme.CrimsonAlert
import dev.west.byteverse.ui.theme.DeepAmethyst
import dev.west.byteverse.ui.theme.ElectricPurple
import dev.west.byteverse.ui.theme.ElevatedCard
import dev.west.byteverse.ui.theme.NebulaCard
import dev.west.byteverse.ui.theme.NeonViolet
import dev.west.byteverse.ui.theme.ObsidianSurface
import dev.west.byteverse.ui.theme.PlasmaGlow
import dev.west.byteverse.ui.theme.SubtleBorder
import dev.west.byteverse.ui.theme.TextMuted
import dev.west.byteverse.ui.theme.TextPrimary
import dev.west.byteverse.ui.theme.TextSecondary
import dev.west.byteverse.ui.theme.VoidBlack

@Composable
fun CosmicBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "cosmic_glow")
    val pulse by transition.animateFloat(
        initialValue = 0.22f,
        targetValue = 0.42f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_alpha"
    )

    Box(
        modifier = modifier
            .background(VoidBlack)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ElectricPurple.copy(alpha = pulse),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.85f, size.height * 0.08f),
                        radius = size.width * 0.75f
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            DeepAmethyst.copy(alpha = pulse * 0.65f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.1f, size.height * 0.75f),
                        radius = size.width * 0.65f
                    )
                )
            }
    ) {
        content()
    }
}

@Composable
fun GlowingCard(
    modifier: Modifier = Modifier,
    glowColor: Color = NeonViolet,
    cornerRadius: Dp = 20.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val cardModifier = modifier
        .then(
            if (onClick != null) {
                Modifier.clip(shape).clickable(onClick = onClick)
            } else {
                Modifier
            }
        )

    Card(
        modifier = cardModifier,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = NebulaCard.copy(alpha = 0.94f)),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    glowColor.copy(alpha = 0.65f),
                    SubtleBorder,
                    glowColor.copy(alpha = 0.28f)
                )
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            content = content
        )
    }
}

@Composable
fun NeonSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    placeholder: String = "Enter GitHub username (e.g. torvalds)"
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = BrightOrchid
                )
            },
            trailingIcon = {
                if (value.isNotEmpty()) {
                    IconButton(onClick = { onValueChange("") }) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Clear username",
                            tint = TextSecondary
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                    onSubmit()
                }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonViolet,
                unfocusedBorderColor = SubtleBorder,
                focusedContainerColor = ObsidianSurface,
                unfocusedContainerColor = ObsidianSurface,
                cursorColor = BrightOrchid,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                focusManager.clearFocus()
                onSubmit()
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricPurple,
                contentColor = Color.White,
                disabledContainerColor = DeepAmethyst.copy(alpha = 0.5f)
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = PlasmaGlow,
                    strokeWidth = 2.2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "SCANNING UNIVERSE...",
                    style = MaterialTheme.typography.labelLarge
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ANALYZE DEVELOPER",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun AnimatedMetricTile(
    label: String,
    value: Int,
    icon: ImageVector,
    accentColor: Color = BrightOrchid,
    modifier: Modifier = Modifier
) {
    val animatedValue by animateIntAsState(
        targetValue = value,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "metric_$label"
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = ElevatedCard,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            Text(
                text = formatCompactCount(animatedValue),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun LanguageBarRow(
    stat: LanguageStat,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsStateWithEntry(target = stat.percentage)
    val percentText = "${(stat.percentage * 100).toInt()}%"

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(stat.color)
                )
                Text(
                    text = stat.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = "${stat.repoCount} repos • $percentText",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(ObsidianSurface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress.coerceIn(0.04f, 1f))
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(stat.color, NeonViolet)
                        )
                    )
            )
        }
    }
}

@Composable
fun animateFloatAsStateWithEntry(target: Float): androidx.compose.runtime.State<Float> {
    return androidx.compose.animation.core.animateFloatAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing),
        label = "bar_progress"
    )
}

@Composable
fun CosmicLoadingState(
    username: String,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "loading_pulse")
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    GlowingCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(44.dp),
                color = NeonViolet,
                trackColor = SubtleBorder,
                strokeWidth = 3.5.dp
            )
            Text(
                text = "SYNCING TELEMETRY",
                style = MaterialTheme.typography.labelLarge,
                color = PlasmaGlow,
                modifier = Modifier.alpha(alpha)
            )
            Text(
                text = "Fetching GitHub profile & repositories for @$username...",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CosmicErrorCard(
    message: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlowingCard(
        modifier = modifier.fillMaxWidth(),
        glowColor = CrimsonAlert
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.ErrorOutline,
                contentDescription = null,
                tint = CrimsonAlert,
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SIGNAL INTERRUPTED",
                    style = MaterialTheme.typography.labelLarge,
                    color = CrimsonAlert
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onRetry,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert)
                    ) {
                        Text("Retry", style = MaterialTheme.typography.labelMedium)
                    }
                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElevatedCard)
                    ) {
                        Text("Dismiss", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun CosmicEmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlowingCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(DeepAmethyst.copy(alpha = 0.25f))
                    .border(1.dp, NeonViolet.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BrightOrchid,
                    modifier = Modifier.size(30.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onAction,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple)
            ) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

fun formatCompactCount(value: Int): String {
    return when {
        value >= 1_000_000 -> String.format(java.util.Locale.US, "%.1fM", value / 1_000_000f)
        value >= 1_000 -> String.format(java.util.Locale.US, "%.1fk", value / 1_000f)
        else -> value.toString()
    }
}
