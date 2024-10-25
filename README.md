
# Android YouTube Clone

This is an Android application that mimics the basic functionality of YouTube. Users can register, log in, upload videos, view a list of uploaded videos, and manage their own videos. The app also features a dark/light mode toggle.

## All Features

* User Registration and Login 
* Add and Upload Videos
* List of All Uploaded Videos
* List of User's Own Videos
* Detailed View for Each Video
* Edit and Delete User's Own Videos
* Dark/Light Mode Toggle

## Layout Files

* `activity_add_video.xml` - Layout for adding new videos.
* `activity_login.xml` - Layout for the login screen.
* `activity_main.xml` - Main layout file with theme toggle.
* `activity_register.xml` - Layout for user registration.
* `activity_video_detail.xml` - Layout for the video detail view.
* `activity_video_list.xml` - Layout for listing all videos.
* `activity_my_videos.xml` - Layout for listing user's own videos.
* `activity_edit_video.xml` - Layout for editing a video.
* `item_video.xml` - Layout for each video item in the list.
* `item_my_video.xml` - Layout for each video item in the user's video list.

## All Java Classes

* `AddVideoActivity.java` - Handles the functionality for adding videos.
* `LoginActivity.java` - Manages user login functionality.
* `MainActivity.java` - The main entry point of the application with theme toggle.
* `RegisterActivity.java` - Manages user registration functionality.
* `Video.java` - Model class for video objects.
* `VideoAdapter.java` - Adapter class for binding video data to the list.
* `MyVideosAdapter.java` - Adapter class for binding user's video data to the list.
* `VideoDetailActivity.java` - Manages the detailed view of each video.
* `VideoListActivity.java` - Displays the list of all videos.
* `MyVideosActivity.java` - Displays the list of user's own videos.
* `EditVideoActivity.java` - Manages editing of user's videos.

## Installation and Setup:

1. **Clone the repository:**

```sh
git clone https://github.com/Ibrahemkewan/android_youtube.git
cd android_youtube
```

2. **Open the project in Android Studio:**
   * Launch Android Studio.
   * Select `Open an existing project`.
   * Navigate to the cloned directory and select it.

3. **Build the project:**
   * Allow Android Studio to build the project and resolve any dependencies.
   * Ensure you have the latest Android SDK and necessary build tools installed.

4. **Run the application:**
   * Connect an Android device or start an emulator.
   * Click on the `Run` button in Android Studio or use `Shift + F10`.

## Dependencies

Ensure you have the following dependencies in your `build.gradle` file:

```gradle
dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
}
```

## Permissions

Ensure the following permission is added to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## Usage

* **Register**: Create a new user account.
* **Login**: Access the app using registered credentials.
* **Add Video**: Upload a new video.
* **View Videos**: Browse the list of all uploaded videos.
* **My Videos**: View, edit, and delete your own uploaded videos.
* **Video Detail**: View details of a selected video.
* **Theme Toggle**: Switch between light and dark modes.

## Contributing

Contributions are welcome! Please fork this repository and submit pull requests for any improvements.

## License

This project is licensed under the MIT License.

## Contact

For any questions or feedback, please reach out to [your email].
```

```sh
git add .
git commit -m "Update README with new features and layout information"
git push -f origin main
```

Be cautious when using force push (`-f`) as it overwrites the remote history. Only use it if you're sure you want to replace the remote content with your local content.
