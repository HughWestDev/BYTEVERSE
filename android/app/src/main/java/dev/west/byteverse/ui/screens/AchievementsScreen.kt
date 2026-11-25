package dev.west.byteverse.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.west.byteverse.domain.model.Achievement
import dev.west.byteverse.ui.components.CosmicEmptyState
import dev.west.byteverse.ui.components.GlowingCard
import dev.west.byteverse.ui.theme.BrightOrchid
import dev.west.byteverse.ui.theme.CyberCyan
import dev.west.byteverse.ui.theme.DeepAmethyst
import dev.west.byteverse.ui.theme.ElectricPurple
import dev.west.byteverse.ui.theme.ElevatedCard
import dev.west.byteverse.ui.theme.EmeraldPulse
import dev.west.byteverse.ui.theme.NeonViolet
import dev.west.byteverse.ui.theme.ObsidianSurface
import dev.west.byteverse.ui.theme.PlasmaGlow
import dev.west.byteverse.ui.theme.SubtleBorder
import dev.west.byteverse.ui.theme.TextMuted
import dev.west.byteverse.ui.theme.TextPrimary
import dev.west.byteverse.ui.theme.TextSecondary
import dev.west.byteverse.ui.viewmodel.AchievementFilter
import dev.west.byteverse.ui.viewmodel.ByteverseUiState

@Composable
fun AchievementsScreen(
    uiState: ByteverseUiState,
    onFilterSelected: (AchievementFilter) -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val analytics = uiState.analytics

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "XP & ACHIEVEMENTS",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary
                )
                Text(
                    text = "Cosmic developer rank progression and milestone badges",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        if (analytics == null) {
            item {
                CosmicEmptyState(
                    icon = Icons.Rounded.EmojiEvents,
                    title = "No XP Telemetry Loaded",
                    subtitle = "Analyze a GitHub profile first to calculate developer XP, cosmic rank, and achievement unlocks.",
                    actionLabel = "SCAN A PROFILE",
                    onAction = onNavigateToHome
                )
            }
        } else {
            val animatedRankProgress by animateFloatAsState(
                targetValue = analytics.rankProgress,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
                label = "rank_xp_progress"
            )
            val unlockedCount = analytics.achievements.count { it.isUnlocked }

            // Rank & XP Card
            item {
                GlowingCard(glowColor = ElectricPurple) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${analytics.rank.badgeCode} • CURRENT RANK",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberCyan
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = analytics.rank.title,
                                style = MaterialTheme.typography.headlineMedium,
                                color = TextPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(ElectricPurple, DeepAmethyst)
                                    )
                                )
                                .border(1.dp, PlasmaGlow.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "${analytics.totalXp} XP",
                                style = MaterialTheme.typography.labelLarge,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = analytics.rank.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    LinearProgressIndicator(
                        progress = { animatedRankProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = NeonViolet,
                        trackColor = ObsidianSurface,
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (analytics.nextRank != null) {
                                "${analytics.xpToNextRank} XP to ${analytics.nextRank.title}"
                            } else {
                                "Maximum Cosmic Rank Reached"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = BrightOrchid
                        )
                        Text(
                            text = "$unlockedCount / ${analytics.achievements.size} Unlocked",
                            style = MaterialTheme.typography.labelMedium,
                            color = EmeraldPulse
                        )
                    }
                }
            }

            // Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AchievementFilter.entries.forEach { filter ->
                        val selected = uiState.achievementFilter == filter
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) ElectricPurple else ElevatedCard,
                            border = BorderStroke(
                                1.dp,
                                if (selected) PlasmaGlow.copy(alpha = 0.7f) else SubtleBorder
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onFilterSelected(filter) }
                        ) {
                            Text(
                                text = filter.label.uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selected) TextPrimary else TextSecondary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                            )
                        }
                    }
                }
            }

            val filtered = when (uiState.achievementFilter) {
                AchievementFilter.ALL -> analytics.achievements
                AchievementFilter.UNLOCKED -> analytics.achievements.filter { it.isUnlocked }
                AchievementFilter.LOCKED -> analytics.achievements.filter { !it.isUnlocked }
            }

            if (filtered.isEmpty()) {
                item {
                    GlowingCard {
                        Text(
                            text = "No achievements match this filter yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                items(filtered, key = { it.id }) { achievement ->
                    AchievementCard(achievement = achievement)
                }
            }
        }
    }
}

@Composable
private fun AchievementCard(achievement: Achievement) {
    val accent = if (achievement.isUnlocked) EmeraldPulse else TextMuted
    val border = if (achievement.isUnlocked) NeonViolet.copy(alpha = 0.65f) else SubtleBorder

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = ElevatedCard,
        border = BorderStroke(1.dp, border)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (achievement.isUnlocked) {
                                DeepAmethyst.copy(alpha = 0.55f)
                            } else {
                                ObsidianSurface
                            }
                        )
                        .border(1.dp, accent.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (achievement.isUnlocked) {
                            Icons.Rounded.CheckCircle
                        } else {
                            Icons.Rounded.Lock
                        },
                        contentDescription = null,
                        tint = if (achievement.isUnlocked) EmeraldPulse else TextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = achievement.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = achievement.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ObsidianSurface,
                    border = BorderStroke(1.dp, NeonViolet.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = BrightOrchid,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+${achievement.xpReward}",
                            style = MaterialTheme.typography.labelSmall,
                            color = PlasmaGlow
                        )
                    }
                }
            }

            LinearProgressIndicator(
                progress = { achievement.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (achievement.isUnlocked) EmeraldPulse else NeonViolet,
                trackColor = ObsidianSurface,
                strokeCap = StrokeCap.Round
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = achievement.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Text(
                    text = achievement.progressLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (achievement.isUnlocked) EmeraldPulse else TextSecondary
                )
            }
        }
    }
}
