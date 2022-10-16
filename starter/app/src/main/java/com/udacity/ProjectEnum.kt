package com.udacity

enum class ProjectEnum(val url: String, val fileName: String) {
    GLIDE(
        "https://github.com/bumptech/glide/archive/master.zip",
        "Glide - Image Loading Library by BumpTech"
    ),
    LOAD_APP(
        "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip",
        "LoadApp - Current repository by Udacity"
    ),
    RETROFIT(
        "https://github.com/square/retrofit/archive/master.zip",
        "Retrofit - Type-safe HTTP client for Android and Java by Square, Inc"
    )
}