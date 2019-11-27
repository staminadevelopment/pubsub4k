# pubsub4k
[![Java 8+][java-badge]](https://java.oracle.com/)
[![License][license-badge]](/LICENSE)
[![Maven Central][maven-badge]](https://search.maven.org/artifact/pw.stamina/pubsub4k)
[![Build Status][actions-badge]](https://github.com/staminadevelopment/pubsub4k/actions)
[![Code quality][codebeat-badge]](https://codebeat.co/projects/github-com-staminadevelopment-pubsub4k-master)
[![Code coverage][codecov-badge]](https://codecov.io/gh/staminadevelopment/pubsub4k)

[java-badge]: https://img.shields.io/badge/Java-8%2B-informational.svg
[license-badge]: https://img.shields.io/github/license/staminadevelopment/pubsub4k.svg
[maven-badge]: https://img.shields.io/maven-central/v/pw.stamina/pubsub4k.svg
[actions-badge]: https://github.com/staminadevelopment/pubsub4k/workflows/Kotlin%20CI/badge.svg
[codebeat-badge]: https://codebeat.co/badges/3ff547d9-7d03-4c6d-aec8-a7d26cd0ac85
[codecov-badge]: https://codecov.io/gh/staminadevelopment/pubsub4k/branch/master/graph/badge.svg

A simple yet powerful pubsub system for Java and Kotlin.

## Features

- Fluent API for Java and Kotlin
- Message filtering and mapping
- Thread safe by default

## Prerequisites
 * Use JDK 1.8 or later

## Add as dependency

Latest version: [![Maven Central][maven-badge]](https://search.maven.org/artifact/pw.stamina/pubsub4k)

### Maven
```xml
<dependency>
  <groupId>pw.stamina</groupId>
  <artifactId>pubsub4k</artifactId>
  <version>{version}</version>
</dependency>
```

### Gradle
Groovy DSL:
```groovy
implementation 'pw.stamina:pubsub4k:{version}'
```

Kotlin DSL:
```kotlin
implementation("pw.stamina:pubsub4k:{version}")
```

## Quick start

1. Get an `EventBus` instance:
```kotlin
val bus = EventBus.createDefaultBus()
```

2. Create a `Publisher` for `String`s:
```kotlin
val stringPublisher = bus.getPublisher<String>()
```

3. Create a class and implement `MessageSubscriber`:
```kotlin
class StringSubscriber : MessageSubscriber
```

4. Create subscription(s) for your subscriber:
```kotlin
class StringSubscriber : MessageSubscriber {

    val stringSubscription = newSubscription<String> {
        println("Received: $it")
    }
}
```

5. Instantiate your subscriber and register your subscription(s):
```kotlin
val subscriber = StringSubscriber()

bus.subscriptions.register(subscriber.stringSubscription)
```

6. Publish a message to your publisher:
```kotlin
stringPublisher.publish("Hello world")
// "Received: Hello world" is printed
```
