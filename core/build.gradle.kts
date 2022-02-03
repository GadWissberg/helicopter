plugins {
    kotlin("jvm")
}

dependencies {
    val gdxVersion: String by project
    val ktxVersion: String by project
    val ashleyVersion: String by project
    val junitVersion: String by project

    implementation(kotlin("stdlib"))
    implementation("com.badlogicgames.ashley:ashley:$ashleyVersion")
    implementation("com.badlogicgames.gdx:gdx-freetype:$gdxVersion")

    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.21")

    // LibKTX kotlin extensions, optional but recommended.
    // The complete list of modules is available at https://github.com/libktx/ktx
    implementation("io.github.libktx:ktx-actors:$ktxVersion")
    implementation("io.github.libktx:ktx-assets:$ktxVersion")
    implementation("io.github.libktx:ktx-collections:$ktxVersion")
    implementation("io.github.libktx:ktx-math:$ktxVersion")
    implementation("io.github.libktx:ktx-log:$ktxVersion")
    implementation("io.github.libktx:ktx-style:$ktxVersion")
    implementation("io.github.libktx:ktx-freetype:$ktxVersion")
    testImplementation("junit:junit:$junitVersion")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
    implementation("com.google.code.gson:gson:2.8.7")
    testImplementation("io.mockk:mockk:1.12.0")

}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}