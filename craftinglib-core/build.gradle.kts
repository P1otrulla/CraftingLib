plugins {
    //`craftinglib-core`
    `java-library`
}

group = "dev.piotrulla.craftinglib"
version = Versions.PROJECT_VERSION

repositories {
    mavenCentral()
}

dependencies {
    api("org.jetbrains:annotations:${Versions.JETBRAINS_ANNOTATIONS}")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}