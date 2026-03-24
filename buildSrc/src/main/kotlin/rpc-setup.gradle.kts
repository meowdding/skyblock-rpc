import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.ValidateAccessWidenerTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("idea")
    id("versioned-catalogues")
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

private val stonecutter = project.extensions.getByName("stonecutter") as dev.kikugie.stonecutter.build.StonecutterBuildExtension
fun isUnobfuscated() = stonecutter.eval(stonecutter.current.version, ">=26.1")

repositories {
    fun scopedMaven(url: String, vararg paths: String) = maven(url) { content { paths.forEach(::includeGroupAndSubgroups) } }

    scopedMaven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1", "me.djtheredstoner")
    scopedMaven("https://repo.hypixel.net/repository/Hypixel", "net.hypixel")
    scopedMaven("https://maven.parchmentmc.org/", "org.parchmentmc")
    scopedMaven("https://api.modrinth.com/maven", "maven.modrinth")
    scopedMaven(
        "https://maven.teamresourceful.com/repository/maven-public/",
        "earth.terrarium",
        "com.teamresourceful",
        "tech.thatgravyboat",
        "me.owdding",
        "com.terraformersmc",
        "com.jagrosh"
    )
    scopedMaven("https://maven.nucleoid.xyz/", "eu.pb4")
    scopedMaven(url = "https://maven.shedaniel.me/", "me.shedaniel", "dev.architectury")
    mavenCentral()
}

fun makeAlias(configuration: String) = if (isUnobfuscated()) configuration else "mod" + configuration.replaceFirstChar { it.uppercase() }

val maybeModImplementation = makeAlias("implementation")
val maybeModCompileOnly = makeAlias("compileOnly")
val maybeModRuntimeOnly = makeAlias("runtimeOnly")
val maybeModApi = makeAlias("api")

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    compilerOptions.optIn.add("kotlin.time.ExperimentalTime")
    compilerOptions.freeCompilerArgs.addAll(
        "-Xcontext-parameters",
        "-Xcontext-sensitive-resolution",
        "-Xnullability-annotations=@org.jspecify.annotations:warn"
    )
}
val mcVersion = stonecutter.current.version.replace(".", "")
val loom = extensions.getByName<LoomGradleExtensionAPI>("loom")
loom.apply {
    runConfigs["client"].apply {
        ideConfigGenerated(true)
        runDir = "../../run"
        vmArg("-Dfabric.modsFolder=" + '"' + rootProject.projectDir.resolve("run/${mcVersion}Mods").absolutePath + '"')
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(if (isUnobfuscated()) 25 else 21)
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(if (isUnobfuscated()) 25 else 21)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(if (isUnobfuscated()) JvmTarget.JVM_25 else JvmTarget.JVM_21)
    compilerOptions.optIn.add("kotlin.time.ExperimentalTime")
    compilerOptions.freeCompilerArgs.add(
        "-Xnullability-annotations=@org.jspecify.annotations:warn"
    )
}

tasks.processResources {
    val range = if (versionedCatalog.versions.has("minecraft.range")) {
        versionedCatalog.versions.get("minecraft.range").toString()
    } else {
        val start = versionedCatalog.versions.getOrFallback("minecraft.start", "minecraft")
        val end = versionedCatalog.versions.getOrFallback("minecraft.end", "minecraft")
        ">=$start <=$end"
    }
    val replacements = mapOf(
        "version" to version,
        "minecraft_range" to range,
        "fabric_lang_kotlin" to versionedCatalog.versions["fabric.language.kotlin"],
        "rlib" to versionedCatalog.versions["resourceful-lib"],
        "olympus" to versionedCatalog.versions["olympus"],
        "sbapi" to versionedCatalog.versions["skyblockapi"],
        "mlib" to versionedCatalog.versions["meowdding.lib"],
        "rconfigkt" to versionedCatalog.versions["rconfigkt"],
        "rconfig" to versionedCatalog.versions["resourceful-config"],
    )
    inputs.properties(replacements)

    filesMatching("fabric.mod.json") {
        expand(replacements)
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true

        excludeDirs.add(file("run"))
    }
}

tasks.withType<ProcessResources>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    with(copySpec {
        from(rootProject.file("src/lang")).include("*.json").into("assets/skyblockrpc/lang")
    })
}

val archiveName = "SkyBlockRpc"

base {
    archivesName.set("$archiveName-${archivesName.get()}")
}

tasks.withType<ValidateAccessWidenerTask> { enabled = false }

tasks.named<Jar>("jar") {
    archiveBaseName = archiveName
    archiveClassifier = "${stonecutter.current.version}-dev"
}

tasks.named<Jar>("sourcesJar") {
    archiveBaseName = archiveName
    archiveClassifier = "${stonecutter.current.version}-sources"
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.named("build") {
    doLast {
        val sourceFile = rootProject.projectDir.resolve(
            if (isUnobfuscated()) "versions/${project.name}/build/libs/${archiveName}-$version-${stonecutter.current.version}-dev.jar"
            else "versions/${project.name}/build/libs/${archiveName}-${stonecutter.current.version}-$version.jar"
        )
        val targetFile = rootProject.projectDir.resolve("build/libs/${archiveName}-$version-${stonecutter.current.version}.jar")
        targetFile.parentFile.mkdirs()
        targetFile.writeBytes(sourceFile.readBytes())
    }
}

dependencies {
    "minecraft"(versionedCatalog["minecraft"])

    maybeModImplementation(versionedCatalog["fabric.loader"])
    maybeModImplementation(versionedCatalog["fabric.language.kotlin"])
    maybeModImplementation(versionedCatalog["fabric.api"])

    api(versionedCatalog["skyblockapi"]) {
        capabilities { requireCapability("tech.thatgravyboat:skyblock-api-${stonecutter.current.version}") }
    }
    "include"(versionedCatalog["skyblockapi"]) {
        capabilities { requireCapability("tech.thatgravyboat:skyblock-api-${stonecutter.current.version}${"-remapped".takeIf { !isUnobfuscated() } ?: ""}") }
    }
    api(versionedCatalog["meowdding.lib"]) {
        capabilities { requireCapability("me.owdding.meowdding-lib:meowdding-lib-${stonecutter.current.version}") }
    }
    "include"(versionedCatalog["meowdding.lib"]) {
        capabilities { requireCapability("me.owdding.meowdding-lib:meowdding-lib-${stonecutter.current.version}${"-remapped".takeIf { !isUnobfuscated() } ?: ""}") }
    }

    maybeModImplementation(versionedCatalog["hypixelapi"])

    includeImplementation(versionedCatalog["resourceful.lib"])
    includeImplementation(versionedCatalog["resourceful.config"])
    includeImplementation(versionedCatalog["resourcefulkt.config"])

    maybeModImplementation(versionedCatalog["placeholders"])
    maybeModImplementation(versionedCatalog["olympus"])

    includeImplementation(versionedCatalog["discordipc"])
}

fun DependencyHandlerScope.includeImplementation(dep: Any) {
    "include"(dep)
    maybeModImplementation(dep)
}
