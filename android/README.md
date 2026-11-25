# BYTEVERSE — Native Android Application

**Tagline:** Explore Your Developer Universe  
**Developer:** WEST.DEV  
**Stack:** Kotlin, Jetpack Compose, Material 3, Navigation Compose, Retrofit + OkHttp + Kotlinx Serialization, Coil, ZXing Core, MediaStore API

## Opening & Running in Android Studio

1. Open **Android Studio** (Ladybug / 2024.2.1 or newer recommended).
2. Choose **File → Open** and select the `/android` directory from this repository.
3. Let Gradle sync the dependencies declared in `gradle/libs.versions.toml`.
4. Run the `app` configuration on an Android emulator or physical Android phone (`minSdk 26` / Android 8.0+).

## API & Configuration Notes

### 1. GitHub REST API (No Key Required by Default)
- Public GitHub user profiles and repositories are queried from `https://api.github.com/`.
- Unauthenticated requests allow up to **60 requests/hour per IP**.
- **Optional Higher Rate Limit (5,000 req/hr):**
  1. Copy `local.properties.example` to `local.properties` inside `/android`.
  2. Add a GitHub Personal Access Token (no special scopes needed for public data):
     ```properties
     GITHUB_TOKEN=ghp_your_token_here
     ```
  3. Rebuild the app — `BuildConfig.GITHUB_TOKEN` will automatically attach the `Authorization: Bearer` header via `GitHubClient.kt`.

### 2. Saving the Identity Card Image
- Uses Jetpack Compose `rememberGraphicsLayer()` to capture the `DeveloperIdentityCard` (including avatar and ZXing-generated QR code) into an Android `Bitmap`.
- **Android 10+ (API 29+):** Saves directly to `Pictures/BYTEVERSE` via `MediaStore.Images.Media` scoped storage (no runtime permission prompt needed).
- **Android 8–9 (API 26–28):** Requests `WRITE_EXTERNAL_STORAGE` at runtime when saving, then writes and indexes the PNG into `Pictures/BYTEVERSE`.

## Project Structure

```
android/
├── app/src/main/java/dev/west/byteverse/
│   ├── MainActivity.kt
│   ├── data/
│   │   ├── remote/ (GitHubApiService, GitHubClient, GitHubDtos)
│   │   └── repository/ (GitHubRepository)
│   ├── domain/
│   │   ├── logic/ (DeveloperStatsCalculator)
│   │   └── model/ (DeveloperModels)
│   ├── ui/
│   │   ├── components/ (NeonComponents, IdentityCard, QrCodeGenerator)
│   │   ├── navigation/ (ByteverseNav)
│   │   ├── screens/ (HomeScreen, AnalyzerScreen, AchievementsScreen, ProfileScreen)
│   │   ├── theme/ (Color, Type, Theme)
│   │   └── viewmodel/ (ByteverseViewModel)
│   └── util/ (IdentityCardExporter)
└── gradle/libs.versions.toml
```
