# Task List — Recommendation System Frontend

- `[x]` **1. Data Models**
  - `[x]` 1.1 Tạo `RecommendationResponse.kt`
  - `[x]` 1.2 Cập nhật `UserCreationRequest.kt` thêm `preferredGenreIds`

- `[x]` **2. API Service**
  - `[x]` 2.1 Thêm `getRecommendations()` vào `ApiService.kt`
  - `[x]` 2.2 Thêm `triggerFullPipeline()` vào `ApiService.kt`

- `[x]` **3. Repository**
  - `[x]` 3.1 Thêm method recommendation vào `SongRepository.kt`

- `[x]` **4. ViewModel**
  - `[x]` 4.1 Tạo `RecommendationViewModel.kt`
  - `[x]` 4.2 Tạo `RecommendationViewModelFactory.kt`
  - `[x]` 4.3 Cập nhật `RegisterViewModel.kt` thêm genre selection

- `[x]` **5. UI — Register Screen**
  - `[x]` 5.1 Cập nhật `RegisterScreen.kt` thêm bước chọn genre

- `[x]` **6. UI — Home Page**
  - `[x]` 6.1 Tạo `RecommendationSection.kt` composable
  - `[x]` 6.2 Cập nhật `SongScreen.kt` thêm section gợi ý
  - `[x]` 6.3 Cập nhật `HomePageU.kt` truyền RecommendationViewModel
  - `[x]` 6.4 Cập nhật `UserHomePage.kt` / `ContentScreen`

- `[x]` **7. Navigation**
  - `[x]` 7.1 Cập nhật `RecipeApp.kt` khởi tạo và truyền RecommendationViewModel
