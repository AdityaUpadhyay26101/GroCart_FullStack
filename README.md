# 🛒 GroCart - Full-Stack Grocery Shopping Application

**GroCart** is a modern, full-stack grocery application that integrates a **Jetpack Compose Android Frontend** with a scalable **Java Spring Boot Backend** and **MySQL Database**. It features real-time data synchronization, personalized user sessions, and optimized performance management to handle complex UI rendering.

---

## 📁 Project Structure (Monorepo)

This repository is structured as a monorepo to maintain both the client and server codebases in a single location:

```text
GroCart-FullStack/
├── grocart-android/              # Jetpack Compose Mobile Client
│   ├── app/src/main/java/com/grocart/first/
│   │   ├── data/                 # Models and SessionManager
│   │   ├── network/              # Retrofit Service and API Interface
│   │   └── ui/                   # Compose Screens and ViewModels
├── grocart-backend/              # Java Spring Boot REST API
│   ├── src/main/java/com/example/grocart_backend/
│   │   ├── controller/           # REST Endpoints (Auth, Cart, Orders)
│   │   ├── model/                # JPA Entities (User, CartItem)
│   │   └── repository/           # MySQL Data Access Layers
├── README.md                     # Main Project Documentation
└── .gitignore                    # Shared project ignore file
```
---

## ✨ Key Features
**MySQL**-Powered Authentication: Secure registration and login system that persists user data in a relational database.

**User Data Isolation:** Personalized experience where each user manages their own unique cart and order history via a specific userId.

**Staggered UI Initialization:** Optimized ViewModel logic that uses delayed coroutines to prevent "Black Screen" errors and startup lag.

**Real-time Cart Sync:** Atomic updates using StateFlow.update to keep the local UI and remote MySQL database perfectly in sync.

**Detailed Bill Calculation:** Dynamic billing including handling charges, delivery fees, and automated quantity adjustments.

---
## 🛠 Tech Stack

### ->Frontend (Android) 
**UI:** Jetpack Compose with Material 3

**Network:** Retrofit 2 & OkHttp (with Logging Interceptor)

**Concurrency:** Kotlin Coroutines & Flow

**Session:** SharedPreferences for persistent login

### ->Backend (Server)
**Framework:** Spring Boot 3

**Database:** MySQL

**ORM:** Spring Data JPA

**Mapping:** Jackson for unified JSON key synchronization

---

## 🚀 Optimized Code Highlights
### 1. Eliminating Startup Lag (Android)
To resolve "Davey" lag events and skipped frames, the app uses a staggered initialization strategy in the GroViewModel:
```kotlin
/* * Staggered init block allows the Android rendering engine to finish 
 * layout inflation before network data triggers a recomposition.
 */
 
init {
    checkExistingSession() 
    viewModelScope.launch {
        delay(1000) // Provides 1s headstart for UI thread stability
        launch { loadUserCart() } 
        launch { getFirstItem() }
    }
}

```
### 2. Unified Data Mapping (Backend)
We synchronized naming conventions to ensure zero-loss transfer between the Android client and MySQL:
```java
// CartItem.java (Backend Entity)
@JsonProperty("item_price") // Matches Android SerialName
@Column(name = "item_price")
private Integer itemPrice;

@JsonProperty("stringResourceId") // Matches Android itemName
@Column(name = "item_name")
private String itemName;
```
---
## 🏗️ Getting Started
### Prerequisites
1. Android Studio Ladybug (or newer)

2. JDK 17 or higher

3. MySQL Server

### 1. Installation
1. Clone the Repository
```bash
git clone https://github.com/AdityaUpadhyay26101/GroCart_FullStack.git
```

### 2.Backend Setup 

- Navigate to ```/grocart-backend```.

- Update ```src/main/resources/application.properties``` with your MySQL credentials.

- Run ```./mvnw spring-boot:run```.

### 3.Frontend Setup

- Open ```/grocart-android``` in Android Studio.

- Ensure the ```BASE_URL``` in ```FirstApiService.kt``` points to your server (e.g., ```10.0.2.2:8080```).

- Enable ```usesCleartextTraffic="true"``` in your ```AndroidManifest.xml``` for local development.
---

## 👨‍💻 Developer
***Aditya Upadhyay*** Computer Science Undergraduate | Government Engineering College, Bharatpur.
