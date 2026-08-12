allprojects {
    group = "com.jazzkuh.commandlib"
    version = "1.0-SNAPSHOT"
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(25)
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        if (project.name in listOf("spigot", "common")) {
            options.release.set(21)
        }
        options.compilerArgs.add("-parameters")
    }

    dependencies {
        "compileOnly"("org.projectlombok:lombok:1.18.42")
        "annotationProcessor"("org.projectlombok:lombok:1.18.42")
        "api"("org.jetbrains:annotations:23.0.0")
        "testImplementation"("junit:junit:4.13.2")
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }

        repositories {
            maven {
                name = "deepsite"
                url = uri(
                    if (version.toString().endsWith("SNAPSHOT"))
                        "https://maven.deepsite.gg/snapshots"
                    else
                        "https://maven.deepsite.gg/releases"
                )
                credentials {
                    username = System.getenv("DEEPSITE_MAVEN_NAME")
                        ?: project.findProperty("deepsiteUsername") as String?
                    password = System.getenv("DEEPSITE_MAVEN_SECRET")
                        ?: project.findProperty("deepsitePassword") as String?
                }
                // Reposilite expects preemptive basic auth
                authentication {
                    create<BasicAuthentication>("basic")
                }
            }
        }
    }
}
