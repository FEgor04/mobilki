val slf4j_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm")
}

group = "com.koji"
version = "unspecified"


dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("org.slf4j:slf4j-api:$slf4j_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}