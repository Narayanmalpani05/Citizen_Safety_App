# Citizen Safety App

A modern, comprehensive Android application designed to enhance personal cybersecurity and physical safety.

## Overview
The Citizen Safety App combines a Machine Learning-driven malicious URL detector, a secure AES256-encrypted Password Vault, a Password Generator, and an Emergency SOS broadcaster into a single Material 3 designed interface.

## Project Structure
*   `Citizen_Safety_app/`: The complete Android Studio project (Java).
*   `ml_backend/`: The Flask-based Machine Learning API for URL detection.
*   `ML assets/`: Original datasets.
*   `stitch_citizen_safety_guardian/`: Design system references.

## Setup Instructions

### 1. Machine Learning Backend
The backend requires Python 3.9+ and uses `scikit-learn` for inference.
1. Navigate to the `ml_backend/` directory:
   ```bash
   cd "ml_backend"
   ```
2. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```
3. Run the local development server:
   ```bash
   python app.py
   ```
   *The server will start on `http://localhost:5000` (accessible to the Android Emulator via `10.0.2.2`).*

### 2. Android App (Development)
1. Open `Citizen_Safety_app/` in Android Studio.
2. The project uses `Retrofit` and handles configurations via `BuildConfig`.
3. By default, `Assemble Debug` will automatically route API requests to `http://10.0.2.2:5000/`.
4. Click **Run** to launch the app on an emulator.

## Production Deployment (Backend)
To deploy the backend to a cloud provider like Render, Railway, or Heroku:
1. Connect your repository to the provider.
2. Set the root directory to `ml_backend/`.
3. The provider will automatically detect the `requirements.txt`, `Procfile` (`web: gunicorn app:app`), and `runtime.txt`.
4. Once deployed, note your new HTTPS URL (e.g., `https://my-app.onrender.com/`).

## Production Deployment (Android)
To release the Android app:
1. Open `ApiClient.java` and update `BASE_URL_PROD` to your deployed HTTPS URL.
2. Build an **Assemble Release** variant (or generate a Signed APK) in Android Studio.
3. The `BuildConfig.DEBUG` flag will automatically switch Retrofit to use the secure production URL, bypassing cleartext limitations.

## Core Architecture
*   **Networking**: Retrofit 2 + Gson + MVVM Architecture (LiveData).
*   **Storage**: AndroidX Security Crypto `EncryptedSharedPreferences` (AES256_GCM).
*   **UI/UX**: Material 3 Design System, `ConstraintLayout`, `RecyclerView`, Lottie Animations.
