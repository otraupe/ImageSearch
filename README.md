# ImageSearch
An Android app for browsing images from [Pixabay](https://pixabay.com/)

## What ImageSearch does
This app was created for a code challenge. It lets you query the [Pixabay images search API](https://pixabay.com/api/docs), displays the results in list form together with a preview image, and offers a detail view with a larger image and some additional information. The app also caches the search results and downloaded images for offline use--the cache is invalidated after 24h (as per the Pixabay API guidelines). Upon scrolling the app dynamically loads more search results and images. The image in the detail view is zoomable (pinch or double-tap).

## Tech stack
The app is written in Kotlin using [Jetpack Compose](https://developer.android.com/jetpack/compose) for composing the UI, [Jetpack Navigation for Compose](https://developer.android.com/jetpack/compose/navigation) for switching views, [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) for dependency injection, [Retrofit 2](https://square.github.io/retrofit) for querying the API, and a [Room](https://developer.android.com/training/data-storage/room) database for caching the search hits. It handles configuration changes by using the MVVM architecture. Image downloading and caching is handled by [Glide](https://bumptech.github.io/glide).

## Build instructions
Create an 'assets' directory with a file 'config.properties' containing the key 'api.key' and set it to your [Pixabay API key](https://pixabay.com/api/docs/#api_search_images)--you'll need to be logged in to see it.

## Future improvements
- Automated tests
- Improved UI creation (calling hiltViewModel() at the top of SearchView apparently slows down the initial display of the main screen)
- Search terms history dropdown
- Dynamic loading of images with different sizes for various devices' screen sizes
- Optimized landscape detail view (increased image view size)
- Dedicated full-screen view for the larger image (with automatic return to initial scaling values)
- Removal of the dialog leading to the detail view (was part of the requirements, but is not really needed)
- Bug fixes: repeated Toast presentations, no user notification on reaching the API's default hit limit of 500, the occasional IndexOutOfBoundsException by Glide's image preloader (potentially a bug on their side)

## Licensing
This software is free for anyone to use as they seem fit.
