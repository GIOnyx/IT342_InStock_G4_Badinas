# InStock - Recipe Suggestion & Food Waste Reduction App

**Prepared By:** Gregory Ivan Onyx M. Badinas (DSIT-3, IT342-64)  
**Domain:** Cooking  
**Primary Users:** General Public  

## 📖 Project Overview
InStock is a full-stack application aimed at reducing food waste. It enables users to select ingredients they currently have in stock, and the system dynamically suggests recipes that match the selected ingredients. 

The project strictly adheres to a layered three-tier architecture[cite: 68, 121]. It operates utilizing a Spring Boot backend API, a React web application, and a native Android mobile application. 

## 🏗️ System Architecture
The application is structured to ensure a clean separation of concerns, decoupling the presentation layer from the business and data layers[cite: 121].

* **Backend (Business & Data Layer):** Developed using Java and Spring Boot[cite: 253]. This serves as the centralized "brain" where data validation, CRUD computations, and security policies are enforced[cite: 123].
* **Frontend Web (Presentation Layer):** Built with React as a Single Page Application (SPA)[cite: 128, 252]. 
* **Frontend Mobile (Presentation Layer):** A native Android mobile application[cite: 253].
* **Database:** A relational database (e.g., MySQL or PostgreSQL) ensuring ACID compliance and data durability[cite: 124, 253].

## ✨ Core Features & IT342 Requirements

### 1. Authentication, Security & RBAC
* **Secure Access:** The system handles user registration, login, and logout securely[cite: 7, 8, 10].
* **Token Management:** Utilizes JSON Web Tokens (JWT) for session authentication and BCrypt for robust password hashing[cite: 9, 11].
* **Protected Routes:** Features a `/me` endpoint to retrieve the current authenticated user[cite: 13].
* **Role-Based Access Control:** Implements minimum API-level and UI-level restrictions for two user roles (e.g., Admin for data management and Regular User for recipe generation)[cite: 15, 16, 17, 19, 20].

### 2. Core Business Module (MVP Scope)
* **Ingredient & Recipe Management:** Users can search and add ingredients to their personal stock list, and add suggested recipes to their "favorites".
* **CRUD Operations:** Includes full Create, Read, Update, and Delete capabilities for core entities with proper validation[cite: 25, 26].

### 3. Required System Integrations
To prevent the formation of isolated "functional silos", InStock connects with multiple external protocols[cite: 100, 101]:
* **External API Integration:** Consumes a public external API (e.g., Spoonacular or similar) to fetch and display dynamic recipe data[cite: 33, 34, 36].
* **Social Login:** Integrates Google OAuth 2.0, securely generating a custom JWT following the OAuth flow[cite: 37, 40].
* **File Uploads:** Allows users to upload relevant files (such as recipe profile images) that are linked directly to database records[cite: 41, 42, 44].
* **Automated Emailing:** Uses SMTP to trigger one account-related email (e.g., welcome/verification) and one system notification (e.g., weekly recipe suggestions)[cite: 55, 57, 58].

### 4. Database Structure
* The system utilizes a fully normalized relational database design to avoid duplicate data[cite: 64].
* It contains a minimum of 5 tables with proper One-to-Many and Many-to-One relationships (e.g., Users, Ingredients, UserStock, Recipes, Favorites)[cite: 61, 62].

## 📂 Repository Structure
This repository strictly follows the IT342 mandated folder structure: `IT342_InStock_64_Badinas`[cite: 87].

```text
📦 IT342_InStock_64_Badinas
 ┣ 📂 backend/        # Spring Boot Java application [cite: 88, 253]
 ┣ 📂 web/            # React Single Page Application [cite: 89, 128]
 ┣ 📂 mobile/         # Android application [cite: 90]
 ┣ 📂 docs/           # SDD, Architecture Diagrams (UML, ERD), API Specs [cite: 79, 91]
 ┗ 📜 README.md       # Project documentation [cite: 92]