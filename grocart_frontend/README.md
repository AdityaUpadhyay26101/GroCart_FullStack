# GroCart - Android Shopping App

GroCart is a sample grocery shopping application for Android, built entirely with modern Android development tools. It demonstrates a simple MVVM architecture, Jetpack Compose for the UI, Firebase for authentication, and more.

## ✨ Features

*   **User Authentication:**
    *   Phone number authentication using Firebase OTP.
    *   Continue as a Guest for anonymous browsing.
*   **Product Browsing:**
    *   View products by category.
    *   Dynamic grid layout for items.
*   **Shopping Cart:**
    *   Add/remove items.
    *   Adjust item quantities.
    *   View a detailed bill with item total, handling charges, and delivery fees.
*   **Checkout Flow:**
    *   Simulated payment processing screen.
*   **Order History:**
    *   View past orders (for logged-in users).
*   **Modern UI:**
    *   Built with Jetpack Compose.
    *   Includes light and dark themes.

## 🛠 Tech Stack & Architecture

*   **Language:** [Kotlin](https://kotlinlang.org/)
*   **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
*   **Architecture:** Model-View-ViewModel (MVVM)
*   **State Management:** `StateFlow` and `ViewModel`
*   **Navigation:** [Navigation-Compose](https://developer.android.com/jetpack/compose/navigation)
*   **Authentication:** [Firebase Authentication](https://firebase.google.com/docs/auth) (Phone/OTP)
*   **Image Loading:** [Coil](https://coil-kt.github.io/coil/)
*   **Asynchronous Operations:** Kotlin Coroutines

## 🚀 Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

*   Android Studio Iguana | 2023.2.1 or newer.
*   A Firebase project.

### Installation

1.  **Clone the repo**
    ```sh
    git clone https://github.com/your-username/your-repository-name.git
    ```
2.  **Firebase Setup**
    *   Go to the [Firebase console](https://console.firebase.google.com/) and create a new project.
    *   Add an Android app to your Firebase project with the package name `com.grocart.first`.
    *   Download the `google-services.json` file.
    *   Place the `google-services.json` file in the `D:/Gro/app/` directory.
    *   Enable **Phone Number** sign-in in the Firebase Authentication section.
3.  **Open in Android Studio**
    *   Open the cloned project in Android Studio.
    *   Let Gradle sync and build the project.
    *   Run the `app` configuration on an emulator or a physical device.

## 📂 Project Structure

Here's a high-level overview of the project's structure:

```
app/src/main/java/com/grocart/first
├── data/              # Data models (Order, InternetItem, etc.) and repository classes.
├── ui/                # All Jetpack Compose screens and UI-related components.
│   ├── theme/         # Theming files (Color, Shape, Theme, Type).
│   ├── FirstApp.kt    # Main App Composable with navigation graph.
│   ├── GroViewModel.kt # The central ViewModel for UI state and business logic.
│   ├── CartScreen.kt  # Composable for the shopping cart.
│   └── LoginUi.kt     # Composables for Phone & OTP authentication.
└── MainActivity.kt    # The single entry point Activity.
```

### ⭐ If you like this project, consider giving it a star!
