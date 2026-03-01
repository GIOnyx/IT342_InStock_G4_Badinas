
---
System Design Document (SDD)

Project Title: InStock
---
## 1.0 EXECUTIVE SUMMARY & INTRODUCTION

### 1.1 Project Overview & Purpose

**InStock is an application aimed at reducing food waste by enabling users to select ingredients they currently have in stock, and dynamically suggesting recipes that match those selected ingredients**. **The system includes a Spring Boot backend API, a React web application, and an Android mobile application, all integrated to provide a seamless, multi-platform experience**.

### 1.2 Objectives

1. **Develop a fully functional recipe suggestion MVP with secure authentication, ingredient list management, a recipe catalog, and intelligent recipe suggestion capabilities**.
2. **Implement a professional three-tier architecture utilizing Spring Boot (backend), React (web), and Android (mobile)**.
3. **Create secure RESTful APIs utilizing JWT for session management**.
4. **Integrate mandatory system requirements including Google OAuth, SMTP for email, file uploading, and external API consumption for recipe generation**.
5. **Deploy all system components to production-ready environments**.

### 1.3 Scope

**Included Features:** User registration/authentication (Email & Google OAuth 2.0), Ingredient listing, "Add" to digital pantry, Persistent Allergy/Dietary Filtering, Recipe suggestions via External API, "Add to favorites", File upload, Email notifications (SMTP), Role-Based Access Control, Relational database (min 5 tables), and Responsive Web/Native Mobile apps .
**Excluded Features:** Nutritional information, AI generation, Real-time recipe creation, "Create your own recipe" manual functionality .

---

## 2.0 FUNCTIONAL REQUIREMENTS SPECIFICATION

### 2.1 Core User Journeys

**Journey 1: First-time User Recipe Discovery**

1. **User registers an account (via standard email or Google OAuth)**.
2. **System sends a welcome email via SMTP**.
3. **User sets dietary preferences (e.g., selects "Peanuts" and "Shellfish" from Allergy Filter)**.
4. **User navigates to "My Pantry" and searches/adds ingredients they currently own**.
5. User clicks "Suggest Recipes". **System calls an external public API, matching ingredients while excluding saved allergens, and displays suggested recipes**.
6. **User saves a favored recipe to their "Favorites" list**.

**Journey 2: Returning Customer Management**

1. **User logs in with existing credentials**.
2. **User uploads a profile picture**.
3. **User removes depleted ingredients from stock, generates new recipes, and views "Favorites" **.

**Journey 3: Administrator System Management**

1. **Admin logs in with administrative credentials (triggering RBAC validation)**.
2. **Admin manages the master ingredient list (Adds/deletes ingredients)**.
3. **Admin views system usage statistics**.

### 2.2 Feature List (MoSCoW)

* **MUST HAVE:** User auth (JWT/OAuth), RBAC (Admin vs User), Ingredient catalog & pantry management (CRUD), Recipe suggestion engine (Spoonacular API), Favorites management, SMTP Email, File upload, Persistent Allergy Filtering .
* **SHOULD HAVE:** Ingredient categories/filtering, Input validation, Responsive design .
* **COULD HAVE:** User dashboard, Password reset flow, Nutritional info .
* **WON'T HAVE:** AI features, Real-time creation, Payment gateway .

### 2.3 Acceptance Criteria

* **AC-1 (Successful Recipe Suggestion):** Given a logged-in user with ingredients, when clicking "Suggest Recipes", the system consumes the external API and displays recipes using selected ingredients while filtering out allergens .
* **AC-2 (Adding to Favorites):** Given a suggested recipe, clicking "Add to Favorites" saves details to DB, shows a success notification, and appears in "My Favorites" .
* **AC-3 (RBAC):** Given a Regular User, accessing Admin endpoints returns a 403 Forbidden and a UI access denied message .
* **AC-4 (Persistent Allergy Filtering):** Given selected allergens, logging out and back in keeps the filter active, hides unsafe ingredients, and includes exclusion parameters in the External API call .

---

## 3.0 NON-FUNCTIONAL REQUIREMENTS

* **Performance:** API response time <= 10 seconds. Database queries complete within 500ms using proper indexing. **Mobile app cold start <= 30 seconds **.
* **Security:** Stateless JWT token authentication. Password Storage hashed using BCrypt. Data Transfer Objects (DTOs) used to prevent sensitive data exposure. **Role Verification at the controller layer **.
* **Compatibility:** Web Browsers (Chrome, Firefox, Safari, Edge). Android Native app. **Responsive Web/Tablet/Desktop design **.
* **Usability:** Clean UI, clear error messages with fast recovery. **Console prints are strictly forbidden for user feedback **.

---

## 4.0 SYSTEM ARCHITECTURE

### 4.1 Technology Stack (STRICT)

* **Backend:** Java 17+, Spring Boot 3.x, Spring Security + JWT, Spring Data JPA . **Custom-built (No BaaS)**.
* **Database:** PostgreSQL 14+.
* **Web Frontend:** React 18, TypeScript, Tailwind CSS, Axios.
* **Mobile Client:** Android Kotlin, XML Layouts (View System strictly;  **NO Jetpack Compose** **), Android API Level 34, Retrofit, Room Database **.
* **Integrations:** Google OAuth 2.0, SMTP (JavaMailSender), Spoonacular API.

---

## 5.0 API CONTRACT & COMMUNICATION

**Base URL:**`<span class="citation-411">https://[server_hostname]:[port]/api/v1</span>`**Response Structure:**`<span class="citation-410">{ "success": boolean, "message": string, "data": object|array|null, "error": { "code": string, "details": object|null }, "timestamp": string }</span>`

### 5.1 Endpoint Specifications

* `<span class="citation-409">POST /auth/register</span>`: Payload `<span class="citation-409">{ email, password, fullName }</span>`.
* `<span class="citation-408">POST /auth/login</span>`: Payload `<span class="citation-408">{ email, password }</span>`.
* `<span class="citation-407">POST /auth/google</span>`: Payload `<span class="citation-407">{ idToken }</span>`.
* `GET /auth/me`: Requires JWT. **Returns **`<span class="citation-406">{ id, email, fullName, role, avatarUrl }</span>`.
* `<span class="citation-405">POST /auth/logout</span>`: Requires JWT.
* `<span class="citation-404">POST /users/avatar</span>`: Multipart form data (`<span class="citation-404">file</span>`).
* `<span class="citation-403">GET /stock</span>`: Returns user's pantry ingredients.
* `<span class="citation-402">POST /stock</span>`: Payload `<span class="citation-402">{ ingredientId }</span>`.
* `<span class="citation-401">DELETE /stock/{ingredientId}</span>`: Removes ingredient from stock.
* `<span class="citation-400">GET /recipes/suggest</span>`: Consumes Spoonacular API.
* `<span class="citation-399">POST /favorites</span>`: Payload `<span class="citation-399">{ externalId, title, imageUrl, summary }</span>`.
* `<span class="citation-398">GET /favorites</span>`: Returns favorite recipes.
* `<span class="citation-397">DELETE /favorites/{id}</span>`: Removes recipe.
* `<span class="citation-396">POST /admin/ingredients</span>`: Payload `<span class="citation-396">{ name, category }</span>` (Admin strictly validated).
* `<span class="citation-395">DELETE /admin/ingredients/{id}</span>`: Deletes master ingredient (Admin strictly validated).

### 5.2 Common Error Codes

* **AUTH-001:** Invalid credentials.
* **AUTH-002:** Token expired.
* **STOCK-001:** Duplicate Ingredient.
* **RECIPE-001:** External API Failure.
* **FILE-001:** Invalid File Type.
* **RBAC-001:** Access Denied.

---

## 6.0 DATABASE DESIGN

### 6.1 Entity Details (PostgreSQL)

* **users:**`<span class="citation-388">id</span>` (PK), `<span class="citation-388">email</span>` (Unique), `<span class="citation-388">password_hash</span>`, `<span class="citation-388">full_name</span>`, `<span class="citation-388">role</span>`, `<span class="citation-388">avatar_url</span>`, `<span class="citation-388">created_at</span>`, `<span class="citation-388">is_verified</span>`.
* **categories:**`<span class="citation-387">id</span>` (PK), `<span class="citation-387">name</span>`.
* **master_ingredients:**`<span class="citation-386">id</span>` (PK), `<span class="citation-386">name</span>`, `<span class="citation-386">category_id</span>` (FK), `<span class="citation-386">image_url</span>`, `<span class="citation-386">is_verified</span>`.
* **pantry_items:**`<span class="citation-385">user_id</span>` (PK, FK), `<span class="citation-385">master_ingredient_id</span>` (PK, FK), `<span class="citation-385">added_at</span>`.
* **favorite_recipes:**`<span class="citation-384">id</span>` (PK), `<span class="citation-384">user_id</span>` (FK), `<span class="citation-384">external_api_id</span>`, `<span class="citation-384">title</span>`, `<span class="citation-384">image_url</span>`, `<span class="citation-384">summary</span>`, `<span class="citation-384">saved_at</span>`.
* **refresh_tokens:**`<span class="citation-383">id</span>` (PK), `<span class="citation-383">user_id</span>` (FK), `<span class="citation-383">token</span>` (Unique), `<span class="citation-383">expiry_date</span>`.

### 6.2 Relationships

* **One-to-Many:** Category → Master_Ingredients.
* **One-to-Many:** User → Pantry_Items.
* **One-to-Many:** Master_Ingredient → Pantry_Items.
* **Many-to-Many:** User ↔ Master_Ingredient (Resolved via `<span class="citation-379">pantry_items</span>` join table).
* **One-to-Many:** User → Favorite_Recipes.
* **One-to-Many:** User → Refresh_Tokens.

---

## 7.0 UI/UX DESIGN SYSTEM

* **Colors:** Primary (`<span class="citation-376">#2E7D32</span>` - Fresh Basil), Secondary (`<span class="citation-376">#E9C46A</span>` - Harvest Gold), Success (`<span class="citation-376">#10B981</span>` - Emerald), Error (`<span class="citation-376">#EF4444</span>` - Tomato Red).
* **Typography:** Inter or Roboto font family.
* **Spacing:** 8px grid system.
* **Mobile Specifics:** Touch-optimized buttons (min 44x44px), XML-Based UI Layouts, Role-Aware UI, Offline caching .
