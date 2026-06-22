plugins {
    `maven-publish`
    id("libsl-publish")
    alias(libs.plugins.loom)
}

val modId = rootProject.property("mod_id")!! as String
val minecraftVersion = rootProject.property("minecraft_version")!! as String
val fabricApiVersion = rootProject.property("fabric_api_version")!! as String

loom {
    val aw = project(":modcommon").file("src/main/resources/$modId.accesswidener")
    if (aw.exists()) {
        accessWidenerPath = aw
    }
}

tasks {
    named<Jar>("jar") {
        manifest {
            attributes["Automatic-Module-Name"] = "one.pkg.libsl.fabric"
        }
    }

    withType<Javadoc> {
        val o = options as StandardJavadocDocletOptions
        o.encoding = "UTF-8"
        o.source = "17"

        o.use()
    }
}


dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")!!}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion+$minecraftVersion")

    compileOnly("net.fabricmc:sponge-mixin:0.17.0+mixin.0.8.7")
    compileOnly("io.github.llamalad7:mixinextras-common:0.5.4")
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.4")
    compileOnly("org.ow2.asm:asm-tree:9.9")

    include(rootProject.libs.snakeyaml)
    implementation(rootProject.libs.snakeyaml)
    include(rootProject.libs.pkg.sewlia.config)
    implementation(rootProject.libs.pkg.sewlia.config)
    include(libs.pkg.tinyutils)
    implementation(libs.pkg.tinyutils)
}
