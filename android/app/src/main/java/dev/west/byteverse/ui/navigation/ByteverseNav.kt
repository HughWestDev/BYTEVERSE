package dev.west.byteverse.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.west.byteverse.ui.components.CosmicBackdrop
import dev.west.byteverse.ui.screens.AchievementsScreen
import dev.west.byteverse.ui.screens.AnalyzerScreen
import dev.west.byteverse.ui.screens.HomeScreen
import dev.west.byteverse.ui.screens.ProfileScreen
import dev.west.byteverse.ui.theme.DeepAmethyst
import dev.west.byteverse.ui.theme.ElevatedCard
import dev.west.byteverse.ui.theme.NeonViolet
import dev.west.byteverse.ui.theme.ObsidianSurface
import dev.west.byteverse.ui.theme.PlasmaGlow
import dev.west.byteverse.ui.theme.SubtleBorder
import dev.west.byteverse.ui.theme.TextMuted
import dev.west.byteverse.ui.theme.TextPrimary
import dev.west.byteverse.ui.viewmodel.ByteverseViewModel
import kotlinx.coroutines.launch

enum class ByteverseDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    HOME("home", "Home", Icons.Rounded.Home),
    ANALYZER("analyzer", "Analyzer", Icons.Rounded.Analytics),
    ACHIEVEMENTS("achievements", "XP & Ranks", Icons.Rounded.EmojiEvents),
    PROFILE("profile", "ID Card", Icons.Rounded.Badge)
}

@Composable
fun ByteverseApp(
    viewModel: ByteverseViewModel = viewModel()
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ByteverseDestination.HOME.route

    fun navigateTo(destination: ByteverseDestination) {
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    CosmicBackdrop(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = ElevatedCard,
                        contentColor = TextPrimary,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .border(1.dp, NeonViolet, RoundedCornerShape(14.dp))
                    )
                }
            },
            bottomBar = {
                ByteverseBottomBar(
                    currentRoute = currentRoute,
                    onSelect = ::navigateTo
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(innerPadding)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = ByteverseDestination.HOME.route,
                    enterTransition = { fadeIn(animationSpec = tween(220)) },
                    exitTransition = { fadeOut(animationSpec = tween(180)) }
                ) {
                    composable(ByteverseDestination.HOME.route) {
                        HomeScreen(
                            uiState = uiState,
                            onUsernameChange = viewModel::onUsernameChanged,
                            onAnalyze = { override ->
                                viewModel.analyzeUsername(override)
                            },
                            onDismissError = viewModel::clearError,
                            onNavigateToAnalyzer = { navigateTo(ByteverseDestination.ANALYZER) },
                            onNavigateToAchievements = { navigateTo(ByteverseDestination.ACHIEVEMENTS) },
                            onNavigateToProfile = { navigateTo(ByteverseDestination.PROFILE) }
                        )
                    }

                    composable(ByteverseDestination.ANALYZER.route) {
                        AnalyzerScreen(
                            uiState = uiState,
                            onUsernameChange = viewModel::onUsernameChanged,
                            onAnalyze = { override -> viewModel.analyzeUsername(override) },
                            onDismissError = viewModel::clearError
                        )
                    }

                    composable(ByteverseDestination.ACHIEVEMENTS.route) {
                        AchievementsScreen(
                            uiState = uiState,
                            onFilterSelected = viewModel::setAchievementFilter,
                            onNavigateToHome = { navigateTo(ByteverseDestination.HOME) }
                        )
                    }

                    composable(ByteverseDestination.PROFILE.route) {
                        ProfileScreen(
                            uiState = uiState,
                            onSaveCard = { bitmap, onResult ->
                                viewModel.saveIdentityCard(context, bitmap, onResult)
                            },
                            onShowMessage = { msg ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(msg)
                                }
                            },
                            onNavigateToHome = { navigateTo(ByteverseDestination.HOME) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ByteverseBottomBar(
    currentRoute: String,
    onSelect: (ByteverseDestination) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ObsidianSurface.copy(alpha = 0.96f))
            .navigationBarsPadding()
    ) {
        HorizontalDivider(color = SubtleBorder)
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            ByteverseDestination.entries.forEach { destination ->
                val selected = currentRoute == destination.route
                NavigationBarItem(
                    selected = selected,
                    onClick = { onSelect(destination) },
                    icon = {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = destination.label
                        )
                    },
                    label = {
                        Text(
                            text = destination.label,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PlasmaGlow,
                        selectedTextColor = PlasmaGlow,
                        indicatorColor = DeepAmethyst.copy(alpha = 0.55f),
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )
            }
        }
    }
}
