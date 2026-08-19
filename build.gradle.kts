plugins {
    id("java")
    alias(libs.plugins.allure)
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val agent: Configuration by configurations.creating

allure {
    version.set(libs.versions.allure.get())
}


dependencies {
    implementation(libs.selenide)
    implementation(libs.postgresql)

    testImplementation(libs.testng)
    testImplementation(libs.rest.assured)
    testImplementation(libs.allure.rest.assured)
    testImplementation(libs.aspectjweaver)
    testImplementation(libs.datafaker)
    testImplementation(libs.poi.ooxml)

    agent(libs.aspectjweaver)
}

tasks.test {
    useTestNG {
        useDefaultListeners = true
    }

    setIncludes(listOf("**/*Test.class", "**/*Tests.class"))

    doFirst {
        jvmArgs("-javaagent:${agent.singleFile}")
    }

    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.clean {
    delete(layout.buildDirectory.dir("allure-results"))
}

