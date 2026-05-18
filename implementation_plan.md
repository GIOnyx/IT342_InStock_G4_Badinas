# Android Feature Enhancements

This implementation plan covers the mobile UI enhancements tailored to the Spring Boot backend modifications we recently finalized.

## Proposed Changes

### 1. Admin Dashboard (`AdminActivity`)
- **[NEW] `com.example.instock.features.admin.AdminActivity.kt`**: Activity that retrieves statistics via Retrofit and binds them to the UI. Includes RBAC check logic in `onCreate` via `TokenManager` (kick to `LoginActivity` if `user.role` is not `ADMIN`).
- **[NEW] `com.example.instock.features.admin.AdminApiService.kt`**: Retrofit interface defining `GET /api/v1/admin/stats`.
- **[MODIFY] `com.example.instock.core.network.ApiClient.kt`**: Add `adminApi` instance initialization lazy block.
- **[NEW] `activity_admin.xml`**: Layout featuring modern `CardView` elements displaying `Total Users`, `Total Pantry Items`, and `Total Favorites`.

### 2. Enhanced Mobile Pantry (`PantryFragment`)
- **[MODIFY] `fragment_pantry.xml`**: Add a `ProgressBar` (spinner) for loading state, and a `LinearLayout` empty state view (icon + text "Pantry is Empty").
- **[MODIFY] `com.example.instock.features.pantry.PantryFragment.kt`**:
    - Implement `ItemTouchHelper.SimpleCallback` attached to the RecyclerView for **Swipe-to-Delete** gestures. Replaces the click callback on the adapter.
    - Setup state management: Show Spinner during network fetch. If payload is empty, hide `rvPantry` and show the Empty View.
- **[MODIFY] `com.example.instock.features.pantry.PantryAdapter.kt`**: Ensure the UI updates dynamically and cleanly when items vanish during the swipe constraint.

### 3. Recipe Discovery & Detail View
- **[MODIFY] `com.example.instock.features.recipes.RecipesFragment.kt`**:
    - Add a "Suggest from Pantry" button fetching the user's pantry from `OfflineCache.loadPantry()`, formatting it as a comma-separated string, and invoking `searchByIngredients` on `RecipeApiService`.
- **[NEW] `com.example.instock.features.recipes.RecipeDetailActivity.kt`**:
    - Activity to display Recipe Detail view. Uses `RecipeApiService.getRecipeDetailSuspend(id)`.
    - Parses `RecipeDetailDTO` to show "Ingredients You Have" (green checks) and "Missing Ingredients" (red crosses) similar to the web React equivalent.
- **[NEW] `activity_recipe_detail.xml`**: Layout specifying an `ImageView` header with title, summary, and lists for missing and matched ingredients.
- **[MODIFY] `fragment_recipes.xml`**: Incorporate the "Suggest from Pantry" button adjacent to the search bar.

## User Review Required

> [!WARNING]  
> Are there specific color schemes or icons (e.g. from Material Icons) you want used for the "Pantry is Empty" state or the Swipe-to-Delete background color? I'll default to standard Material Design guidelines (red background with a trash icon for delete) if left unspecified.

## Verification Plan

### Automated/Manual Testing
1. Boot up the mobile app locally into an emulator.
2. Login with standard USER account and attempt to spoof access to `AdminActivity` — verify redirection.
3. Login as ADMIN and examine dashboard cards and data synchronization.
4. Interact with the Pantry — test Swipe-to-Delete, loading spinner behavior on a slow connection, and whether empty state appears.
5. In Recipes, tap "Suggest from Pantry", select a recipe, and verify the Missing vs. Owned ingredient lists in the Detail Activity.
