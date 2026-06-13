plugins {
    `maven-publish`
    id("libsl-publish")
    alias(libs.plugins.moddev)
}

neoForge {
    neoFormVersion = "${rootProject.property("neoform_version")!!}"
    val at = file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }
}

tasks {
    withType<Javadoc> {
        val o = options as StandardJavadocDocletOptions
        o.encoding = "UTF-8"
        o.source = "25"

        o.use()
    }
}

dependencies {
    compileOnly("net.fabricmc:sponge-mixin:0.17.0+mixin.0.8.7")
    compileOnly("org.ow2.asm:asm-tree:9.9")
    compileOnly("io.github.llamalad7:mixinextras-common:0.5.3")
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.3")

    compileOnly(libs.snakeyaml)
    compileOnly(libs.pkg.sewlia.config)
    compileOnly(libs.pkg.tinyutils)
    compileOnly(libs.adventure.mod)
}

artifacts {
    add("commonJava", sourceSets.main.get().java.sourceDirectories.singleFile)
    add("commonResources", sourceSets.main.get().resources.sourceDirectories.singleFile)
}