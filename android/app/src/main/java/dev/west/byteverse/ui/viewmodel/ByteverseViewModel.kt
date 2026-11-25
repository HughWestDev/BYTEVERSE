package dev.west.byteverse.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.west.byteverse.data.repository.GitHubRepository
import dev.west.byteverse.data.repository.GitHubResult
import dev.west.byteverse.domain.model.DeveloperAnalytics
import dev.west.byteverse.util.IdentityCardExporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AchievementFilter(val label: String) {
    ALL("All"),
    UNLOCKED("Unlocked"),
    LOCKED("Locked")
}

data class ByteverseUiState(
    val usernameInput: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRateLimitError: Boolean = false,
    val analytics: DeveloperAnalytics? = null,
    val achievementFilter: AchievementFilter = AchievementFilter.ALL,
    val isSavingCard: Boolean = false,
    val savedCardUri: Uri? = null,
    val recentSearches: List<String> = listOf("torvalds", "JakeWharton", "chrisbanes", "octocat")
)

class ByteverseViewModel(
    private val repository: GitHubRepository = GitHubRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ByteverseUiState())
    val uiState: StateFlow<ByteverseUiState> = _uiState.asStateFlow()

    fun onUsernameChanged(newValue: String) {
        _uiState.update { state ->
            state.copy(
                usernameInput = newValue,
                errorMessage = null
            )
        }
    }

    fun analyzeUsername(overrideUsername: String? = null) {
        val target = (overrideUsername ?: _uiState.value.usernameInput)
            .trim()
            .removePrefix("@")

        if (target.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Please enter a GitHub username first.")
            }
            return
        }

        _uiState.update {
            it.copy(
                usernameInput = target,
                isLoading = true,
                errorMessage = null,
                isRateLimitError = false,
                savedCardUri = null
            )
        }

        viewModelScope.launch {
            when (val result = repository.analyzeDeveloper(target)) {
                is GitHubResult.Success -> {
                    _uiState.update { state ->
                        val updatedRecents = (listOf(result.data.profile.username) +
                            state.recentSearches.filterNot {
                                it.equals(result.data.profile.username, ignoreCase = true)
                            }).take(6)

                        state.copy(
                            isLoading = false,
                            errorMessage = null,
                            isRateLimitError = false,
                            analytics = result.data,
                            recentSearches = updatedRecents
                        )
                    }
                }
                is GitHubResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message,
                            isRateLimitError = result.isRateLimit
                        )
                    }
                }
            }
        }
    }

    fun setAchievementFilter(filter: AchievementFilter) {
        _uiState.update { it.copy(achievementFilter = filter) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null, isRateLimitError = false) }
    }

    fun saveIdentityCard(
        context: Context,
        bitmap: Bitmap,
        onResult: (Result<Uri>) -> Unit
    ) {
        val username = _uiState.value.analytics?.profile?.username ?: "developer"
        _uiState.update { it.copy(isSavingCard = true) }

        viewModelScope.launch {
            val result = IdentityCardExporter.saveCardToGallery(
                context = context,
                bitmap = bitmap,
                username = username
            )
            _uiState.update { state ->
                state.copy(
                    isSavingCard = false,
                    savedCardUri = result.getOrNull() ?: state.savedCardUri
                )
            }
            onResult(result)
        }
    }
}
