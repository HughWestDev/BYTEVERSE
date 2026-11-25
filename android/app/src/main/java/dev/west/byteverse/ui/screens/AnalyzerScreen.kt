package dev.west.byteverse.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.FolderSpecial
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Workspaces
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.west.byteverse.domain.model.RepositorySummary
import dev.west.byteverse.ui.components.AnimatedMetricTile
import dev.west.byteverse.ui.components.CosmicEmptyState
import dev.west.byteverse.ui.components.CosmicErrorCard
import dev.west.byteverse.ui.components.CosmicLoadingState
import dev.west.byteverse.ui.components.GlowingCard
import dev.west.byteverse.ui.components.LanguageBarRow
import dev.west.byteverse.ui.components.NeonSearchBar
import dev.west.byteverse.ui.components.formatCompactCount
import dev.west.byteverse.ui.theme.AmberFlare
import dev.west.byteverse.ui.theme.BrightOrchid
import dev.west.byteverse.ui.theme.CyberCyan
import dev.west.byteverse.ui.theme.DeepAmethyst
import dev.west.byteverse.ui.theme.ElevatedCard
import dev.west.byteverse.ui.theme.EmeraldPulse
import dev.west.byteverse.ui.theme.NeonViolet
import dev.west.byteverse.ui.theme.PlasmaGlow
import dev.west.byteverse.ui.theme.SubtleBorder
import dev.west.byteverse.ui.theme.TextMuted
import dev.west.byteverse.ui.theme.TextPrimary
import dev.west.byteverse.ui.theme.TextSecondary
import dev.west.byteverse.ui.viewmodel.ByteverseUiState

@Composable
fun AnalyzerScreen(
    uiState: ByteverseUiState,
    onUsernameChange: (String) -> Unit,
    onAnalyze: (String?) -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "PROFILE ANALYZER",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary
                )
                Text(
                    text = "Live GitHub REST API telemetry & language breakdown",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        item {
            GlowingCard {
                NeonSearchBar(
                    value = uiState.usernameInput,
                    onValueChange = onUsernameChange,
                    onSubmit = { onAnalyze(null) },
                    isLoading = uiState.isLoading
                )
            }
        }

        if (uiState.isLoading) {
            item {
                CosmicLoadingState(
                    username = uiState.usernameInput.trim().ifBlank { "developer" }
                )
            }
        } else if (uiState.errorMessage != null) {
            item {
                CosmicErrorCard(
                    message = uiState.errorMessage,
                    onRetry = { onAnalyze(null) },
                    onDismiss = onDismissError
                )
            }
        }

        val analytics = uiState.analytics
        if (analytics == null && !uiState.isLoading) {
            item {
                CosmicEmptyState(
                    icon = Icons.Rounded.Radar,
                    title = "No Developer Scanned Yet",
                    subtitle = "Enter a GitHub username above or run a quick scan on torvalds to inspect repositories, stars, and top languages.",
                    actionLabel = "SCAN @TORVALDS",
                    onAction = { onAnalyze("torvalds") }
                )
            }
        } else if (analytics != null && !uiState.isLoading) {
            val profile = analytics.profile

            // Profile Header Card
            item {
                GlowingCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AsyncImage(
                            model = profile.avatarUrl,
                            contentDescription = "${profile.displayName} avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(78.dp)
                                .clip(CircleShape)
                                .border(2.5.dp, NeonViolet, CircleShape)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile.displayName,
                                style = MaterialTheme.typography.headlineMedium,
                                color = TextPrimary
                            )
                            Text(
                                text = "@${profile.username}",
                                style = MaterialTheme.typography.labelLarge,
                                color = BrightOrchid
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DeepAmethyst.copy(alpha = 0.45f),
                                border = BorderStroke(1.dp, NeonViolet.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "${analytics.rank.badgeCode} • ${analytics.rank.title}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PlasmaGlow,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = profile.bio,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )

                    if (profile.location != null || profile.company != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            profile.location?.let { loc ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Rounded.LocationOn,
                                        contentDescription = null,
                                        tint = CyberCyan,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = loc,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            profile.company?.let { org ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Rounded.Workspaces,
                                        contentDescription = null,
                                        tint = BrightOrchid,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = org,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2x2 Metrics Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AnimatedMetricTile(
                            label = "Followers",
                            value = profile.followers,
                            icon = Icons.Rounded.Group,
                            accentColor = NeonViolet,
                            modifier = Modifier.weight(1f)
                        )
                        AnimatedMetricTile(
                            label = "Following",
                            value = profile.following,
                            icon = Icons.Rounded.PersonAdd,
                            accentColor = CyberCyan,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AnimatedMetricTile(
                            label = "Public Repos",
                            value = profile.publicRepos,
                            icon = Icons.Rounded.FolderSpecial,
                            accentColor = EmeraldPulse,
                            modifier = Modifier.weight(1f)
                        )
                        AnimatedMetricTile(
                            label = "Total Stars",
                            value = profile.totalStars,
                            icon = Icons.Rounded.Star,
                            accentColor = AmberFlare,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Top Programming Languages
            item {
                GlowingCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Code,
                            contentDescription = null,
                            tint = BrightOrchid,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "TOP PROGRAMMING LANGUAGES",
                            style = MaterialTheme.typography.labelLarge,
                            color = PlasmaGlow
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    if (analytics.topLanguages.isEmpty()) {
                        Text(
                            text = "No public repository languages detected for this profile.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            analytics.topLanguages.forEach { stat ->
                                LanguageBarRow(stat = stat)
                            }
                        }
                    }
                }
            }

            // Top Starred Repositories
            if (analytics.topRepositories.isNotEmpty()) {
                item {
                    Text(
                        text = "TOP STARRED REPOSITORIES",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary
                    )
                }

                items(analytics.topRepositories, key = { it.name }) { repo ->
                    RepositoryCard(repo = repo)
                }
            }
        }
    }
}

@Composable
private fun RepositoryCard(repo: RepositorySummary) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = ElevatedCard,
        border = BorderStroke(1.dp, SubtleBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = repo.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = AmberFlare,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = formatCompactCount(repo.stars),
                        style = MaterialTheme.typography.labelMedium,
                        color = AmberFlare
                    )
                }
            }

            if (!repo.description.isNullOrBlank()) {
                Text(
                    text = repo.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!repo.language.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DeepAmethyst.copy(alpha = 0.4f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = repo.language,
                            style = MaterialTheme.typography.labelSmall,
                            color = PlasmaGlow
                        )
                    }
                }
                Text(
                    text = "${formatCompactCount(repo.forks)} forks",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }
    }
}
