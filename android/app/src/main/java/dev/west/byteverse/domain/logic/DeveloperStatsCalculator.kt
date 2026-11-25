package dev.west.byteverse.domain.logic

import androidx.compose.ui.graphics.Color
import dev.west.byteverse.data.remote.GitHubRepoDto
import dev.west.byteverse.data.remote.GitHubUserDto
import dev.west.byteverse.domain.model.Achievement
import dev.west.byteverse.domain.model.DeveloperAnalytics
import dev.west.byteverse.domain.model.DeveloperProfile
import dev.west.byteverse.domain.model.DeveloperRank
import dev.west.byteverse.domain.model.LanguageStat
import dev.west.byteverse.domain.model.RepositorySummary
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.math.min

object DeveloperStatsCalculator {

    private val languageColors = mapOf(
        "Kotlin" to Color(0xFFA97BFF),
        "Java" to Color(0xFFF59E0B),
        "TypeScript" to Color(0xFF38BDF8),
        "JavaScript" to Color(0xFFFACC15),
        "Python" to Color(0xFF34D399),
        "Rust" to Color(0xFFFB923C),
        "Go" to Color(0xFF22D3EE),
        "C++" to Color(0xFFF472B6),
        "C" to Color(0xFF94A3B8),
        "Swift" to Color(0xFFFB7185),
        "Dart" to Color(0xFF2DD4BF),
        "C#" to Color(0xFFC084FC),
        "Ruby" to Color(0xFFE11D48),
        "Shell" to Color(0xFF4ADE80),
        "HTML" to Color(0xFFF97316),
        "CSS" to Color(0xFF818CF8)
    )

    fun analyze(
        user: GitHubUserDto,
        repos: List<GitHubRepoDto>
    ): DeveloperAnalytics {
        val totalStars = repos.sumOf { it.stargazersCount }
        val totalForks = repos.sumOf { it.forksCount }

        val createdInstant = runCatching { Instant.parse(user.createdAt) }
            .getOrElse { Instant.now() }
        val createdYearInt = ZonedDateTime.ofInstant(createdInstant, ZoneOffset.UTC).year
        val currentYearInt = ZonedDateTime.now(ZoneOffset.UTC).year
        val accountAgeYears = (currentYearInt - createdYearInt).coerceAtLeast(0)

        val profile = DeveloperProfile(
            username = user.login,
            displayName = user.name?.takeIf { it.isNotBlank() } ?: user.login,
            avatarUrl = user.avatarUrl,
            profileUrl = user.htmlUrl,
            bio = user.bio?.takeIf { it.isNotBlank() }
                ?: "Exploring code constellations across the open-source universe.",
            company = user.company?.takeIf { it.isNotBlank() },
            location = user.location?.takeIf { it.isNotBlank() },
            followers = user.followers,
            following = user.following,
            publicRepos = user.publicRepos,
            publicGists = user.publicGists,
            totalStars = totalStars,
            totalForks = totalForks,
            accountAgeYears = accountAgeYears,
            createdYear = createdYearInt.toString()
        )

        val topLanguages = calculateTopLanguages(repos)

        val topRepositories = repos
            .sortedByDescending { it.stargazersCount }
            .take(5)
            .map {
                RepositorySummary(
                    name = it.name,
                    description = it.description,
                    language = it.language,
                    stars = it.stargazersCount,
                    forks = it.forksCount,
                    url = it.htmlUrl
                )
            }

        val baseXp = calculateBaseXp(
            publicRepos = user.publicRepos,
            totalStars = totalStars,
            followers = user.followers,
            languageCount = topLanguages.size,
            accountAgeYears = accountAgeYears
        )

        val achievements = buildAchievements(
            publicRepos = user.publicRepos,
            totalStars = totalStars,
            followers = user.followers,
            languageCount = topLanguages.size,
            accountAgeYears = accountAgeYears,
            totalForks = totalForks
        )

        val bonusXp = achievements.filter { it.isUnlocked }.sumOf { it.xpReward }
        val totalXp = baseXp + bonusXp

        val rank = DeveloperRank.entries.lastOrNull { totalXp >= it.minXp }
            ?: DeveloperRank.INITIATED_NODE
        val nextRankIndex = DeveloperRank.entries.indexOf(rank) + 1
        val nextRank = DeveloperRank.entries.getOrNull(nextRankIndex)

        val rankProgress = if (nextRank == null) {
            1f
        } else {
            val span = (nextRank.minXp - rank.minXp).coerceAtLeast(1)
            ((totalXp - rank.minXp).toFloat() / span.toFloat()).coerceIn(0f, 1f)
        }

        val xpToNextRank = if (nextRank == null) {
            0
        } else {
            (nextRank.minXp - totalXp).coerceAtLeast(0)
        }

        return DeveloperAnalytics(
            profile = profile,
            topLanguages = topLanguages,
            topRepositories = topRepositories,
            totalXp = totalXp,
            rank = rank,
            nextRank = nextRank,
            rankProgress = rankProgress,
            xpToNextRank = xpToNextRank,
            achievements = achievements
        )
    }

    private fun calculateTopLanguages(repos: List<GitHubRepoDto>): List<LanguageStat> {
        val counts = repos
            .mapNotNull { it.language?.takeIf { lang -> lang.isNotBlank() } }
            .groupingBy { it }
            .eachCount()

        val total = counts.values.sum()
        if (total == 0) return emptyList()

        return counts.entries
            .sortedByDescending { it.value }
            .take(6)
            .map { (lang, count) ->
                LanguageStat(
                    name = lang,
                    repoCount = count,
                    percentage = count.toFloat() / total.toFloat(),
                    color = languageColors[lang] ?: Color(0xFFC084FC)
                )
            }
    }

    private fun calculateBaseXp(
        publicRepos: Int,
        totalStars: Int,
        followers: Int,
        languageCount: Int,
        accountAgeYears: Int
    ): Int {
        val repoXp = min(publicRepos, 200) * 20
        val starXp = min(totalStars, 2000) * 4
        val followerXp = min(followers, 1000) * 5
        val languageXp = languageCount * 120
        val veteranXp = accountAgeYears * 150
        return repoXp + starXp + followerXp + languageXp + veteranXp
    }

    private fun buildAchievements(
        publicRepos: Int,
        totalStars: Int,
        followers: Int,
        languageCount: Int,
        accountAgeYears: Int,
        totalForks: Int
    ): List<Achievement> {
        return listOf(
            Achievement(
                id = "first_orbit",
                title = "First Orbit",
                description = "Publish at least 1 public repository on GitHub.",
                xpReward = 100,
                isUnlocked = publicRepos >= 1,
                progress = (publicRepos / 1f).coerceIn(0f, 1f),
                progressLabel = "${min(publicRepos, 1)} / 1 repos",
                category = "REPOS"
            ),
            Achievement(
                id = "repo_forge",
                title = "Repo Forge",
                description = "Maintain 15 or more public repositories.",
                xpReward = 250,
                isUnlocked = publicRepos >= 15,
                progress = (publicRepos / 15f).coerceIn(0f, 1f),
                progressLabel = "${min(publicRepos, 15)} / 15 repos",
                category = "REPOS"
            ),
            Achievement(
                id = "polyglot",
                title = "Cosmic Polyglot",
                description = "Ship repositories across 3 or more programming languages.",
                xpReward = 200,
                isUnlocked = languageCount >= 3,
                progress = (languageCount / 3f).coerceIn(0f, 1f),
                progressLabel = "${min(languageCount, 3)} / 3 languages",
                category = "STACK"
            ),
            Achievement(
                id = "starlight",
                title = "Starlight Signal",
                description = "Earn 10+ total stars across your repositories.",
                xpReward = 200,
                isUnlocked = totalStars >= 10,
                progress = (totalStars / 10f).coerceIn(0f, 1f),
                progressLabel = "${min(totalStars, 10)} / 10 stars",
                category = "IMPACT"
            ),
            Achievement(
                id = "constellation",
                title = "Constellation",
                description = "Accumulate 100+ stars across your open-source work.",
                xpReward = 450,
                isUnlocked = totalStars >= 100,
                progress = (totalStars / 100f).coerceIn(0f, 1f),
                progressLabel = "${min(totalStars, 100)} / 100 stars",
                category = "IMPACT"
            ),
            Achievement(
                id = "supernova",
                title = "Supernova Core",
                description = "Surpass 1,000 total repository stars.",
                xpReward = 900,
                isUnlocked = totalStars >= 1000,
                progress = (totalStars / 1000f).coerceIn(0f, 1f),
                progressLabel = "${min(totalStars, 1000)} / 1000 stars",
                category = "IMPACT"
            ),
            Achievement(
                id = "beacon",
                title = "Gravity Beacon",
                description = "Attract 50+ followers in the GitHub universe.",
                xpReward = 350,
                isUnlocked = followers >= 50,
                progress = (followers / 50f).coerceIn(0f, 1f),
                progressLabel = "${min(followers, 50)} / 50 followers",
                category = "SOCIAL"
            ),
            Achievement(
                id = "fork_engine",
                title = "Fork Reactor",
                description = "Inspire 25+ forks of your repositories.",
                xpReward = 300,
                isUnlocked = totalForks >= 25,
                progress = (totalForks / 25f).coerceIn(0f, 1f),
                progressLabel = "${min(totalForks, 25)} / 25 forks",
                category = "IMPACT"
            ),
            Achievement(
                id = "veteran",
                title = "Deep Space Veteran",
                description = "Active GitHub presence for 3+ years.",
                xpReward = 250,
                isUnlocked = accountAgeYears >= 3,
                progress = (accountAgeYears / 3f).coerceIn(0f, 1f),
                progressLabel = "${min(accountAgeYears, 3)} / 3 years",
                category = "LEGACY"
            )
        )
    }
}
