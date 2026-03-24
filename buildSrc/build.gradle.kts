plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    maven("https://maven.teamresourceful.com/repository/maven-public/")
}

fun plugin(provider: Provider<PluginDependency>): Provider<String> = provider.map {
    "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}"
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin.api)
    implementation(plugin(libs.plugins.kotlin.symbol.processor))
    implementation(plugin(libs.plugins.fabric.loom.asProvider()))
    implementation(plugin(libs.plugins.fabric.loom.remap))
    implementation("dev.kikugie.stonecutter:dev.kikugie.stonecutter.gradle.plugin:0.8.3")
}
