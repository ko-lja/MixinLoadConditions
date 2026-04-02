plugins {
    `java-library`
    `maven-publish`
}

group = "lu.kolja"
version = "0.1.2"

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven")
    maven("https://maven.minecraftforge.net/")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }

    withSourcesJar()
    withJavadocJar()
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.5")
    compileOnly("org.ow2.asm:asm:9.5")
    compileOnly("org.ow2.asm:asm-tree:9.5")
    compileOnly("net.minecraftforge:forge:1.20.1-47.2.0:universal")
    compileOnly("net.minecraftforge:fmlloader:1.20.1-47.2.0")
    compileOnly("net.minecraftforge:fmlcore:1.20.1-47.2.0")
    compileOnly("net.minecraftforge:javafmllanguage:1.20.1-47.2.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveBaseName.set("mixinloadconditions")
    manifest.attributes(
        "MixinConfigs" to "mixinloadconditions.init.mixins.json",
        "FMLModType" to "GAMELIBRARY",
        "Automatic-Module-Name" to "mixinloadconditions",
        "Implementation-Version" to project.version,
    )
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = group as String
            artifactId = "mixinloadconditions"
            version = version as String

            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "expandiumReleases"
            url = uri("https://repo.expandium.net/releases")
            credentials {
                username = project.findProperty("repoUser") as String
                password = project.findProperty("repoToken") as String
            }
        }
    }
}
