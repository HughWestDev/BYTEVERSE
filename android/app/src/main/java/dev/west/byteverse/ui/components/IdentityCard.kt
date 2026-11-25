package dev.west.byteverse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.west.byteverse.domain.model.DeveloperAnalytics
import dev.west.byteverse.ui.theme.BrightOrchid
import dev.west.byteverse.ui.theme.CyberCyan
import dev.west.byteverse.ui.theme.DeepAmethyst
import dev.west.byteverse.ui.theme.ElectricPurple
import dev.west.byteverse.ui.theme.NeonViolet
import dev.west.byteverse.ui.theme.ObsidianSurface
import dev.west.byteverse.ui.theme.PlasmaGlow
import dev.west.byteverse.ui.theme.SubtleBorder
import dev.west.byteverse.ui.theme.TextMuted
import dev.west.byteverse.ui.theme.TextPrimary
import dev.west.byteverse.ui.theme.TextSecondary

@Composable
fun DeveloperIdentityCard(
    analytics: DeveloperAnalytics,
    modifier: Modifier = Modifier
) {
    val profile = analytics.profile
    val primaryLang = analytics.topLanguages.firstOrNull()?.name ?: "Polyglot"
    val context = LocalContext.current

    val cardGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0A0814),
            Color(0xFF170D2E),
            Color(0xFF08070F)
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(cardGradient)
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(NeonViolet, CyberCyan.copy(alpha = 0.7f), ElectricPurple)
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Top Identity Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "BYTEVERSE // ID",
                        style = MaterialTheme.typography.labelLarge,
                        color = PlasmaGlow
                    )
                    Text(
                        text = "WEST.DEV UNIVERSE PASS",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DeepAmethyst.copy(alpha = 0.55f))
                        .border(1.dp, NeonViolet.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = analytics.rank.badgeCode,
                        style = MaterialTheme.typography.labelMedium,
                        color = PlasmaGlow
                    )
                }
            }

            // Profile Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(profile.avatarUrl)
                        .allowHardware(false)
                        .crossfade(true)
                        .build(),
                    contentDescription = "${profile.displayName} avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(2.dp, NeonViolet, CircleShape)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = profile.displayName,
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "@${profile.username}",
                        style = MaterialTheme.typography.labelLarge,
                        color = BrightOrchid
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = analytics.rank.title.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = CyberCyan
                    )
                }
            }

            Text(
                text = profile.bio,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            HorizontalDivider(color = SubtleBorder)

            // Metrics + QR Code Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CardStatItem(
                            label = "STARS",
                            value = formatCompactCount(profile.totalStars),
                            modifier = Modifier.weight(1f)
                        )
                        CardStatItem(
                            label = "REPOS",
                            value = formatCompactCount(profile.publicRepos),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CardStatItem(
                            label = "FOLLOWERS",
                            value = formatCompactCount(profile.followers),
                            modifier = Modifier.weight(1f)
                        )
                        CardStatItem(
                            label = "TOP STACK",
                            value = primaryLang,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ObsidianSurface)
                            .border(1.dp, SubtleBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "XP ${analytics.totalXp}  •  SINCE ${profile.createdYear}",
                            style = MaterialTheme.typography.labelSmall,
                            color = PlasmaGlow
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    DeveloperQrCode(
                        url = profile.profileUrl,
                        size = 96.dp
                    )
                    Text(
                        text = "SCAN GITHUB",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun CardStatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
