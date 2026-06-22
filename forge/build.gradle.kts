plugins {
    `maven-publish`
    id("libsl-publish")
    alias(libs.plugins.fg.main)
    alias(libs.plugins.fg.rm)
    alias(libs.plugins.fg.jar)
}

val modId = rootProject.property("mod_id")!! as String
val minecraftVersion = rootProject.property("minecraft_version")!! as String
val forgeVersion = rootProject.property("forge_version")!! as String

renamer.enableMixinRefmaps {
    config("$modId.mixins.json")
}

minecraft {
    mappings("official", minecraftVersion)
    useDefaultAccessTransformer()
}

repositories {
    minecraft.mavenizer(this)
}

jarJar.register() {
    archiveClassifier = "dev"
}

tasks.named<Jar>("jarJar") {
    from(configurations.getByName("jarJarClasspath")) {
        into("META-INF/jarjar")
    }
}

renamer.classes(tasks.named<Jar>("jarJar")) {
    archiveClassifier.set("")
    archiveExtension.set("jar")
    mappings(renamer.mixin.generatedMappings)
}

dependencies {
    implementation(minecraft.dependency("net.minecraftforge:forge:$minecraftVersion-$forgeVersion"))
    
    annotationProcessor("org.spongepowered:mixin:0.8.7:processor")

    compileOnly("net.fabricmc:sponge-mixin:0.17.0+mixin.0.8.7")
    compileOnly("io.github.llamalad7:mixinextras-common:0.5.4")
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.4")
    compileOnly("org.ow2.asm:asm-tree:9.9")

    implementation(rootProject.libs.snakeyaml)
    "jarJar"(rootProject.libs.snakeyaml.get().toString()) {
        jarJar.configure(this) {
            setConstraint(true)
            setVersion(rootProject.libs.snakeyaml.get().version)
        }
    }

    implementation(rootProject.libs.pkg.sewlia.config)
    "jarJar"(rootProject.libs.pkg.sewlia.config.get().toString()) {
        jarJar.configure(this) {
            setConstraint(true)
            setVersion(rootProject.libs.pkg.sewlia.config.get().version)
        }
    }

    implementation(libs.pkg.tinyutils)
    "jarJar"(libs.pkg.tinyutils.get().toString()) {
        jarJar.configure(this) {
            setConstraint(true)
            setVersion(libs.pkg.tinyutils.get().version)
        }
    }
}

renamer.mappings(minecraft.dependency.toSrg)

tasks {
    named<Jar>("jar") {
        archiveClassifier = "slim"
        manifest {
            attributes["Automatic-Module-Name"] = "one.pkg.libsl.neoforge"
        }
    }

    withType<Javadoc> {
        val o = options as StandardJavadocDocletOptions
        o.encoding = "UTF-8"
        o.source = "17"

        o.use()
    }
}
tasks.named<ProcessResources>("processResources").configure {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<Jar>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}