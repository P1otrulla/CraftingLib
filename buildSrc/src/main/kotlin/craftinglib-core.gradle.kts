plugins {
    id("craftinglib-java")
}

dependencies {
    api("org.jetbrains:annotations:${Versions.JETBRAINS_ANNOTATIONS}")
}

java {
    withSourcesJar()
    withJavadocJar()
}