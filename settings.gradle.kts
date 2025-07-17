rootProject.name = "SkyBlockRPC"

pluginManagement {
    repositories {
        maven(url = "https://maven.teamresourceful.com/repository/msrandom/")
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs")
    }
}
