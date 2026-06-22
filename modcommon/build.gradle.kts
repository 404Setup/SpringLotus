plugins {
    `maven-publish`
    id("libsl-publish")
    alias(libs.plugins.moddev)
}

val mcVersion = rootProject.property("minecraft_version")!! as String

legacyForge {
    mcpVersion = mcVersion
    val at = file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }
    parchment {
        minecraftVersion = mcVersion
        mappingsVersion = "${rootProject.property("parchment_version")!!}"
    }
}

tasks {
    withType<Javadoc> {
        val o = options as StandardJavadocDocletOptions
        o.encoding = "UTF-8"
        o.source = "17"

        o.use()
    }
}

dependencies {
    compileOnly("net.fabricmc:sponge-mixin:0.17.0+mixin.0.8.7")
    compileOnly("org.ow2.asm:asm-tree:9.9")
    compileOnly("io.github.llamalad7:mixinextras-common:0.5.4")
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.4")

    compileOnly(libs.snakeyaml)
    compileOnly(libs.pkg.sewlia.config)
    compileOnly(libs.pkg.tinyutils)
}

artifacts {
    add("commonJava", sourceSets.main.get().java.sourceDirectories.singleFile)
    add("commonResources", sourceSets.main.get().resources.sourceDirectories.singleFile)
}