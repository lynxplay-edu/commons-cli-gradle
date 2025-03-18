import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

plugins {
    java
    jacoco
}

testing.suites.withType<JvmTestSuite>{ useJUnitJupiter() }
java.toolchain.languageVersion = JavaLanguageVersion.of(21)
tasks.withType<JavaCompile> { options.encoding = Charsets.UTF_8.name() }
tasks.withType<ProcessResources> { filteringCharset = Charsets.UTF_8.name() }

repositories {
    mavenCentral()
}

tasks.withType<AbstractTestTask> { ignoreFailures = true }
tasks.jacocoTestReport {
    reports.csv.required = true
    executionData(
        allSentraTests().map { layout.buildDirectory.file("jacoco/${sentraTask(it)}.exec") }
    )
    dependsOn(allSentraTests().map { tasks.getByName(sentraTask(it)) })
}
testing {
    suites {
        allSentraTests().forEach {
            register<JvmTestSuite>(sentraTask(it)) {
                sources.java.setSrcDirs(listOf(sentraWorkDir().resolve("tests").resolve(it.name)))
            }
        }

        withType<JvmTestSuite> {
            useJUnitJupiter()
            configurations.named(sources.implementationConfigurationName) {
                extendsFrom(configurations.getByName(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME))
            }
            dependencies { implementation(project()) }
        }
    }
}

fun sentraWorkDir(): Path {
    return Path.of((properties["sentra.workdir"] ?: "build/sentra") as String)
}

fun allSentraTests(): List<Path> {
    return sentraWorkDir().resolve("tests").let {
        if (it.exists()) it.listDirectoryEntries() else emptyList()
    }
}

fun sentraTask(path: Path): String {
    return "sentraTest${path.name.replace("-", "")}"
}
