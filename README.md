[![Maven](https://img.shields.io/maven-central/v/com.nbottarini/asimov-flagz.svg)](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.nbottarini%22%20AND%20a%3A%22asimov-flagz%22)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![CI Status](https://github.com/nbottarini/asimov-flagz-kt/actions/workflows/gradle.yml/badge.svg?branch=main)](https://github.com/nbottarini/asimov-flagz-kt/actions?query=branch%3Amain+workflow%3Aci)

# Asimov Flagz
Feature flags library based on Togglz library.

## Installation

#### Gradle (Kotlin)

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.nbottarini:asimov-flagz:0.2.1")
}
```

#### Gradle (Groovy)

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.nbottarini:asimov-flagz:0.2.1'
}
```

#### Maven

```xml
<dependency>
    <groupId>com.nbottarini</groupId>
    <artifactId>asimov-flagz</artifactId>
    <version>0.2.1</version>
</dependency>
```

## Quick start

1) Define an enum with your feature flags (it must implement the Feature interface):
```kotlin
enum class Features: Feature {
    MY_FEATURE,
    MY_OTHER_FEATURE
}
```

2) Initialize the library by configuring your enum:
```kotlin
initFlagz {
    featureEnum<Features>()
    repositories(EnvironmentFeatureRepository())
}
```
You'll have to configure one or more feature repositories. A repository provides a way to store and search for
feature flag states (enabled or disabled).

In this example the EnvironmentFeatureRepository looks for features in the system environment variables. The environment
should be prefixed with 'FEATURE_'. For example 'FEATURE_MY_FEATURE=1', 'FEATURE_MY_OTHER_FEATURE=0'.

4) Use that flags in your code:
```kotlin
    if (Features.MY_FEATURE.isEnabled) {
        // Do something...
    }
```
