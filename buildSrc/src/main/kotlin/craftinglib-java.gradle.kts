// Base for every published module: Java 11 bytecode, Maven Central only.
plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

java {
    // javac 21 (paper-api classes are Java 21), bytecode still Java 11 via options.release
    toolchain.languageVersion.set(JavaLanguageVersion.of(Versions.JAVA_TOOLCHAIN))
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    compileOnlyApi("org.jetbrains:annotations:${Versions.JETBRAINS_ANNOTATIONS}")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(Versions.JAVA_RELEASE)
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-processing", "-Xlint:-serial"))
}

tasks.withType<Javadoc>().configureEach {
    options.encoding = "UTF-8"
    (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
}
