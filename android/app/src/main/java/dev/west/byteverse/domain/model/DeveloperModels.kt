package dev.west.byteverse.domain.model

import androidx.compose.ui.graphics.Color

data class DeveloperProfile(
    val username: String,
    val displayName: String,
    val avatarUrl: String,
    val profileUrl: String,
    val bio: String,
    val company: String?,
    val location: String?,
    val followers: Int,
    val following: Int,
    val publicRepos: Int,
    val publicGists: Int,
    val totalStars: Int,
    val totalForks: Int,
    val accountAgeYears: Int,
    val createdYear: String
)

data class LanguageStat(
    val name: String,
    val repoCount: Int,
    val percentage: Float,
    val color: Color
)

data class RepositorySummary(
    val name: String,
    val description: String?,
    val language: String?,
    val stars: Int,
    val forks: Int,
    val url: String
)

enum class DeveloperRank(
    val title: String,
    val minXp: Int,
    val maxXp: Int,
    val badgeCode: String,
    val description: String
) {
    INITIATED_NODE(
        title = "Initiated Node",
        minXp = 0,
        maxXp = 500,
        badgeCode = "LVL-01",
        description = "Entry signal detected in the developer cosmos."
    ),
    ORBIT_CODER(
        title = "Orbit Coder",
        minXp = 500,
        maxXp = 1500,
        badgeCode = "LVL-02",
        description = "Consistent commits establishing gravitational pull."
    ),
    NEBULA_ARCHITECT(
        title = "Nebula Architect",
        minXp = 1500,
        maxXp = 4000,
        badgeCode = "LVL-03",
        description = "Multi-repository systems shaping open-source clusters."
    ),
    QUANTUM_ENGINEER(
        title = "Quantum Engineer",
        minXp = 4000,
        maxXp = 10000,
        badgeCode = "LVL-04",
        description = "High-impact codebases radiating across developer networks."
    ),
    SINGULARITY_CORE(
        title = "Singularity Core",
        minXp = 10000,
        maxXp = Int.MAX_VALUE,
        badgeCode = "LVL-05",
        description = "Apex cosmic contributor warping the open-source universe."
    )
}

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val xpReward: Int,
    val isUnlocked: Boolean,
    val progress: Float,
    val progressLabel: String,
    val category: String
)

data class DeveloperAnalytics(
    val profile: DeveloperProfile,
    val topLanguages: List<LanguageStat>,
    val topRepositories: List<RepositorySummary>,
    val totalXp: Int,
    val rank: DeveloperRank,
    val nextRank: DeveloperRank?,
    val rankProgress: Float,
    val xpToNextRank: Int,
    val achievements: List<Achievement>
)
