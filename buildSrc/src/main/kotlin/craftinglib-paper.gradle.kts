plugins {
    id("craftinglib-java")
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

java {
    disableAutoTargetJvm()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:${Versions.PAPER_API}")
}