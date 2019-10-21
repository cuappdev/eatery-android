# eatery-android
Eatery app for Android



# Pushing to the Play Store!

Step 1) Create a new release branch off of master. Navigate to src/res/values/strings.xml.
Update the strings with name "encryption_key", "encryption_salt", and "google_maps_key" with the values pinned in the eatery-android Slack channel.

Step 2) Navigate online to Google Play Console (should be at https://play.google.com/apps/publish/) and sign in with cornellappdev@gmail.com. 

Under all applications, click Eatery, and then under Release management (in the left sidebar), click release dashboard. Then, in the dashboard, click on Manage Release. From this page, note two things, the release number/version name, and the version code. At the time this document was written, the release number/version name was 2.3.2 and the version code was 35.

Step 3) Returning to your release branch, navigate to app/build.gradle. Update the versionCode in the app to be one more than whateve the listed version code was in Google Play Console (i.e. here it would go from 35 to 36), and update the versionName to go from 2.3.2 to 2.4.0 if there is a large change (collegetown eateries), or 2.3.2 to 2.3.3 for a typical update.

Step 4) RUN THE SIMULATOR, CLICK AROUND A LOT, MAKE SURE EVERYTHING WORKS. ESPECIALLY MAKE SURE MAPS WORKS (if it is blank/can't get the locations of eateries it means the maps API key was copied incorrectly)

Step 5) We almost there! In Android Studio, hit Build -> Generate Signed Bundle / APK -> Select Signed Bundle -> Download the eatery key file (also pinned in Eatery-Android) and fill in Key Store path with the path to the file. 

The Key Store password can be found in the Android-Eatery slack as well and the key_alias should be a key with name eatery_key. Click Remember passwords -> next -> select release -> finish.

Step 6) After Gradle builds and the release has been created, the app.aab file (what we will submit to the playstore) should be under eatery-android/app/release. Continuing from where we left off in Google Console, select the blue "MANAGE" button under the production track, click CREATE RELEASE, and then browse files and upload the app.aab files. Release name should be the updated versionName, and put in a message for "What's new in this release?"

Step 7) Push to production
