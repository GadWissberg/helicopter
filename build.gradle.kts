buildscript {
    val androidPluginVersion: String by project
    val kotlinVersion: String by project
    repositories {
        gradlePluginPortal()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:$androidPluginVersion")
        classpath(kotlin("gradle-plugin", kotlinVersion))
    }
}

plugins {
    base
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.clean {
    delete(buildDir)
}