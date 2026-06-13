plugins {
    java
    `maven-publish`
}

extensions.configure<PublishingExtension> {
    repositories {
        maven {
            credentials(PasswordCredentials::class.java) {
                username = System.getenv("REPO_USER")
                password = System.getenv("REPO_PASS")
            }

            name = if (version.toString().endsWith("SNAPSHOT")) "libslAlpha" else "libsl"
            val base = "https://mvnc.pkg.one"
            val releasesRepoUrl = "$base/releases/"
            val snapshotsRepoUrl = "$base/snapshots/"
            setUrl(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name.set("SpringLotus")
                description.set("A Minecraft dependency library")
                url.set("https://modrinth.com/mod/SpringLotus")
                scm {
                    url.set("https://github.com/404Setup/SpringLotus")
                    connection.set("scm:git:https://github.com/404Setup/SpringLotus.git")
                    developerConnection.set("scm:git:https://github.com/404Setup/SpringLotus.git")
                }
            }
        }
    }
}
