// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

// Force a modern JavaPoet for all classpaths (including buildscript/plugin)
buildscript {
    configurations.all {
        resolutionStrategy {
            force("com.squareup:javapoet:${libs.versions.javapoet.get()}")
        }
    }
}