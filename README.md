# Microsphere Spring Cloud

> Microsphere Projects for Spring Cloud

[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/microsphere-projects/microsphere-spring-cloud)
[![zread](https://img.shields.io/badge/Ask_Zread-_.svg?style=flat&color=00b0aa&labelColor=000000&logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB3aWR0aD0iMTYiIGhlaWdodD0iMTYiIHZpZXdCb3g9IjAgMCAxNiAxNiIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTQuOTYxNTYgMS42MDAxSDIuMjQxNTZDMS44ODgxIDEuNjAwMSAxLjYwMTU2IDEuODg2NjQgMS42MDE1NiAyLjI0MDFWNC45NjAxQzEuNjAxNTYgNS4zMTM1NiAxLjg4ODEgNS42MDAxIDIuMjQxNTYgNS42MDAxSDQuOTYxNTZDNS4zMTUwMiA1LjYwMDEgNS42MDE1NiA1LjMxMzU2IDUuNjAxNTYgNC45NjAxVjIuMjQwMUM1LjYwMTU2IDEuODg2NjQgNS4zMTUwMiAxLjYwMDEgNC45NjE1NiAxLjYwMDFaIiBmaWxsPSIjZmZmIi8%2BCjxwYXRoIGQ9Ik00Ljk2MTU2IDEwLjM5OTlIMi4yNDE1NkMxLjg4ODEgMTAuMzk5OSAxLjYwMTU2IDEwLjY4NjQgMS42MDE1NiAxMS4wMzk5VjEzLjc1OTlDMS42MDE1NiAxNC4xMTM0IDEuODg4MSAxNC4zOTk5IDIuMjQxNTYgMTQuMzk5OUg0Ljk2MTU2QzUuMzE1MDIgMTQuMzk5OSA1LjYwMTU2IDE0LjExMzQgNS42MDE1NiAxMy43NTk5VjExLjAzOTlDNS42MDE1NiAxMC42ODY0IDUuMzE1MDIgMTAuMzk5OSA0Ljk2MTU2IDEwLjM5OTlaIiBmaWxsPSIjZmZmIi8%2BCjxwYXRoIGQ9Ik0xMy43NTg0IDEuNjAwMUgxMS4wMzg0QzEwLjY4NSAxLjYwMDEgMTAuMzk4NCAxLjg4NjY0IDEwLjM5ODQgMi4yNDAxVjQuOTYwMUMxMC4zOTg0IDUuMzEzNTYgMTAuNjg1IDUuNjAwMSAxMS4wMzg0IDUuNjAwMUgxMy43NTg0QzE0LjExMTkgNS42MDAxIDE0LjM5ODQgNS4zMTM1NiAxNC4zOTg0IDQuOTYwMVYyLjI0MDFDMTQuMzk4NCAxLjg4NjY0IDE0LjExMTkgMS42MDAxIDEzLjc1ODQgMS42MDAxWiIgZmlsbD0iI2ZmZiIvPgo8cGF0aCBkPSJNNCAxMkwxMiA0TDQgMTJaIiBmaWxsPSIjZmZmIi8%2BCjxwYXRoIGQ9Ik00IDEyTDEyIDQiIHN0cm9rZT0iI2ZmZiIgc3Ryb2tlLXdpZHRoPSIxLjUiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIvPgo8L3N2Zz4K&logoColor=ffffff)](https://zread.ai/microsphere-projects/microsphere-spring-cloud)
[![Maven Build](https://github.com/microsphere-projects/microsphere-spring-cloud/actions/workflows/maven-build.yml/badge.svg)](https://github.com/microsphere-projects/microsphere-spring-cloud/actions/workflows/maven-build.yml)
[![Codecov](https://codecov.io/gh/microsphere-projects/microsphere-spring-cloud/branch/dev-1.x/graph/badge.svg)](https://app.codecov.io/gh/microsphere-projects/microsphere-spring-cloud)
![Maven](https://img.shields.io/maven-central/v/io.github.microsphere-projects/microsphere-spring-cloud.svg)
![License](https://img.shields.io/github/license/microsphere-projects/microsphere-spring-cloud.svg)


Microsphere Spring Cloud is an extension library for Spring Cloud that enhances and optimizes its capabilities,
particularly focused on providing dynamic runtime configuration changes without application restarts. It's designed to
solve common pain points when working with distributed systems in Spring Cloud.

## Purpose and Scope

Microsphere Spring Cloud is a comprehensive extension framework that enhances Spring Cloud applications with advanced
service registration capabilities, dynamic OpenFeign client configuration, and fault tolerance features. This project
provides production-ready enhancements to the core Spring Cloud ecosystem, focusing on operational reliability and
dynamic configuration management.

The framework supports multiple Spring Cloud versions (2022.x, 2023.x, 2024.x and 2025.x) and integrates seamlessly with
various
service discovery systems including Nacos, Eureka, Consul, and Zookeeper. For detailed information about specific
subsystems, see Project Structure, Service Registration System, OpenFeign Auto-Refresh System, and Fault Tolerance.

## Modules

| **Module**                                | **Purpose**                                                                         |
|-------------------------------------------|-------------------------------------------------------------------------------------|
| **microsphere-spring-cloud-parent**       | Defines the parent POM with dependency management and Spring Cloud version profiles |
| **microsphere-spring-cloud-dependencies** | Centralizes dependency management for all project modules                           |
| **microsphere-spring-cloud-commons**      | Common utilities for service discovery, registry, and fault tolerance               |
| **microsphere-spring-cloud-openfeign**    | Extensions for Spring Cloud OpenFeign with auto-refresh capabilities                |

## Getting Started

The easiest way to get started is by adding the Microsphere Spring Cloud BOM (Bill of Materials) to your project's
pom.xml:

```xml
<dependencyManagement>
    <dependencies>
        ...
        <!-- Microsphere Spring Cloud Dependencies -->
        <dependency>
            <groupId>io.github.microsphere-projects</groupId>
            <artifactId>microsphere-spring-cloud-dependencies</artifactId>
            <version>${microsphere-spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        ...
    </dependencies>
</dependencyManagement>
```

`${microsphere-spring-boot.version}` has two branches:

| **Branches** | **Purpose**                                      | **Latest Version** |
|--------------|--------------------------------------------------|--------------------|
| **0.2.x**    | Compatible with Spring Cloud 2022.0.x - 2025.0.x | 0.2.4              |
| **0.1.x**    | Compatible with Spring Cloud Hoxton - 2021.0.x   | 0.1.4              |

Then add the specific modules you need:

```xml
<dependencies>
    <!-- Microsphere Spring Cloud Commons -->
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-spring-cloud-commons</artifactId>
    </dependency>

    <!-- Microsphere Spring Cloud OpenFeign -->
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-spring-cloud-openfeign</artifactId>
    </dependency>
</dependencies>
```

## Building from Source

You don't need to build from source unless you want to try out the latest code or contribute to the project.

To build the project, follow these steps:

1. Clone the repository:

```bash
git clone https://github.com/microsphere-projects/microsphere-spring-cloud.git
```

2. Build the source:

- Linux/MacOS:

```bash
./mvnw package
```

- Windows:

```powershell
mvnw.cmd package
```

## Contributing

We welcome your contributions! Please read [Code of Conduct](./CODE_OF_CONDUCT.md) before submitting a pull request.

## Reporting Issues

* Before you log a bug, please search
  the [issues](https://github.com/microsphere-projects/microsphere-spring-cloud/issues)
  to see if someone has already reported the problem.
* If the issue doesn't already
  exist, [create a new issue](https://github.com/microsphere-projects/microsphere-spring-cloud/issues/new).
* Please provide as much information as possible with the issue report.

## Documentation

### User Guide

[DeepWiki Host](https://deepwiki.com/microsphere-projects/microsphere-spring-cloud)

[ZRead Host](https://zread.ai/microsphere-projects/microsphere-spring-cloud)

### Wiki

[Github Host](https://github.com/microsphere-projects/microsphere-spring-cloud/wiki)

### JavaDoc

- [microsphere-spring-cloud-commons](https://javadoc.io/doc/io.github.microsphere-projects/microsphere-spring-cloud-commons)
- [microsphere-spring-cloud-openfeign](https://javadoc.io/doc/io.github.microsphere-projects/microsphere-spring-cloud-openfeign)

## License

The Microsphere Spring is released under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).
