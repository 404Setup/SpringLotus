plugins {
    `maven-publish`
    id("libsl-publish")
    alias(libs.plugins.fg.main)
    alias(libs.plugins.fg.rm)
}

val modId = rootProject.property("mod_id")!! as String
val minecraftVersion = rootProject.property("minecraft_version")!! as String
val forgeVersion = rootProject.property("forge_version")!! as String

renamer.enableMixinRefmaps {
    config("$modId.mixins.json")
}

renamer.classes(tasks.named<Jar>("jar")) {
    archiveClassifier.set("srg")
    mappings(renamer.mixin.generatedMappings)
}

minecraft {
    mappings("official", minecraftVersion)
    useDefaultAccessTransformer()
}

repositories {
    minecraft.mavenizer(this)
}

dependencies {
    implementation(minecraft.dependency("net.minecraftforge:forge:$minecraftVersion-$forgeVersion"))
    
    annotationProcessor("org.spongepowered:mixin:0.8.7:processor")

    compileOnly("net.fabricmc:sponge-mixin:0.17.0+mixin.0.8.7")
    compileOnly("io.github.llamalad7:mixinextras-common:0.5.4")
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.4")
    compileOnly("org.ow2.asm:asm-tree:9.9")

    implementation(rootProject.libs.snakeyaml)
    implementation(rootProject.libs.pkg.sewlia.config)
    implementation(libs.pkg.tinyutils)
}

renamer.mappings(minecraft.dependency.toSrg)

tasks {
    named<Jar>("jar") {
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
