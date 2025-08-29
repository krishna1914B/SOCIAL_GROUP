# Social App

A social-style Android app with Firebase (for users & metadata) and Cloudinary (for image/video storage).

## Features

- User authentication with Firebase Anonymous Authentication
- User profile management (name and date of birth)
- Friend list display
- Media (image/video) upload and sharing
- Real-time updates with Firestore

## Tech Stack

- Android (Kotlin, Jetpack Compose)
- Firebase Firestore (for users & album metadata)
- Firebase Authentication (anonymous login)
- Cloudinary SDK (for uploading and serving media)
- MVVM architecture

## Setup Instructions

### Firebase Configuration

1. Create a new Firebase project at https://console.firebase.google.com/
2. Register your Android app with package name `com.socialapp.android`
3. Download the `google-services.json` file
4. Replace the placeholder `app/google-services.json` with your actual file
5. Enable Anonymous Authentication in the Firebase Console:
   - Go to Authentication > Sign-in method
   - Enable Anonymous sign-in
6. Set up Firestore Database:
   - Go to Firestore Database > Create database
   - Start in test mode (or set up production rules as needed)
   - Set up the following collections:
     - `users` - to store user information
     - `albums` - to store media metadata

### Cloudinary Configuration

The Cloudinary credentials have already been configured in the app:
- Cloud Name: `dduwnws1c`
- API Key: `299232436378198`
- API Secret: `gAH-qxx0TJ1GEd_HQKKC8PsW4Rs`

These are stored in `app/src/main/res/values/cloudinary_config.xml`.

If you need to change them, simply update the values in that file.

## Building with Codemagic

This project is configured to build with Codemagic CI/CD. To set up:

1. Fork this repository to your GitHub account
2. Connect your GitHub account to Codemagic
3. Add your app to Codemagic
4. In the app settings, add the following environment variables:
   - `GOOGLE_SERVICES_JSON` - Base64 encoded content of your google-services.json file
   - `CLOUDINARY_URL` - Your Cloudinary URL in the format `cloudinary://API_KEY:API_SECRET@CLOUD_NAME`
5. Configure the workflow to decode the google-services.json file during build:
   ```yaml
   scripts:
     - name: Decode google-services.json
       script: |
         echo $GOOGLE_SERVICES_JSON | base64 --decode > $CM_BUILD_DIR/app/google-services.json
   ```

## Project Structure

```
app/
├── src/main/java/com/socialapp/android/
│   ├── data/
│   │   ├── model/       # Data models (User, Media)
│   │   └── repository/  # Data repositories (UserRepository, MediaRepository)
│   ├── ui/
│   │   ├── album/       # Album screen components
│   │   ├── friends/     # Friends screen components
│   │   ├── main/        # Main screen with tabs
│   │   ├── onboarding/  # User onboarding screen
│   │   └── user/        # User ViewModel and related classes
│   │   └── media/       # Media ViewModel and related classes
│   ├── ui/theme/        # Theme and styling
│   ├── MainActivity.kt  # Main activity
│   ├── NavigationHost.kt # Navigation host
│   └── SocialApplication.kt # Application class
├── src/main/res/        # Resources
└── build.gradle.kts     # App-level build configuration
```

## Development

To run the app locally:

1. Open the project in Android Studio
2. Ensure you have the correct Firebase and Cloudinary configurations
3. Build and run the app on an emulator or physical device

## Dependencies

Key dependencies used in this project:

- Jetpack Compose for UI
- Firebase Authentication and Firestore
- Cloudinary Android SDK
- Coil for image loading
- AndroidX Lifecycle for MVVM architecture

## License

This project is open source and available under the MIT License.