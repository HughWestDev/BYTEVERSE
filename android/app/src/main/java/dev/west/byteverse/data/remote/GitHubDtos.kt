package dev.west.byteverse.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubUserDto(
    val id: Long,
    val login: String,
    val name: String? = null,
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("html_url") val htmlUrl: String,
    val bio: String? = null,
    val company: String? = null,
    val location: String? = null,
    val blog: String? = null,
    val followers: Int = 0,
    val following: Int = 0,
    @SerialName("public_repos") val publicRepos: Int = 0,
    @SerialName("public_gists") val publicGists: Int = 0,
    @SerialName("created_at") val createdAt: String = ""
)

@Serializable
data class GitHubRepoDto(
    val id: Long,
    val name: String,
    @SerialName("full_name") val fullName: String,
    val description: String? = null,
    @SerialName("html_url") val htmlUrl: String,
    val language: String? = null,
    @SerialName("stargazers_count") val stargazersCount: Int = 0,
    @SerialName("forks_count") val forksCount: Int = 0,
    val fork: Boolean = false,
    @SerialName("updated_at") val updatedAt: String = ""
)
