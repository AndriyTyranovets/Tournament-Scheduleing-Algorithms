plugins {
    java
}

group = "com.github.atyranovets"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.guava:guava:29.0-jre")
    implementation("org.apache.commons:commons-lang3:3.11")
    implementation("org.apache.commons:commons-collections4:4.4")
    testImplementation("junit:junit:4.12")
    testImplementation("org.mockito:mockito-all:1.10.19")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}
