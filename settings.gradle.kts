dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {

            version("selenide", "7.17.0")
            version("testng", "7.12.0")
            version("postgresql", "42.7.13")
            version("restAssured", "6.0.1")
            version("allurePlugin", "3.1.0")
            version("allure", "2.25.0")
            version("aspectj", "1.9.22")
            version("datafaker", "2.2.2")
            version("poi", "5.2.5")

            library("selenide", "com.codeborne", "selenide").versionRef("selenide")
            library("testng", "org.testng", "testng").versionRef("testng")
            library("postgresql", "org.postgresql", "postgresql").versionRef("postgresql")
            library("rest-assured", "io.rest-assured", "rest-assured").versionRef("restAssured")
            library("aspectjweaver", "org.aspectj", "aspectjweaver").versionRef("aspectj")
            library("allure-rest-assured", "io.qameta.allure", "allure-rest-assured")
                .versionRef("allure")
            library("datafaker", "net.datafaker", "datafaker").versionRef("datafaker")
            library("poi-ooxml", "org.apache.poi", "poi-ooxml").versionRef("poi")

            plugin("allure", "io.qameta.allure").versionRef("allurePlugin")
        }
    }
}

rootProject.name = "PR-CY"