package dev.west.byteverse.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Hub
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.west.byteverse.ui.components.CosmicErrorCard
import dev.west.byteverse.ui.components.CosmicLoadingState
import dev.west.byteverse.ui.components.GlowingCard
import dev.west.byteverse.ui.components.NeonSearchBar
import dev.west.byteverse.ui.components.formatCompactCount
import dev.west.byteverse.ui.theme.BrightOrchid
import dev.west.byteverse.ui.theme.CyberCyan
import dev.west.byteverse.ui.theme.DeepAmethyst
import dev.west.byteverse.ui.theme.ElectricPurple
import dev.west.byteverse.ui.theme.ElevatedCard
import dev.west.byteverse.ui.theme.NeonViolet
import dev.west.byteverse.ui.theme.ObsidianSurface
import dev.west.byteverse.ui.theme.PlasmaGlow
import dev.west.byteverse.ui.theme.SubtleBorder
import dev.west.byteverse.ui.theme.TextMuted
import dev.west.byteverse.ui.theme.TextPrimary
import dev.west.byteverse.ui.theme.TextSecondary
import dev.west.byteverse.ui.viewmodel.ByteverseUiState

@Composable
fun HomeScreen(
    uiState: ByteverseUiState,
    onUsernameChange: (String) -> Unit,
    onAnalyze: (String?) -> Unit,
    onDismissError: () -> Unit,
    onNavigateToAnalyzer: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Brand Hero Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(ElectricPurple, DeepAmethyst)
                                    )
                                )
                                .border(1.dp, PlasmaGlow.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Hub,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "BYTEVERSE",
                                style = MaterialTheme.typography.headlineLarge,
                                color = TextPrimary
                            )
                            Text(
                                text = "Explore Your Developer Universe",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BrightOrchid
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ElevatedCard,
                        border = BorderStroke(1.dp, NeonViolet.copy(alpha = 0.45f))
                    ) {
                        Text(
                            text = "WEST.DEV",
                            style = MaterialTheme.typography.labelSmall,
                            color = PlasmaGlow,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // GitHub Scanner Card
        item {
            GlowingCard {
                Text(
                    text = "GITHUB UNIVERSE SCANNER",
                    style = MaterialTheme.typography.labelLarge,
                    color = PlasmaGlow
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enter any GitHub handle to decode languages, XP rank, achievements, and generate your scannable ID pass.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))

                NeonSearchBar(
                    value = uiState.usernameInput,
                    onValueChange = onUsernameChange,
                    onSubmit = {
                        onAnalyze(null)
                    },
                    isLoading = uiState.isLoading
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "QUICK LAUNCH",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.recentSearches.forEach { handle ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = ObsidianSurface,
                            border = BorderStroke(1.dp, SubtleBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(enabled = !uiState.isLoading) {
                                    onAnalyze(handle)
                                }
                        ) {
                            Text(
                                text = "@$handle",
                                style = MaterialTheme.typography.labelMedium,
                                color = BrightOrchid,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Loading / Error feedback
        item {
            AnimatedVisibility(
                visible = uiState.isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CosmicLoadingState(
                    username = uiState.usernameInput.trim().ifBlank { "developer" }
                )
            }
        }

        if (uiState.errorMessage != null && !uiState.isLoading) {
            item {
                CosmicErrorCard(
                    message = uiState.errorMessage,
                    onRetry = { onAnalyze(null) },
                    onDismiss = onDismissError
                )
            }
        }

        // Active Profile Summary OR Feature Overview
        val analytics = uiState.analytics
        if (analytics != null && !uiState.isLoading) {
            item {
                GlowingCard(
                    glowColor = CyberCyan,
                    onClick = onNavigateToAnalyzer
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        AsyncImage(
                            model = analytics.profile.avatarUrl,
                            contentDescription = analytics.profile.displayName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .border(2.dp, NeonViolet, CircleShape)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ACTIVE TELEMETRY",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberCyan
                            )
                            Text(
                                text = analytics.profile.displayName,
                                style = MaterialTheme.typography.headlineMedium,
                                color = TextPrimary
                            )
                            Text(
                                text = "@${analytics.profile.username} • ${analytics.rank.title}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BrightOrchid
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = "Open Analyzer",
                            tint = PlasmaGlow
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MiniDashboardStat("XP", analytics.totalXp.toString())
                        MiniDashboardStat("STARS", formatCompactCount(analytics.profile.totalStars))
                        MiniDashboardStat("REPOS", formatCompactCount(analytics.profile.publicRepos))
                        MiniDashboardStat(
                            "BADGES",
                            "${analytics.achievements.count { it.isUnlocked }}/${analytics.achievements.size}"
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { analytics.rankProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = NeonViolet,
                        trackColor = ObsidianSurface,
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }

        // Quick Module Shortcuts
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "EXPLORE MODULES",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )

                DashboardModuleTile(
                    icon = Icons.Rounded.Analytics,
                    title = "GitHub Profile Analyzer",
                    subtitle = "Inspect stars, followers, repositories & top language stack",
                    accentColor = NeonViolet,
                    onClick = onNavigateToAnalyzer
                )
                DashboardModuleTile(
                    icon = Icons.Rounded.EmojiEvents,
                    title = "XP, Rank & Achievements",
                    subtitle = "Track cosmic developer rank progression & unlocked badges",
                    accentColor = CyberCyan,
                    onClick = onNavigateToAchievements
                )
                DashboardModuleTile(
                    icon = Icons.Rounded.QrCode2,
                    title = "Developer Identity Card",
                    subtitle = "Generate scannable QR profile pass & save PNG to gallery",
                    accentColor = BrightOrchid,
                    onClick = onNavigateToProfile
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.RocketLaunch,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "BYTEVERSE v1.0 • BUILT BY WEST.DEV",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun MiniDashboardStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DashboardModuleTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    GlowingCard(
        glowColor = accentColor,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.16f))
                    .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
