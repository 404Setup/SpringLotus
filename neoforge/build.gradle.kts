import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `maven-publish`
    id("libsl-publish")
    alias(libs.plugins.moddev)
}

repositories {
}

dependencies {
    jarJar(rootProject.libs.snakeyaml)
    implementation(rootProject.libs.snakeyaml)
    jarJar(rootProject.libs.pkg.sewlia.config)
    implementation(rootProject.libs.pkg.sewlia.config)
    jarJar(libs.pkg.tinyutils)
    implementation(libs.pkg.tinyutils)
}

tasks {
    jarJar {
        enabled = true
    }

    named<Jar>("jar") {
        manifest {
            attributes["Automatic-Module-Name"] = "one.pkg.libsl.neoforge"
        }
    }

    withType<Javadoc> {
        val o = options as StandardJavadocDocletOptions
        o.encoding = "UTF-8"
        o.source = "25"

        o.use()
    }
}

neoForge {
    version = rootProject.property("neoforge_version")!! as String

    val at = project(":modcommon").file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }

    mods {
        create("libsl") {
            sourceSet(sourceSets.main.get())
        }
    }
}
