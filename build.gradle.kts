import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    alias(libs.plugins.loom).apply(false)
    alias(libs.plugins.paperweight).apply(false)
    alias(libs.plugins.shadow).apply(false)
    alias(libs.plugins.moddev).apply(false)
}

fun Project.string(key: String): String? = property(key) as? String

allprojects {
    apply<JavaLibraryPlugin>()
    apply<ShadowPlugin>()

    configurations.named("shadow") {
        isTransitive = true
    }

    group = project.property("group")!! as String
    version = project.property("version")!! as String

    val targetJavaVersion = project.property("java_version")!! as String

    java {
        val javaVersion = JavaVersion.toVersion(targetJavaVersion)
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        if (JavaVersion.current() < javaVersion) {
            toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
        }
    }

    base {
        archivesName.set("${project.string("archives_base_name")}-${project.name}")
    }

    tasks.named<Jar>("jar") {
        from(rootProject.file("LICENSE")) {
            rename { "${it}_${project.string("mod_name")!!}" }
        }

        if (project.name == "modcommon") {
            manifest {
                attributes(
                    "Specification-Title" to project.string("mod_name")!!,
                    "Specification-Vendor" to project.string("mod_author")!!,
                    "Specification-Version" to archiveVersion.get(),
                    "Implementation-Title" to project.name,
                    "Implementation-Version" to archiveVersion.get(),
                    "Implementation-Vendor" to project.string("mod_author")!!,
                    "Built-On-Minecraft" to rootProject.string("minecraft_version")!!
                )
            }
        }
    }

    tasks.withType<Test>().configureEach {
        enabled = false
    }

    tasks.withType<JavaCompile>().configureEach {
        if (name.contains("Test", ignoreCase = true)) {
            enabled = false
        }
    }

    tasks.named<ProcessResources>("processResources") {
        val expandProps = mapOf(
            "version" to version,
            "group" to project.group,
            "minecraft_version" to rootProject.string("minecraft_version")!!,
            "minecraft_version_range" to project.string("minecraft_version_range")!!,
            "mod_name" to project.string("mod_name")!!,
            "mod_author" to project.string("mod_author")!!,
            "mod_id" to project.string("mod_id")!!,
            "license" to project.string("license")!!,
            "description" to project.description,
            "homepage_url" to project.string("homepage_url")!!,
            "issues_url" to project.string("issues_url")!!,
            "update_url" to project.string("update_url")!!,
            "neoforge_version" to project.string("neoforge_version")!!,
            "neoforge_version_range" to project.string("neoforge_version_range")!!,
            "neoforge_loader_version_range" to project.string("neoforge_loader_version_range")!!,
            "fabric_loader_version" to project.string("fabric_loader_version")!!,
            "fabric_api_version" to project.string("fabric_api_version")!!,
            "credits" to project.string("credits")!!,
            "java_version" to project.string("java_version")!!
        )

        val jsonExpandProps = expandProps.mapValues { (_, value) ->
            if (value is String) value.replace("\n", "\\n") else value
        }

        filesMatching(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml")) {
            expand(expandProps)
        }

        filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "*.mixins.json")) {
            expand(jsonExpandProps)
        }

        inputs.properties(expandProps)
    }

    tasks.withType<JavaCompile>().configureEach {
        val target = targetJavaVersion.toInt()
        if (target >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(target)
        }

        options.compilerArgs.add("-Xdiags:verbose")
    }

    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://libraries.minecraft.net")
        exclusiveContent {
            forRepository {
                maven("https://repo.spongepowered.org/repository/maven-public") { name = "Sponge" }
            }
            filter { includeGroupAndSubgroups("org.spongepowered") }
        }
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases")
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")
        maven("https://mvnc.pkg.one/releases")
        maven("https://mvnc.pkg.one/snapshots")
    }

    val finalProjects = listOf("fabric", "neoforge")

    if (project.name in finalProjects) {
        tasks.named<ShadowJar>("shadowJar") {
            configurations = listOf(project.configurations["shadow"])
            exclude("META-INF/versions/**")
            exclude("*.html")
            exclude("*.txt")
            exclude("module-info.class")
            exclude("META-INF/MANIFEST.MF")
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
    } else {
        tasks.withType<ShadowJar>().configureEach {
            enabled = false
        }
    }
}

subprojects {
    apply(plugin = "java-library")

    val commonJava = configurations.create("commonJava") {
        isCanBeResolved = true
        isCanBeConsumed = true
    }
    val commonResources = configurations.create("commonResources") {
        isCanBeResolved = true
        isCanBeConsumed = true
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }

    tasks {
        withType<JavaCompile>().configureEach {
            dependsOn(commonJava)
            source(commonJava)
        }

        withType<ProcessResources>().configureEach {
            dependsOn(commonResources)
            from(commonResources)
        }

        withType<Jar>().configureEach {
            if (name == "sourcesJar") {
                dependsOn(commonJava, commonResources)
                from(commonJava, commonResources)
            }
        }
    }
}

configure(listOf(project(":neoforge"), project(":fabric"))) {
    dependencies {
        "commonJava"(project(path = ":modcommon", configuration = "commonJava"))
        "commonResources"(project(path = ":modcommon", configuration = "commonResources"))
        compileOnly(project(":modcommon"))
    }
}