# InStock Software Test Plan

## 1. Document Control

| Field          | Value                                 |
| -------------- | ------------------------------------- |
| Project        | InStock                               |
| Scope          | Backend, Web Frontend, Android Mobile |
| Test Plan Type | Functional Manual Test Plan           |
| Prepared By    | Gregory Ivan Onyx M. Badinas          |
| Version        | 1.0                                   |
| Status         | Draft for Execution                   |

## 2. Purpose

This document defines the manual functional test coverage for the critical features of the InStock system after the Vertical Slice Architecture refactor. The objective is to verify that all mandatory project requirements are working correctly across the Spring Boot backend, React web application, and Kotlin Android application where applicable.

## 3. Test Scope

### In Scope

- Authentication and session security
- Role-based access control for standard users and administrators
- Pantry/Inventory CRUD operations
- Recipe retrieval through Spoonacular integration
- Google OAuth login flow
- SMTP welcome email dispatch

### Out of Scope

- Performance testing
- Load and stress testing
- Accessibility certification
- Automated test implementation
- UI redesign validation beyond functional behavior

## 4. Test Environment

| Layer             | Environment                                                            |
| ----------------- | ---------------------------------------------------------------------- |
| Backend           | Spring Boot API running locally or deployed staging instance           |
| Web               | React/Vite frontend connected to target backend                        |
| Mobile            | Android emulator or device connected to target backend                 |
| Database          | PostgreSQL instance with seeded test users                             |
| External Services | Google OAuth credentials, Spoonacular API key, SMTP server credentials |

## 5. Test Data

| Data Item                                               | Purpose                       |
| ------------------------------------------------------- | ----------------------------- |
| `admin@test.com` / valid password                     | Administrator validation      |
| `user@test.com` / valid password                      | Standard user validation      |
| New unregistered email account                          | Registration flow             |
| Pantry ingredients like `tomato`, `egg`, `garlic` | Pantry CRUD and recipe search |
| Invalid JWT or expired JWT                              | Unauthorized access checks    |
| SMTP inbox accessible by QA                             | Welcome email verification    |

## 6. Entry and Exit Criteria

### Entry Criteria

- Backend application starts successfully
- Web frontend builds and runs successfully
- Android application builds successfully
- Database is reachable
- Required environment variables are configured for JWT, Spoonacular, Google OAuth, and SMTP

### Exit Criteria

- All critical manual test cases executed
- All failed test cases documented with reproducible evidence
- No unresolved blocker remains on authentication, RBAC, CRUD, or integrations

## 7. Manual Functional Test Cases

---

## 7.1 Authentication & Security

### TC-AUTH-001: Successful User Registration

| Field         | Details                                                             |
| ------------- | ------------------------------------------------------------------- |
| Objective     | Verify that a new user can register successfully                    |
| Priority      | Critical                                                            |
| Prerequisites | Backend and frontend are running; test email does not already exist |

**Test Steps**

1. Open the web application registration screen.
2. Enter a valid full name.
3. Enter a unique valid email address.
4. Enter a password with at least 8 characters.
5. Confirm the password if the UI requires confirmation.
6. Submit the registration form.

**Expected Results**

1. Registration request returns success.
2. User record is created in the database.
3. Password is stored hashed, not plaintext.
4. JWT token is returned after successful registration.
5. User is redirected into the authenticated area or session is established.
6. Welcome email dispatch is triggered.

### TC-AUTH-002: Registration Validation Failure

| Field         | Details                                     |
| ------------- | ------------------------------------------- |
| Objective     | Verify input validation during registration |
| Priority      | High                                        |
| Prerequisites | Registration page is accessible             |

**Test Steps**

1. Open the registration page.
2. Submit the form with blank required fields.
3. Submit the form with an invalid email format.
4. Submit the form with a password shorter than 8 characters.
5. Submit the form using an email address that already exists.

**Expected Results**

1. Validation messages are shown for blank or invalid fields.
2. Backend rejects invalid payloads.
3. Duplicate email registration is blocked.
4. No new user record is created for invalid submissions.

### TC-AUTH-003: Successful Login

| Field         | Details                             |
| ------------- | ----------------------------------- |
| Objective     | Verify login with valid credentials |
| Priority      | Critical                            |
| Prerequisites | Registered test account exists      |

**Test Steps**

1. Open the login page.
2. Enter a valid registered email.
3. Enter the correct password.
4. Submit the login form.

**Expected Results**

1. Login request returns success.
2. JWT token is returned by the backend.
3. User session data is stored on the client.
4. User is redirected to the dashboard or protected area.

### TC-AUTH-004: Failed Login with Invalid Credentials

| Field         | Details                                        |
| ------------- | ---------------------------------------------- |
| Objective     | Verify login rejection for invalid credentials |
| Priority      | Critical                                       |
| Prerequisites | Login page is accessible                       |

**Test Steps**

1. Open the login page.
2. Enter an existing email with an incorrect password.
3. Submit the login form.
4. Repeat with an unregistered email address.

**Expected Results**

1. Backend returns an authentication failure response.
2. No JWT token is issued.
3. User remains on the login page.
4. Error feedback is shown to the user.

### TC-AUTH-005: JWT Validation for Protected Endpoints

| Field         | Details                                             |
| ------------- | --------------------------------------------------- |
| Objective     | Verify that protected endpoints require a valid JWT |
| Priority      | Critical                                            |
| Prerequisites | API testing tool such as Postman is available       |

**Test Steps**

1. Send a request to a protected endpoint such as `GET /api/v1/auth/me` without an `Authorization` header.
2. Repeat the request with an invalid JWT.
3. Repeat the request with a valid JWT from a logged-in user.

**Expected Results**

1. Request without token is rejected.
2. Request with invalid token is rejected.
3. Request with valid token succeeds and returns the authenticated user profile.

### TC-AUTH-006: Session Guard on Protected UI Routes

| Field         | Details                                                   |
| ------------- | --------------------------------------------------------- |
| Objective     | Verify client-side protection of authenticated web routes |
| Priority      | High                                                      |
| Prerequisites | Web app is running                                        |

**Test Steps**

1. Clear browser storage so no user token is present.
2. Attempt to navigate directly to a protected route such as `/dashboard/pantry`.
3. Log in successfully.
4. Retry navigation to the same protected route.

**Expected Results**

1. Unauthenticated access redirects to login or landing page.
2. Authenticated access loads the protected route successfully.

---

## 7.2 Role-Based Access Control (RBAC)

### TC-RBAC-001: Admin Can Access Bulk Pantry Deletion API

| Field         | Details                                                         |
| ------------- | --------------------------------------------------------------- |
| Objective     | Verify that only administrators can invoke bulk pantry deletion |
| Priority      | Critical                                                        |
| Prerequisites | Admin user exists and has a valid JWT                           |

**Test Steps**

1. Log in using an admin account.
2. Populate the pantry with at least one item.
3. Send `DELETE /api/v1/stock` with the admin JWT.

**Expected Results**

1. Request is authorized.
2. Backend deletes the pantry records associated with the admin user.
3. Success response is returned.

### TC-RBAC-002: Standard User Is Blocked from Bulk Pantry Deletion API

| Field         | Details                                          |
| ------------- | ------------------------------------------------ |
| Objective     | Verify API-level restriction for non-admin users |
| Priority      | Critical                                         |
| Prerequisites | Standard user exists and has a valid JWT         |

**Test Steps**

1. Log in using a standard user account.
2. Send `DELETE /api/v1/stock` using the standard user JWT.

**Expected Results**

1. Request is denied by backend role enforcement.
2. Pantry data is not deleted.
3. API returns forbidden or equivalent authorization error.

### TC-RBAC-003: Clear All Pantry Control Hidden for Standard User

| Field         | Details                                         |
| ------------- | ----------------------------------------------- |
| Objective     | Verify UI-level restriction for non-admin users |
| Priority      | High                                            |
| Prerequisites | Standard user can log in to the web application |

**Test Steps**

1. Log in as a standard user.
2. Navigate to the Pantry page.
3. Inspect the available pantry action buttons.

**Expected Results**

1. `Clear All` button is not visible to the standard user.
2. Other authorized pantry actions remain visible.

### TC-RBAC-004: Clear All Pantry Control Visible for Admin

| Field         | Details                                               |
| ------------- | ----------------------------------------------------- |
| Objective     | Verify admin UI visibility for the bulk delete action |
| Priority      | High                                                  |
| Prerequisites | Admin user can log in to the web application          |

**Test Steps**

1. Log in as an admin user.
2. Navigate to the Pantry page.

**Expected Results**

1. `Clear All` button is visible.
2. Admin can trigger the bulk delete action successfully.

### TC-RBAC-005: Route Guard for Role-Restricted Screens

| Field         | Details                                                                       |
| ------------- | ----------------------------------------------------------------------------- |
| Objective     | Verify `PrivateRoute` role checking behavior where `allowedRoles` is used |
| Priority      | Medium                                                                        |
| Prerequisites | A route or test harness exists that uses `allowedRoles`                     |

**Test Steps**

1. Log in as a user whose role is not in the allowed list.
2. Attempt to access a route guarded with `allowedRoles`.
3. Repeat using a user whose role is allowed.

**Expected Results**

1. Unauthorized user sees `Not Authorized` or is redirected.
2. Authorized user can access the route normally.

---

## 7.3 Core Business CRUD: Pantry / Inventory

### TC-PANTRY-001: Create Pantry Item

| Field         | Details                           |
| ------------- | --------------------------------- |
| Objective     | Verify pantry item creation       |
| Priority      | Critical                          |
| Prerequisites | Authenticated user session exists |

**Test Steps**

1. Navigate to the Pantry page.
2. Enter a new ingredient such as `tomato`.
3. Click `Add`.

**Expected Results**

1. Pantry item is created successfully.
2. New item appears in the pantry list.
3. Overview count is updated if applicable.

### TC-PANTRY-002: Prevent Duplicate Pantry Item Creation

| Field         | Details                                      |
| ------------- | -------------------------------------------- |
| Objective     | Verify duplicate protection for pantry items |
| Priority      | High                                         |
| Prerequisites | Pantry already contains `tomato`           |

**Test Steps**

1. Enter `tomato` again on the Pantry page.
2. Click `Add`.

**Expected Results**

1. Backend rejects duplicate item creation.
2. UI shows duplicate or informational message.
3. Only one pantry item remains for that ingredient.

### TC-PANTRY-003: Read Pantry Item List

| Field         | Details                                         |
| ------------- | ----------------------------------------------- |
| Objective     | Verify retrieval and display of pantry items    |
| Priority      | Critical                                        |
| Prerequisites | Authenticated user has one or more pantry items |

**Test Steps**

1. Open the Pantry page.
2. Observe the loaded pantry list.
3. Refresh the page.

**Expected Results**

1. Existing pantry items are loaded from the backend.
2. Items remain visible after refresh.
3. No unauthorized user data is shown.

### TC-PANTRY-004: Update Pantry Item

| Field         | Details                                         |
| ------------- | ----------------------------------------------- |
| Objective     | Verify update flow for an existing pantry item  |
| Priority      | Critical                                        |
| Prerequisites | Authenticated user has at least one pantry item |

**Test Steps**

1. Navigate to the Pantry page.
2. Click `Edit` on an existing pantry item.
3. Change the ingredient name to a new valid value such as `garlic`.
4. Click `Save`.

**Expected Results**

1. Backend accepts the `PUT` update request.
2. Item name is updated in the database.
3. Updated value is immediately shown in the UI.

### TC-PANTRY-005: Prevent Duplicate Pantry Item Update

| Field         | Details                                   |
| ------------- | ----------------------------------------- |
| Objective     | Verify duplicate validation during update |
| Priority      | High                                      |
| Prerequisites | Pantry contains `tomato` and `garlic` |

**Test Steps**

1. Edit `garlic`.
2. Change its name to `tomato`.
3. Click `Save`.

**Expected Results**

1. Backend rejects the update with a duplicate conflict.
2. Original pantry item remains unchanged.
3. UI shows an appropriate duplicate warning.

### TC-PANTRY-006: Delete Single Pantry Item

| Field         | Details                                         |
| ------------- | ----------------------------------------------- |
| Objective     | Verify deletion of a single pantry item         |
| Priority      | Critical                                        |
| Prerequisites | Authenticated user has at least one pantry item |

**Test Steps**

1. Navigate to the Pantry page.
2. Click `Delete` on one pantry item.

**Expected Results**

1. Selected item is removed from the backend.
2. Item disappears from the pantry list.
3. Other pantry items remain intact.

### TC-PANTRY-007: Delete All Pantry Items as Admin

| Field         | Details                                            |
| ------------- | -------------------------------------------------- |
| Objective     | Verify bulk pantry clear operation for admin users |
| Priority      | High                                               |
| Prerequisites | Admin account is logged in; pantry contains items  |

**Test Steps**

1. Log in as admin.
2. Open the Pantry page.
3. Click `Clear All`.

**Expected Results**

1. All pantry items for the admin user are deleted.
2. Pantry UI becomes empty.
3. Success feedback is shown.

### TC-PANTRY-008: Find Recipe from Pantry

| Field         | Details                                 |
| ------------- | --------------------------------------- |
| Objective     | Verify pantry-to-recipe workflow        |
| Priority      | Medium                                  |
| Prerequisites | Pantry contains at least one ingredient |

**Test Steps**

1. Open the Pantry page.
2. Click `Find Recipe`.

**Expected Results**

1. User is navigated to the Recipes page.
2. Pantry ingredients are carried into the recipe search flow.

---

## 7.4 External API Integration: Spoonacular

### TC-RECIPE-001: Search Recipes by Ingredients

| Field         | Details                                                  |
| ------------- | -------------------------------------------------------- |
| Objective     | Verify recipe retrieval using ingredient-based search    |
| Priority      | Critical                                                 |
| Prerequisites | Spoonacular API key is configured and backend is running |

**Test Steps**

1. Log in to the web application.
2. Navigate to the Recipes page.
3. Enter one or more ingredients such as `egg, tomato`.
4. Trigger the recipe search.

**Expected Results**

1. Backend calls Spoonacular through the recipe facade.
2. Matching recipes are returned to the frontend.
3. Recipe cards are displayed with title, image, and metadata.

### TC-RECIPE-002: Search Recipes by Name

| Field         | Details                                          |
| ------------- | ------------------------------------------------ |
| Objective     | Verify recipe retrieval using title-based search |
| Priority      | High                                             |
| Prerequisites | Spoonacular API key is configured                |

**Test Steps**

1. Navigate to the Recipes page.
2. Switch to or use the recipe title search mode if available.
3. Search using a term such as `beef broccoli`.

**Expected Results**

1. Backend returns recipe results from Spoonacular.
2. Matching recipes are displayed to the user.

### TC-RECIPE-003: View Recipe Details

| Field         | Details                                         |
| ------------- | ----------------------------------------------- |
| Objective     | Verify retrieval of detailed recipe information |
| Priority      | High                                            |
| Prerequisites | At least one recipe search result is available  |

**Test Steps**

1. Perform a recipe search.
2. Open any recipe detail view.

**Expected Results**

1. Backend fetches recipe detail information from Spoonacular.
2. UI displays ingredients, instructions, preparation time, and serving details.
3. Source URL is available when provided.

### TC-RECIPE-004: External API Failure Handling

| Field         | Details                                                                                 |
| ------------- | --------------------------------------------------------------------------------------- |
| Objective     | Verify user-facing behavior when Spoonacular is unavailable                             |
| Priority      | Medium                                                                                  |
| Prerequisites | Ability to temporarily use an invalid Spoonacular API key or disconnect external access |

**Test Steps**

1. Trigger a recipe search while the external API is unavailable or misconfigured.

**Expected Results**

1. Application does not crash.
2. Error feedback is shown to the user.
3. No corrupted recipe data is displayed.

---

## 7.5 System Integrations

### TC-INT-001: Google OAuth Login

| Field         | Details                                                                             |
| ------------- | ----------------------------------------------------------------------------------- |
| Objective     | Verify Google social login end-to-end                                               |
| Priority      | Critical                                                                            |
| Prerequisites | Google OAuth credentials are configured correctly; test Google account is available |

**Test Steps**

1. Open the login page.
2. Click `Continue with Google`.
3. Complete authentication using a valid Google account.
4. Return to the application after Google redirects back.

**Expected Results**

1. Google OAuth flow completes successfully.
2. Backend finds or creates the user record in the database.
3. Backend issues a custom application JWT.
4. Frontend stores the JWT and authenticates the session.
5. User is redirected into the protected application area.

### TC-INT-002: First-Time Google OAuth User Provisioning

| Field         | Details                                                            |
| ------------- | ------------------------------------------------------------------ |
| Objective     | Verify that first-time OAuth users are persisted correctly         |
| Priority      | High                                                               |
| Prerequisites | Google account email does not already exist in local user database |

**Test Steps**

1. Log in with a Google account that has never used the system before.
2. Inspect resulting user data through `/api/v1/auth/me` or database.

**Expected Results**

1. New user record is created.
2. User role defaults to `USER`.
3. OAuth-created profile fields such as email and display name are stored.

### TC-INT-003: SMTP Welcome Email on Standard Registration

| Field         | Details                                                           |
| ------------- | ----------------------------------------------------------------- |
| Objective     | Verify welcome email dispatch for standard registration           |
| Priority      | Critical                                                          |
| Prerequisites | SMTP configuration is valid; tester has access to recipient inbox |

**Test Steps**

1. Register a brand-new account using email/password registration.
2. Monitor the configured inbox for the registered email.

**Expected Results**

1. Registration succeeds.
2. Application dispatches a welcome email through SMTP.
3. Inbox receives a welcome email with the expected subject and body.

### TC-INT-004: SMTP Welcome Email on First-Time OAuth Registration

| Field         | Details                                                                             |
| ------------- | ----------------------------------------------------------------------------------- |
| Objective     | Verify welcome email dispatch for first-time Google OAuth users                     |
| Priority      | High                                                                                |
| Prerequisites | SMTP is configured; Google OAuth is configured; Google account is new to the system |

**Test Steps**

1. Sign in using Google with a brand-new Google account.
2. Monitor the Google account inbox or destination inbox.

**Expected Results**

1. First-time OAuth account is created successfully.
2. Welcome email dispatch is triggered through the registration event listener.

### TC-INT-005: SMTP Failure Does Not Break Registration

| Field         | Details                                                             |
| ------------- | ------------------------------------------------------------------- |
| Objective     | Verify resilience when SMTP service is misconfigured or unavailable |
| Priority      | High                                                                |
| Prerequisites | Ability to temporarily use incorrect SMTP credentials               |

**Test Steps**

1. Configure invalid SMTP credentials in the environment.
2. Perform a new registration.

**Expected Results**

1. User registration still succeeds.
2. Application logs email dispatch failure.
3. User-facing registration flow does not crash.

---

## 8. Defect Reporting Guidance

Each failed test case should capture:

- Test Case ID
- Environment used
- Exact input data
- Actual result
- Expected result
- Screenshot or API response evidence
- Severity and reproducibility

## 9. Automated Testing Strategy

The next phase should convert the highest-risk areas of this plan into automated tests.

### Backend Automated Test Targets

- `AuthController` and auth flow coverage for registration, login, `/me`, invalid credentials, and JWT-protected access
- `PantryController` coverage for create, read, update, delete-one, and admin-only delete-all
- `RecipeController` coverage for recipe search and detail endpoints using mocked Spoonacular responses
- `FavoriteController` coverage for save/list/delete flows
- `JwtAuthenticationFilter` and security configuration behavior for protected endpoints and role restrictions
- `UserRegistrationAuditListener` and `EmailService` behavior using mocked mail sender integration
- `OAuth2LoginSuccessHandler` and OAuth user provisioning behavior with mocked OAuth principals

### Frontend Automated Test Targets

- `PrivateRoute.jsx` for unauthenticated access, authorized access, and role-restricted access
- `PantryPage.jsx` for add, edit, delete, role-based visibility of `Clear All`, and error handling
- `AuthPanel.jsx`, `Login.jsx`, and `Register.jsx` for successful and failed auth flows
- `RecipesPage.jsx` and `RecipeDetailPage.jsx` for recipe search, result rendering, and detail rendering
- `ProfilePage.jsx` for authenticated profile loading

### Mobile Automated Test Targets

- `features.auth.LoginActivity` and `RegisterActivity` for field validation and navigation behavior
- `core.network.ApiClient` and auth service wiring with integration or mocked API tests
- `core.navigation.FooterNavigation` for feature navigation integrity
- Feature activities for launch and wiring validation after the package refactor
