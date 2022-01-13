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
    implementation("com.nbottarini:asimov-flagz:0.2.3")
}
```

#### Gradle (Groovy)

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.nbottarini:asimov-flagz:0.2.3'
}
```

#### Maven

```xml
<dependency>
    <groupId>com.nbottarini</groupId>
    <artifactId>asimov-flagz</artifactId>
    <version>0.2.3</version>
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
    repository(EnvironmentFeatureRepository())
}
```
You'll have to configure a feature repositories. A repository provides a way to store and search for
feature flag states (enabled or disabled).

In this example the EnvironmentFeatureRepository looks for features in the system environment variables. The environment
variable must be prefixed with 'FEATURE_'. For example 'FEATURE_MY_FEATURE=1', 'FEATURE_MY_OTHER_FEATURE=0'.

4) Use that flags in your code:
```kotlin
    if (Features.MY_FEATURE.isEnabled) {
        // Do something...
    }
```

## EnabledByDefault
By default, if a feature flag is not set is disabled. You can change this behaviour per-feature with the `EnabledByDefault`
annotation.

```kotlin
enum class Features: Feature {
    MY_FEATURE,
    
    @EnabledByDefault
    MY_OTHER_FEATURE
}
```

## Feature repositories

Feature repositories allows you to store and retrieve feature flags state.

### InMemoryFeatureRepository

Store features state in memory.

```kotlin
initFlagz {
    featureEnum<Features>()
    repository(InMemoryFeatureRepository())
}
```

### EnvironmentFeatureRepository

This is a read-only repository. This repository read feature's state from environment variables. Each variable must be
prefixed with "FEATURE_". For example "FEATURE_MY_AWESOME_FEATURE".

```kotlin
initFlagz {
    featureEnum<Features>()
    repository(EnvironmentFeatureRepository())
}
```

Values '1', 'true' and 'TRUE' are interpreted as enabled. '0', 'false' and 'FALSE' indicates that the feature is disabled.

By default, this repository uses the [asimov-environment](https://github.com/nbottarini/asimov-environment-kt) library to 
access the environment variables, so you can create a .env file to store your feature flags states for development.

```dotenv
FEATURE_ALLOW_REGISTRATION=1
FEATURE_NEW_BLOG=0
```

You can use a different environment variables provider by implemented the interface `EnvironmentProvider` and passing you 
implementation on repository construction.
```kotlin
class MyEnvProvider: EnvironmentProvider {
    override fun get(name: String) = MyEnvironmentLibrary.get[name]
}

initFlagz {
    featureEnum<Features>()
    repository(EnvironmentFeatureRepository(MyEnvProvider))
}
```

### JdbcFeatureRepository

Store feature states in a database.

To use this repository you have to pass a jdbc datasource for your database.

```kotlin
val datasource = MysqlDataSource()
datasource.setURL(/* jdbc url */)
datasource.setUser(/* username */)
datasource.setPassword(/* password */)

initFlagz {
    featureEnum<Features>()
    repository(JdbcFeatureRepository(datasource))
}
```

By default, it creates a feature_flags table in the database if it doesn't exist.
You can customize the table name:
```kotlin
JdbcFeatureRepository(datasource, "flags")
```

You can also disable the schema generation and create the schema by yourself:
```kotlin
JdbcFeatureRepository(datasource, generateSchema = false)
```

```sql
CREATE TABLE feature_flags (
    name                VARCHAR(100) PRIMARY KEY,
    is_enabled          INTEGER NOT NULL,
    strategy_id         VARCHAR(200),
    strategy_params     VARCHAR(2000)
)
```

This repository uses ansi sql so is compatible with most database providers.

### CachedFeatureRepository

Wraps another repository by introducing a cache.
```kotlin
initFlagz {
    featureEnum<Features>()
    repository(CachedFeatureRepository(JdbcFeatureRepository(datasource)))
}

/** or **/

initFlagz {
    featureEnum<Features>()
    repository(JdbcFeatureRepository(datasource).cached())
}
```

You can specify the TTL in milliseconds for the cache:
```kotlin
initFlagz {
    featureEnum<Features>()
    repository(CachedFeatureRepository(JdbcFeatureRepository(datasource), 60_000))
}

/** or **/

initFlagz {
    featureEnum<Features>()
    repository(JdbcFeatureRepository(datasource).cached(60_000))
}
```

### CompositeFeatureRepository

Allows to use more than one repository. The features are retrieved from the first matching repository. 

```kotlin
initFlagz {
    featureEnum<Features>()
    repository(
        CompositeFeatureRepository(
            JdbcFeatureRepository(datasource),
            EnvironmentFeatureRepository()
        )
    )
}

/** or **/

initFlagz {
    featureEnum<Features>()
    repositories(
        JdbcFeatureRepository(datasource),
        EnvironmentFeatureRepository()
    )
}
```
When a feature flag is set you can customize if it is persisted in the first repository, the last or all of them.

```kotlin
initFlagz {
    featureEnum<Features>()
    repository(
        CompositeFeatureRepository(
            JdbcFeatureRepository(datasource),
            EnvironmentFeatureRepository()
        ),
        SetStrategies.ALL
    )
}
```

## Conditional strategies

You can provide different strategies to enable/disable features dynamically based on dates, gradual rollout, per users,
per user roles, per user attributes, etc. You can also create your own strategies.

To define a strategy for a feature you have to annotate it with the `Conditional` annotation. For example:

```kotlin
enum class Features: Feature {
    MY_FEATURE,

    @Conditional(ReleaseDateStrategy.ID, [Param(PARAM_DATE, "2020-09-06T10:00:00Z")])
    MY_OTHER_FEATURE
}
```

### ReleaseDateStrategy
This strategy allows you to enable a feature in a certain date. The provided date must be in ISO 8601 format.

### RollingReleaseStrategy
This strategy allows you gradually enable a feature to a percentage of your user base.

```kotlin
enum class Features: Feature {
    MY_FEATURE,

    @Conditional(RollingReleaseStrategy.ID, [Param(PARAM_PERCENTAGE, "30")])
    MY_OTHER_FEATURE
}
```

The percentage value must be an integer between 0 and 100.
A hashcode function is applied to each user's name and is normalized to a value between 0 and 100. All users with hashcode
lower than the percentage have the feature enabled and the others disabled.

### UsersStrategy
This strategy allows you to enable a feature only to certain users.

```kotlin
enum class Features: Feature {
    MY_FEATURE,

    @Conditional(UsersStrategy.ID, [Param(PARAM_USERS, "alice, bob")])
    MY_OTHER_FEATURE
}
```

Users must be set by using a userProvider. Read next section for details.

### UserAttributeStrategy
This strategy allows you to enable a feature only to users that have some attribute.

```kotlin
enum class Features: Feature {
    MY_FEATURE,

    @Conditional(UserAttributeStrategy.ID, [Param(PARAM_NAME, "country"), Param(PARAM_VALUE, "AR")])
    MY_OTHER_FEATURE
}
```

## User providers
Allows the customization of feature flags per user or user's attributes.

You can pass a userProvider implementation to the initialization function. The default userProvider is `ThreadLocalUserProvider`.

```kotlin
initFlagz {
    featureEnum<Features>()
    repository(EnvironmentFeatureRepository())
    userProvider(MyUserProvider())
}
```

### ThreadLocalUserProvider
This implementation allows you to set the current user per thread.

You can access the current user provider by calling `FlagzContext.manager.userProvider`.
```kotlin
val userProvider = FlagzContext.manager.userProvider as ThreadLocalUserProvider

userProvider.bind(SimpleFeatureUser("alice", mapOf("roles" to "admin")))

// Do something and use feature flags

userProvider.release()
```
Users have to implement the FeatureUser interface, or you can use SimpleFeatureUser implementation.

If you are using a mediator / command-bus like [CQBus](](https://github.com/nbottarini/asimov-cqbus-kt)) you can add a 
middleware to set the current user.
```kotlin
class SetFeatureFlagsUserMiddleware: Middleware {
    override fun <T: Request<R>, R> execute(request: T, next: (T) -> R, context: ExecutionContext): R {
        val userProvider = FlagzContext.manager.userProvider as? ThreadLocalUserProvider ?: return next(request)
        val user = SimpleFeatureUser(context.identity.name, mapOf("roles", context.identity.roles.joinToString(", ")))
        userProvider.bind(user)
        val response = next(request)
        userProvider.release()
        return response
    }
}
```
