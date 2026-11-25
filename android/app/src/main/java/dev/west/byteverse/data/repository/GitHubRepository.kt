package dev.west.byteverse.data.repository

import dev.west.byteverse.data.remote.GitHubApiService
import dev.west.byteverse.data.remote.GitHubClient
import dev.west.byteverse.data.remote.GitHubRepoDto
import dev.west.byteverse.domain.logic.DeveloperStatsCalculator
import dev.west.byteverse.domain.model.DeveloperAnalytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

sealed interface GitHubResult<out T> {
    data class Success<T>(val data: T) : GitHubResult<T>
    data class Error(val message: String, val isRateLimit: Boolean = false) : GitHubResult<Nothing>
}

class GitHubRepository(
    private val api: GitHubApiService = GitHubClient.apiService
) {

    suspend fun analyzeDeveloper(rawUsername: String): GitHubResult<DeveloperAnalytics> {
        val username = rawUsername.trim().removePrefix("@")
        if (username.isBlank()) {
            return GitHubResult.Error("Enter a valid GitHub username to scan.")
        }

        return withContext(Dispatchers.IO) {
            try {
                val userResponse = api.getUser(username)
                if (!userResponse.isSuccessful) {
                    return@withContext when (userResponse.code()) {
                        404 -> GitHubResult.Error(
                            "Developer \"$username\" was not found in the GitHub universe."
                        )
                        403, 429 -> GitHubResult.Error(
                            "GitHub REST API rate limit reached. Wait a few minutes or add GITHUB_TOKEN in local.properties.",
                            isRateLimit = true
                        )
                        else -> GitHubResult.Error(
                            "GitHub API returned error ${userResponse.code()}. Please try again."
                        )
                    }
                }

                val user = userResponse.body()
                    ?: return@withContext GitHubResult.Error("Empty response received from GitHub.")

                val allRepos = mutableListOf<GitHubRepoDto>()
                val maxPages = ((user.publicRepos + 99) / 100).coerceIn(1, 3)

                for (page in 1..maxPages) {
                    val reposResponse = api.getUserRepos(
                        username = username,
                        perPage = 100,
                        page = page
                    )
                    if (!reposResponse.isSuccessful) {
                        if (reposResponse.code() == 403 || reposResponse.code() == 429) {
                            return@withContext GitHubResult.Error(
                                "GitHub REST API rate limit reached while fetching repositories.",
                                isRateLimit = true
                            )
                        }
                        break
                    }
                    val pageItems = reposResponse.body().orEmpty()
                    allRepos.addAll(pageItems)
                    if (pageItems.size < 100) break
                }

                GitHubResult.Success(DeveloperStatsCalculator.analyze(user, allRepos))
            } catch (e: IOException) {
                GitHubResult.Error(
                    "Network connection failed. Check your internet access and try again."
                )
            } catch (e: Exception) {
                GitHubResult.Error(
                    e.localizedMessage ?: "Unexpected error analyzing GitHub profile."
                )
            }
        }
    }
}
