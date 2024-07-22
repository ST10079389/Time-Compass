pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        /*
         This code was taken from a Stackoverflow post
         Uploaded by: FloatOverflow
         Titled: Android Studio maven { url "https://jitpack.io" } can't download
         Available at: https://stackoverflow.com/questions/50389211/android-studio-maven-url-https-jitpack-io-cant-download#52812651
         Accessed: 23 May 2024
        */
        maven { url = uri("https://www.jitpack.io" ) }
    }
}

rootProject.name = "Time-Compass(OPSC7311-Part1)"
include(":app")
 