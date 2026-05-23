# Kế Hoạch Frontend — Hệ Thống Gợi Ý Nhạc

## Tổng Quan

Thêm giao diện frontend (Android - Jetpack Compose) để tích hợp với backend hệ thống gợi ý nhạc Item-Based Collaborative Filtering đã triển khai. 

## Những Gì Cần Làm

### 1. Data Models (Response)

#### [NEW] `RecommendationResponse.kt`
```kotlin
data class RecommendationResponse(
    val source: String,       // "PERSONALIZED" | "COLD_START_GENRE" | "COLD_START_GLOBAL"
    val totalCount: Int,
    val songs: List<Song>
)
```

#### [MODIFY] `UserCreationRequest.kt`
Thêm trường `preferredGenreIds: List<String>? = null` để gửi genre khi đăng ký.

---

### 2. API Service

#### [MODIFY] `ApiService.kt`
Thêm 3 endpoint mới:
```kotlin
// Lấy gợi ý nhạc cá nhân hóa
@GET("api/recommendations")
suspend fun getRecommendations(
    @Query("userId") userId: String,
    @Query("limit") limit: Int = 10
): Response<ApiResponse<RecommendationResponse>>

// Admin trigger full pipeline
@POST("api/admin/recommendations/trigger-full-pipeline")
suspend fun triggerFullPipeline(): Response<ApiResponse<String>>

// Admin trigger aggregation
@POST("api/admin/recommendations/trigger-aggregation")
suspend fun triggerAggregation(): Response<ApiResponse<String>>
```

---

### 3. Repository

#### [MODIFY] `SongRepository.kt`
Thêm các method gọi recommendation API.

#### [MODIFY] `UserRepository.kt`
Thêm method `createUser` có `preferredGenreIds`.

---

### 4. ViewModel

#### [NEW] `RecommendationViewModel.kt`
ViewModel quản lý trạng thái danh sách gợi ý:
- `getRecommendations(userId, limit)` — gọi API lấy gợi ý
- `RecommendationState` — isLoading, recommendations, source, error

#### [NEW] `RecommendationViewModelFactory.kt`

#### [MODIFY] `RegisterViewModel.kt`
- Thêm `selectedGenreIds: List<String>` vào `RegisterUiState`
- Thêm `toggleGenre(genreId: String)` để user chọn/bỏ chọn genre
- Truyền `preferredGenreIds` vào `UserCreationRequest` khi gọi `register()`
- Thêm `genres: List<Genre>` vào state để hiển thị danh sách chọn

---

### 5. UI — Màn Hình Đăng Ký

#### [MODIFY] `RegisterScreen.kt`
Thêm **bước 2 chọn thể loại yêu thích** sau khi điền thông tin cơ bản:
- Hiển thị danh sách Genre dưới dạng chip có thể chọn nhiều
- Bố cục 2 cột với `LazyVerticalGrid` hoặc `FlowRow`
- Design: chip có gradient khi được chọn, viền mờ khi chưa chọn
- Nút "Bỏ qua" (skip) → đăng ký không có genre
- Nút "Đăng ký" → đăng ký với genre đã chọn
- Gọi `songViewModel.getGenres()` khi mở màn hình để load danh sách

**Luồng UI:**
```
Trang 1: Nhập username, password, họ tên, ngày sinh
    ↓ Nhấn "Tiếp theo"
Trang 2: Chọn thể loại yêu thích (multi-select chips)
    ↓ Nhấn "Đăng ký" hoặc "Bỏ qua"
→ Điều hướng về LoginScreen
```

---

### 6. UI — Section Gợi Ý Trên Trang Chủ

#### [MODIFY] `SongScreen.kt`
Thêm section **"Gợi ý cho bạn"** (recommendation) vào `LazyColumn`:
- Đặt ở **đầu** danh sách (sau MoodFilterBar)
- Hiển thị badge nhỏ: `PERSONALIZED` → ✨ "Dành riêng cho bạn" | `COLD_START_GENRE` → 🎵 "Theo sở thích" | `COLD_START_GLOBAL` → 🔥 "Đang thịnh hành"
- Sử dụng `LazyRow` hiển thị các `SongItem` giống các section hiện tại
- Loading skeleton khi đang tải
- Ẩn section nếu danh sách rỗng

#### [MODIFY] `HomePageU.kt` / `UserHomePage.kt` / `ContentScreen`
Truyền `RecommendationViewModel` vào chuỗi composable.

#### [MODIFY] `RecipeApp.kt`
- Khởi tạo `RecommendationViewModel` tại `UserHomePage` composable
- Gọi `recommendationViewModel.getRecommendations(userId, 10)` trong `LaunchedEffect`

---

### 7. Navigation & Screen

#### [MODIFY] `Screen.kt`
Không cần route mới — recommendation hiển thị inline trên trang chủ.

---

## Verification Plan

### Build
- `./gradlew compileDebugKotlin` — đảm bảo compile clean

### Manual Testing
1. Đăng ký user mới → chọn genre → xác nhận `preferredGenreIds` được gửi lên backend
2. Mở trang chủ → kiểm tra section "Gợi ý cho bạn" xuất hiện với đúng `source` badge
3. User mới (cold start) → phải thấy source `COLD_START_GENRE` hoặc `COLD_START_GLOBAL`
4. User cũ (có lịch sử) → phải thấy source `PERSONALIZED`

---

## Open Questions

> [!IMPORTANT]
> **Q1: Vị trí endpoint trong ApiService?** Backend dùng prefix `/identity/api/recommendations` nhưng base URL của app là `http://10.0.2.2:8080/identity/`. Tôi sẽ dùng `api/recommendations` (tương đối với base URL). Xác nhận base URL trong `ApiClient.kt`?

> [!IMPORTANT]
> **Q2: Ai là `userId`?** `getUserInfo()` trả về `UserResponse` — field nào là ID? Tôi thấy `UserResponse.kt` có trường gì cần kiểm tra. Cần xác nhận tên field id trong `UserResponse`.

> [!NOTE]
> **Q3: Register screen flow** — Hiện tại sau khi đăng ký thành công app điều hướng về LoginScreen. Luồng 2 bước mới vẫn kết thúc ở LoginScreen. Có muốn auto-login sau đăng ký không?

> [!NOTE]
> **Q4: Recommendation section** — Đặt ở đâu trong `SongScreen`? Hiện tại thứ tự là: MoodFilterBar → Gợi ý bài hát → Album Hot → Playlist → Bài hát gần đây → ZingChart. Tôi sẽ đặt **TRÊN CÙNG** (trước MoodFilterBar) để nổi bật nhất.
