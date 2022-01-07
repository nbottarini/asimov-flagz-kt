import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("maven-publish")
    id("signing")
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

val nexusUsername: String? by project
val nexusPassword: String? by project

group = "com.nbottarini"
version = "0.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.nbottarini:asimov-environment:1.0.1")
    implementation("com.nbottarini:asimov-time:0.5.1")
    implementation("com.google.code.gson:gson:2.8.9")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation("com.h2database:h2:2.0.206")
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

kotlin {
    sourceSets["main"].apply {
        kotlin.srcDirs("src")
        resources.srcDirs("resources")
    }
    sourceSets["test"].apply {
        kotlin.srcDir("test")
        resources.srcDir("test_resources")
    }
}

java {
    withJavadocJar()
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    sourceSets["main"].apply {
        java.srcDirs("src")
        resources.srcDirs("resources")
    }
    sourceSets["test"].apply {
        java.srcDir("test")
        resources.srcDir("test_resources")
    }
}

tasks.withType<Test> { useJUnitPlatform() }

tasks.withType<Test>().configureEach {
    reports.html.required.set(false)
    reports.junitXml.required.set(false)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "asimov-flagz"
            from(components["java"])

            pom {
                name.set("Flagz")
                description.set("Feature flags library")
                url.set("https://github.com/nbottarini/asimov-flagz-kt")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("http://www.opensource.org/licenses/mit-license.php")
                    }
                }

                developers {
                    developer {
                        id.set("nbottarini")
                        name.set("Nicolas Bottarini")
                        email.set("nicolasbottarini@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/asimov-flagz-kt.git")
                    developerConnection.set("scm:git:ssh://github.com/asimov-flagz-kt.git")
                    url.set("https://github.com/nbottarini/asimov-flagz-kt")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(nexusUsername)
            password.set(nexusPassword)
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}
