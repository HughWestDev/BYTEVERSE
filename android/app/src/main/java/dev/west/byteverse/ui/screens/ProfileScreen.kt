package dev.west.byteverse.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.layer.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import dev.west.byteverse.ui.components.CosmicEmptyState
import dev.west.byteverse.ui.components.DeveloperIdentityCard
import dev.west.byteverse.ui.components.GlowingCard
import dev.west.byteverse.ui.theme.BrightOrchid
import dev.west.byteverse.ui.theme.DeepAmethyst
import dev.west.byteverse.ui.theme.ElectricPurple
import dev.west.byteverse.ui.theme.EmeraldPulse
import dev.west.byteverse.ui.theme.NeonViolet
import dev.west.byteverse.ui.theme.PlasmaGlow
import dev.west.byteverse.ui.theme.TextPrimary
import dev.west.byteverse.ui.theme.TextSecondary
import dev.west.byteverse.ui.viewmodel.ByteverseUiState
import dev.west.byteverse.util.IdentityCardExporter
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    uiState: ByteverseUiState,
    onSaveCard: (android.graphics.Bitmap, (Result<android.net.Uri>) -> Unit) -> Unit,
    onShowMessage: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    fun triggerCardCaptureAndSave() {
        coroutineScope.launch {
            runCatching {
                graphicsLayer.toImageBitmap().asAndroidBitmap()
            }.onSuccess { bitmap ->
                onSaveCard(bitmap) { result ->
                    result.onSuccess {
                        onShowMessage("Identity Card saved to Pictures/BYTEVERSE")
                    }.onFailure { err ->
                        onShowMessage(err.localizedMessage ?: "Failed to save Identity Card")
                    }
                }
            }.onFailure { err ->
                onShowMessage(err.localizedMessage ?: "Unable to render Identity Card image")
            }
        }
    }

    val legacyPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            triggerCardCaptureAndSave()
        } else {
            onShowMessage("Storage permission is needed to save the Identity Card on Android 9 and below.")
        }
    }

    fun handleSaveClick() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val permissionState = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (permissionState != PackageManager.PERMISSION_GRANTED) {
                legacyPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                return
            }
        }
        triggerCardCaptureAndSave()
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "DEVELOPER IDENTITY",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary
                )
                Text(
                    text = "Exportable BYTEVERSE pass with scannable GitHub QR code",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        val analytics = uiState.analytics
        if (analytics == null) {
            item {
                CosmicEmptyState(
                    icon = Icons.Rounded.Badge,
                    title = "Identity Card Unavailable",
                    subtitle = "Scan a GitHub username first to mint your futuristic developer identity card and QR pass.",
                    actionLabel = "SCAN A PROFILE",
                    onAction = onNavigateToHome
                )
            }
        } else {
            // Capturable Identity Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawWithContent {
                            graphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawLayer(graphicsLayer)
                        }
                ) {
                    DeveloperIdentityCard(analytics = analytics)
                }
            }

            // Export Actions
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = ::handleSaveClick,
                        enabled = !uiState.isSavingCard,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricPurple,
                            contentColor = Color.White,
                            disabledContainerColor = DeepAmethyst.copy(alpha = 0.5f)
                        )
                    ) {
                        if (uiState.isSavingCard) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = PlasmaGlow,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "SAVING TO GALLERY...",
                                style = MaterialTheme.typography.labelLarge
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Download,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SAVE CARD AS IMAGE",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    val savedUri = uiState.savedCardUri
                    if (savedUri != null) {
                        OutlinedButton(
                            onClick = {
                                IdentityCardExporter.shareSavedCard(
                                    context = context,
                                    imageUri = savedUri,
                                    username = analytics.profile.username
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, NeonViolet),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = PlasmaGlow
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Share,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SHARE SAVED CARD",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldPulse,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Saved in Pictures/BYTEVERSE on your device.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = EmeraldPulse
                            )
                        }
                    }
                }
            }

            // Telemetry Summary
            item {
                GlowingCard {
                    Text(
                        text = "CARD METADATA",
                        style = MaterialTheme.typography.labelLarge,
                        color = PlasmaGlow
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    MetadataLine("GitHub URL", analytics.profile.profileUrl)
                    MetadataLine("Account Age", "${analytics.profile.accountAgeYears} years (since ${analytics.profile.createdYear})")
                    MetadataLine("Total Forks", analytics.profile.totalForks.toString())
                    MetadataLine(
                        "Achievements",
                        "${analytics.achievements.count { it.isUnlocked }} / ${analytics.achievements.size} unlocked"
                    )
                    MetadataLine("Issued By", "BYTEVERSE • WEST.DEV")
                }
            }
        }
    }
}

@Composable
private fun MetadataLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = BrightOrchid
        )
    }
}
