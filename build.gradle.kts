plugins {
    java
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)
tasks.withType<JavaCompile> { options.encoding = Charsets.UTF_8.name() }
tasks.withType<ProcessResources> { filteringCharset = Charsets.UTF_8.name() }

repositories {
    mavenCentral()
}