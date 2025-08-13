import Versions.JAVA_VERSION

plugins {
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.forClassVersion(JAVA_VERSION)
    targetCompatibility = JavaVersion.forClassVersion(JAVA_VERSION)
}

repositories {
    mavenCentral()
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src"))
        resources.setSrcDirs(emptyList<String>())
    }
    test {
        java.setSrcDirs(emptyList<String>())
        resources.setSrcDirs(emptyList<String>())
    }
}

