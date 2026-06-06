# Microsphere Spring Cloud

> Microsphere Projects for Spring Cloud

[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/microsphere-projects/microsphere-spring-cloud)
[![Maven Build](https://github.com/microsphere-projects/microsphere-spring-cloud/actions/workflows/maven-build.yml/badge.svg)](https://github.com/microsphere-projects/microsphere-spring-cloud/actions/workflows/maven-build.yml)
[![Codecov](https://codecov.io/gh/microsphere-projects/microsphere-spring-cloud/branch/dev-1.x/graph/badge.svg)](https://app.codecov.io/gh/microsphere-projects/microsphere-spring-cloud)
![Maven](https://img.shields.io/maven-central/v/io.github.microsphere-projects/microsphere-spring-cloud.svg)
![License](https://img.shields.io/github/license/microsphere-projects/microsphere-spring-cloud.svg)

Microsphere Spring Cloud is a production-ready extension library for Spring Cloud that enhances and optimizes its
capabilities, particularly focused on providing dynamic runtime configuration changes without application restarts.
It solves common pain points when building and operating distributed systems with Spring Cloud.

## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Modules](#modules)
- [Getting Started](#getting-started)
    - [Add the BOM](#add-the-bom)
    - [Add Module Dependencies](#add-module-dependencies)
    - [Version Compatibility](#version-compatibility)
- [Usage](#usage)
    - [OpenFeign Auto-Refresh](#openfeign-auto-refresh)
    - [Multiple Service Registry](#multiple-service-registry)
    - [Service Registration Events](#service-registration-events)
- [Building from Source](#building-from-source)
- [Documentation](#documentation)
- [Contributing](#contributing)
- [Getting Help](#getting-help)
- [License](#license)

## Features

- **OpenFeign Auto-Refresh** — Dynamically refresh OpenFeign client configuration (encoder, decoder, retryer, request
  interceptors, etc.) at runtime in response to `EnvironmentChangeEvent`, eliminating the need for application restarts.
- **Multiple Service Registry** — Register a single service instance simultaneously with multiple service registries
  (Nacos, Eureka, Consul, Zookeeper) through a unified API.
- **Service Registration Events** — Rich lifecycle events (`RegistrationPreRegisteredEvent`,
  `RegistrationRegisteredEvent`, `RegistrationPreDeregisteredEvent`, `RegistrationDeregisteredEvent`) published via
  AOP, enabling fine-grained observability of registration state changes.
- **Union Discovery Client** — Aggregate service instances from multiple discovery backends behind a single
  `DiscoveryClient` interface.
- **Fault Tolerance Utilities** — Weighted round-robin load balancing and Tomcat dynamic configuration support for
  improved resilience.
- **Actuator Endpoints** — Built-in `/service-registration` and `/service-deregistration` Actuator endpoints for
  operational control at runtime.
- **Multi-version Support** — Seamlessly compatible with Spring Cloud 2022.x through 2025.x (main branch) and
  Hoxton–2021.0.x (1.x branch).

## Prerequisites

| Requirement  | Version                                                          |
|--------------|------------------------------------------------------------------|
| Java         | 17+                                                              |
| Spring Boot  | 3.0+ (main branch) / 2.x (1.x branch)                            |
| Spring Cloud | 2022.0.x – 2025.x (main branch) / Hoxton – 2021.0.x (1.x branch) |
| Maven        | 3.6+                                                             |

## Modules

| **Module**                                | **Purpose**                                                                         |
|-------------------------------------------|-------------------------------------------------------------------------------------|
| **microsphere-spring-cloud-parent**       | Defines the parent POM with dependency management and Spring Cloud version profiles |
| **microsphere-spring-cloud-dependencies** | Centralizes dependency management for all project modules                           |
| **microsphere-spring-cloud-commons**      | Common utilities for service discovery, registry, and fault tolerance               |
| **microsphere-spring-cloud-openfeign**    | Extensions for Spring Cloud OpenFeign with auto-refresh capabilities                |

## Getting Started

### Add the BOM

Add the Microsphere Spring Cloud BOM to your project's `pom.xml` to manage dependency versions centrally:

```xml

<dependencyManagement>
    <dependencies>
        <!-- Microsphere Spring Cloud Dependencies -->
        <dependency>
            <groupId>io.github.microsphere-projects</groupId>
            <artifactId>microsphere-spring-cloud-dependencies</artifactId>
            <version>${microsphere-spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Add Module Dependencies

Then declare only the modules you need (versions are managed by the BOM):

```xml

<dependencies>
    <!-- Core utilities: service registry, discovery, fault tolerance -->
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-spring-cloud-commons</artifactId>
    </dependency>

    <!-- OpenFeign extensions with auto-refresh support -->
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-spring-cloud-openfeign</artifactId>
    </dependency>
</dependencies>
```

### Version Compatibility

| **Branch** | **Spring Cloud compatibility**         | **Latest version** |
|------------|----------------------------------------|--------------------|
| `main`     | 2022.0.x, 2023.0.x, 2024.0.x, 2025.0.x | `0.2.17`           |
| `1.x`      | Hoxton, 2020.0.x, 2021.0.x             | `0.1.17`           |

By default, the `main` branch builds against Spring Cloud 2025.0.x. To build or run tests against an older generation,
activate the corresponding Maven profile:

```bash
# Spring Cloud 2022.x
./mvnw verify -P spring-cloud-2022

# Spring Cloud 2023.x
./mvnw verify -P spring-cloud-2023

# Spring Cloud 2024.x
./mvnw verify -P spring-cloud-2024
```

## Usage

### OpenFeign Auto-Refresh

Enable dynamic refresh of Feign client components (encoder, decoder, retryer, request interceptors, etc.) without
restarting the application. When a configuration change is published as an `EnvironmentChangeEvent` (for example by
Spring Cloud Config or Nacos Config), the affected Feign clients are automatically updated.

1. Annotate your Spring Boot application (or any `@Configuration` class) with `@EnableFeignAutoRefresh`:

```java

@SpringBootApplication
@EnableFeignClients
@EnableFeignAutoRefresh
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

2. Define your Feign client as usual:

```java

@FeignClient(name = "my-service")
public interface MyServiceClient {
    @GetMapping("/api/resource")
    String getResource();
}
```

3. Update Feign client configuration properties at runtime (e.g. via your config server). For example, changing
   `spring.cloud.openfeign.client.config.my-service.retryer` will refresh the retryer for `my-service` without any
   restart.

### Multiple Service Registry

Register a service instance with more than one service registry simultaneously. Useful when migrating between
registries or when different consumers rely on different discovery systems.

```java

@Configuration
public class RegistryConfig {

    @Bean
    public MultipleAutoServiceRegistration multipleAutoServiceRegistration(
            MultipleServiceRegistry registry,
            MultipleRegistration registration,
            AutoServiceRegistrationProperties properties) {
        return new MultipleAutoServiceRegistration(registry, properties, registration);
    }
}
```

The `MultipleServiceRegistry` delegates `register`, `deregister`, and `setStatus` calls to all configured
underlying registries (Nacos, Eureka, Consul, Zookeeper) based on their `Registration` type.

### Service Registration Events

React to service registration lifecycle changes by listening to the dedicated Spring events published by the
`EventPublishingRegistrationAspect`:

```java

@Component
public class RegistrationListener {

    @EventListener
    public void onRegistered(RegistrationRegisteredEvent event) {
        System.out.println("Service registered: " + event.getRegistration().getServiceId());
    }

    @EventListener
    public void onDeregistered(RegistrationDeregisteredEvent event) {
        System.out.println("Service deregistered: " + event.getRegistration().getServiceId());
    }
}
```

Available events: `RegistrationPreRegisteredEvent`, `RegistrationRegisteredEvent`,
`RegistrationPreDeregisteredEvent`, `RegistrationDeregisteredEvent`.

## Building from Source

You don't need to build from source to use the library — released artifacts are available on Maven Central. Build
from source only if you want to try the latest unreleased code or contribute to the project.

1. Clone the repository:

```bash
git clone https://github.com/microsphere-projects/microsphere-spring-cloud.git
cd microsphere-spring-cloud
```

2. Build and run tests:

- Linux/macOS:

```bash
./mvnw verify
```

- Windows:

```powershell
mvnw.cmd verify
```

3. To run integration tests that require Docker (via Testcontainers), add the `testcontainers` profile:

```bash
./mvnw verify -P testcontainers
```

## Documentation

| Resource              | Link                                                                                                                             |
|-----------------------|----------------------------------------------------------------------------------------------------------------------------------|
| User Guide (DeepWiki) | [deepwiki.com/microsphere-projects/microsphere-spring-cloud](https://deepwiki.com/microsphere-projects/microsphere-spring-cloud) |
| User Guide (ZRead)    | [zread.ai/microsphere-projects/microsphere-spring-cloud](https://zread.ai/microsphere-projects/microsphere-spring-cloud)         |
| Wiki                  | [GitHub Wiki](https://github.com/microsphere-projects/microsphere-spring-cloud/wiki)                                             |
| JavaDoc — commons     | [javadoc.io](https://javadoc.io/doc/io.github.microsphere-projects/microsphere-spring-cloud-commons)                             |
| JavaDoc — openfeign   | [javadoc.io](https://javadoc.io/doc/io.github.microsphere-projects/microsphere-spring-cloud-openfeign)                           |
| Release Notes         | [release-notes.md](./release-notes.md)                                                                                           |

## Contributing

Contributions are welcome! Here's how to get involved:

1. Search [existing issues](https://github.com/microsphere-projects/microsphere-spring-cloud/issues) to avoid
   duplicates, then [open a new issue](https://github.com/microsphere-projects/microsphere-spring-cloud/issues/new)
   if your topic isn't covered.
2. Fork the repository and create a feature branch from `main`.
3. Make your changes, add tests, and ensure the build passes (`./mvnw verify`).
4. Submit a pull request against the `main` branch.

Please read [CODE_OF_CONDUCT.md](./CODE_OF_CONDUCT.md) before contributing. All participants are expected to
follow it.

### Maintainers

| Name                                      | Role                       | Contact              |
|-------------------------------------------|----------------------------|----------------------|
| [Mercy Ma](https://github.com/mercyblitz) | Lead architect & developer | mercyblitz@gmail.com |

## Getting Help

- **Bugs & feature requests** — [GitHub Issues](https://github.com/microsphere-projects/microsphere-spring-cloud/issues)
- **Questions & discussions
  ** — [GitHub Discussions](https://github.com/microsphere-projects/microsphere-spring-cloud/discussions)
- **Interactive AI docs** — [DeepWiki](https://deepwiki.com/microsphere-projects/microsphere-spring-cloud) or
  [ZRead](https://zread.ai/microsphere-projects/microsphere-spring-cloud)
- **Wiki** — [GitHub Wiki](https://github.com/microsphere-projects/microsphere-spring-cloud/wiki)

## License

Microsphere Spring Cloud is released under the [Apache License 2.0](./LICENSE).
