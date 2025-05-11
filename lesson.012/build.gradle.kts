plugins {
    id("java")
}

group = "aHalpukov"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("com.codeborne:selenide:7.9.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.27.3")
    testImplementation("org.seleniumhq.selenium:selenium-java:4.32.0")
    testImplementation("org.apache.httpcomponents.client5:httpclient5:5.4.4")
    testImplementation("io.qameta.allure:allure-junit4:2.29.1")
    implementation("com.microsoft.playwright:playwright:1.52.0")
}

tasks.test {
    useJUnitPlatform()
}