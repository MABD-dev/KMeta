plugins {
    kotlin("jvm") version "2.0.21"
    id("com.google.devtools.ksp") version "2.0.21-1.0.27"
    id("com.diffplug.spotless") version "7.0.4"
}

group = "org.mabd"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:2.0.21-1.0.27")

    ksp(project(":processor"))
    implementation(project(":processor"))

    testImplementation(kotlin("test"))
}


tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
    sourceSets.main {
        kotlin.srcDirs("build/generated/ksp/main/kotlin")
    }
}

spotless {
    format("misc") {
        target("*.gradle", ".gitignore")

        trimTrailingWhitespace()
        leadingSpacesToTabs(4)
        endWithNewline()
    }

    kotlin {
        target("**/*.kt")

        ktlint()
    }
}