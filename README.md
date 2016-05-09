# Nearbooks [![BuddyBuild](https://dashboard.buddybuild.com/api/statusImage?appID=56b0ee2d187416010091ba9a&branch=master&build=latest)](https://dashboard.buddybuild.com/apps/56b0ee2d187416010091ba9a/build/latest)

Nearbooks Android client for manage the books lending in nearsoft. This Android client uses the
[Nearbooks backend](https://github.com/Nearsoft/nearbooks) to get the books data.

## Code convention
- Use [this](https://source.android.com/source/code-style.html) code conventions before make any pull request.

## Contribution guidelines
1. Make a fork from this repository.
2. Ensure you are using [Android Studio](http://developer.android.com/intl/es/sdk/index.html) as your IDE.
3. Open Android Studio and click `File` -> `New` -> `Project from Version Control` -> `Git`
4. Paste your fork URL in the `Git Repository Url` field.
5. Do any contribution in the `develop` branch from your fork.
6. Create a pull request from your fork to the [develop branch](https://github.com/Nearsoft/nearbooks-android/tree/develop) of this repository.
   - See [this](http://danielkummer.github.io/git-flow-cheatsheet/) git working flow for more info.

## Used libraries
- [Android data binding](developer.android.com/tools/data-binding/guide.html) for View Model binding.
- [Dagger 2](http://google.github.io/dagger/) for dependency injection.
- [Retrofit 2](http://square.github.io/retrofit/) for consuming REST apis.
- [Picasso](http://square.github.io/picasso/) for images loading.
- [Zxing](https://github.com/journeyapps/zxing-android-embedded) for scanning QR and bar codes.
- [RxAndroid](https://github.com/ReactiveX/RxAndroid) to avoid callback hell and it is fun =p.
- [Realm](https://realm.io/) for storing local data.
- [Crashlytics](https://www.fabric.io) for reporting app crashes.

## Beta testers
#### How to become a beta tester?
1. Go to [this url](https://play.google.com/apps/testing/com.nearsoft.nsbooks) and follow the instructions there with your nearsoft account(`...@nearsoft.com`).
2. Download the application from the [Google play store](https://play.google.com/store/apps/details?id=com.nearsoft.nsbooks).
3. Please report any bug or suggestion [here](https://github.com/Nearsoft/nearbooks-android/issues).
4. Thank you for testing Nearbooks! =D