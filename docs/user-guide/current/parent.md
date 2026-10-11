[中文](./zh/parent.md) · [1.x version line](../1.x/parent.md) · [User Guide index](../README.md)

# `microsphere-spring-cloud-parent` — User Guide (0.2.x)

The parent POM of the microsphere-spring-cloud **0.2.x** version line (branch `release`).
It centralizes dependency management for Spring Boot / Spring Cloud / Spring Cloud Alibaba /
Microsphere Spring Boot, aggregates the testing BOMs, and exposes one Maven profile per
supported Spring Cloud release train so the same source can be built and tested against
Boot 3.0 up to Boot 4.0.

## Version Information

| Item | Value |
|---|---|
| Git branch | `release` |
| Version line | 0.2.x |
| Maven coordinates | `io.github.microsphere-projects:microsphere-spring-cloud-parent` |
| Artifact type | `pom` (no code, no runtime artifact) |
| Latest published release | **`0.2.26`** (branch head is `0.2.27-SNAPSHOT`, not published) |
| Repository | Maven Central |
| Java | **17+** (`<java.version>17</java.version>` in the aggregator POM) |
| Servlet namespace | `jakarta.*` |
| Spring Boot range | 3.0.x – 4.0.x (per profile) |
| Spring Cloud range | 2022.0.x – 2025.1.x (per profile) |
| Upstream | `microsphere-spring-boot` **0.2.34** |
| Build parent | `io.github.microsphere-projects:microsphere-build` **0.3.16** |
| Test stack | JUnit Jupiter **6.1.3**, Testcontainers **2.0.5** |

## When to Use

Use this parent POM when:

- your application or internal library builds on the 0.2.x line and you want **one place** to pin
  Spring Boot / Spring Cloud / Spring Cloud Alibaba / Microsphere versions;
- you want to verify the same code against Spring Cloud 2022 → 2025.1 by flipping a Maven profile
  instead of editing POMs;
- you build sibling Microsphere Spring Cloud modules (for example a company-internal module that
  depends on `microsphere-spring-cloud-commons`).

If your project must keep its existing parent (typically `spring-boot-starter-parent`), import the
[dependencies BOM](./dependencies.md) instead. See *Parent or BOM?* below.

## Inheriting the Parent

```xml
<project>
    <parent>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-spring-cloud-parent</artifactId>
        <version>0.2.26</version>
        <relativePath/> <!-- resolve from the repository, not from disk -->
    </parent>

    <groupId>com.example</groupId>
    <artifactId>order-service</artifactId>
    <version>1.0.0</version>

    <dependencies>
        <!-- versions come from the imported BOMs: no <version> element needed -->
        <dependency>
            <groupId>io.github.microsphere-projects</groupId>
            <artifactId>microsphere-spring-cloud-commons</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
    </dependencies>
</project>
```

Because the parent chain reaches `microsphere-build` → standard Maven plugin management
(compiler at Java 17, source/javadoc, GPG signing, flattening of `${revision}`), consumers get a
reproducible build configuration as well as version alignment.

## Properties You May Override

The parent declares these properties; override them in your `<properties>` **only if you know why**:

| Property | Value in 0.2.26 | Set by |
|---|---|---|
| `microsphere-spring-boot.version` | `0.2.34` | parent POM property |
| `junit-jupiter.version` | `6.1.3` | parent POM property |
| `testcontainers.version` | `2.0.5` | parent POM property |
| `spring-boot.version` | per profile | **Maven profile** |
| `spring-cloud.version` | per profile | **Maven profile** |
| `spring-cloud-alibaba.version` | per profile | **Maven profile** |
| `java.version` | `17` | aggregator POM (`microsphere-spring-cloud`) |

## Aggregated BOMs

The parent's `dependencyManagement` imports, in declaration order:

| Imported BOM | Version property | Resolved value (default profile) |
|---|---|---|
| `org.junit:junit-bom` | `junit-jupiter.version` | 6.1.3 |
| `org.testcontainers:testcontainers-bom` | `testcontainers.version` | 2.0.5 |
| `org.springframework.boot:spring-boot-dependencies` | `spring-boot.version` | 3.5.15 |
| `org.springframework.cloud:spring-cloud-dependencies` | `spring-cloud.version` | 2025.0.3 |
| `com.alibaba.cloud:spring-cloud-alibaba-dependencies` | `spring-cloud-alibaba.version` | 2025.0.0.0 |
| `io.github.microsphere-projects:microsphere-spring-boot-dependencies` | `microsphere-spring-boot.version` | 0.2.34 |

Order matters in Maven: the **first** managed entry wins. JUnit and Testcontainers are declared
first so the test stack is not silently downgraded by Spring Boot's own BOM.

The child BOM `microsphere-spring-cloud-dependencies` inherits all of the above and adds the two
runnable artifacts of this repository (`microsphere-spring-cloud-commons`,
`microsphere-spring-cloud-openfeign`) at `${revision}`.

## Version Compatibility (Profiles)

Five Spring Cloud profiles are defined. **`spring-cloud-2025` is active by default.**

| Profile | Spring Boot | Spring Cloud | Spring Cloud Alibaba |
|---|---|---|---|
| `spring-cloud-2022` | 3.0.13 | 2022.0.5 | 2022.0.0.2 |
| `spring-cloud-2023` | 3.3.13 | 2023.0.6 | 2023.0.3.4 |
| `spring-cloud-2024` | 3.4.13 | 2024.0.3 | 2023.0.3.4 |
| `spring-cloud-2025` **(default)** | 3.5.15 | 2025.0.3 | 2025.0.0.0 |
| `spring-cloud-2025.1` | 4.0.8 | 2025.1.3 | 2025.1.0.0 |

### Switching Profiles at Build Time

```bash
# default build — Spring Boot 3.5.15 / Spring Cloud 2025.0.3
mvn clean install

# older release trains
mvn clean install -Pspring-cloud-2022
mvn clean install -Pspring-cloud-2023
mvn clean install -Pspring-cloud-2024

# Spring Boot 4.0 / Spring Cloud 2025.1
mvn clean install -Pspring-cloud-2025.1

# show which profile is active and the resulting versions
mvn help:active-profiles
mvn dependency:tree -Pspring-cloud-2023 | grep spring-cloud
```

Maven semantics worth remembering:

- `activeByDefault` is switched off as soon as **any** profile is activated on the command line or
  by another activation condition, so `-Pspring-cloud-2022` fully replaces the default.
- Activating two Spring Cloud profiles at once (`-Pspring-cloud-2022,spring-cloud-2024`) is legal
  but undefined in effect: the last profile declaring `spring-boot.version` wins. Activate exactly one.

The profile only changes **managed versions**. The published `0.2.26` jars are built once and run
across the whole 3.0.x–4.0.x / 2022.0.x–2025.1.x range, because every auto-configuration in
[commons](./commons.md) and [openfeign](./openfeign.md) is class-guarded
(`@ConditionalOnClass(name = …)`) and degrades gracefully when optional providers (Eureka, Nacos,
Consul, Zookeeper, Kubernetes, Feign, Tomcat) are absent.

## The `testcontainers` Profile

Registry/discovery integration tests are gated behind a system property and skipped in ordinary
builds:

```java
@EnabledIfSystemProperty(named = "testcontainers.enabled", matches = "true")
@EnabledIfDockerAvailable
class MultipleServiceRegistryIntegrationTest { … }
```

To run them (Docker required):

```bash
mvn clean test -Ptestcontainers
# the profile sets -Dtestcontainers.enabled=true on surefire
```

## Parent or BOM?

| Situation | Choose |
|---|---|
| New service/library, no opinion about the Maven parent | `microsphere-spring-cloud-parent` (this module) |
| Project already inherits `spring-boot-starter-parent` or a corporate parent | `microsphere-spring-cloud-dependencies` (import scope) |
| You need Spring Boot/Spring Cloud versions pinned **and** Microsphere artifacts | Either — the parent imports the same BOMs, and the BOM inherits them |
| Multi-module reactor with its own dependency management | BOM, imported in the root POM only |

The BOM is a thin child of this parent; see [dependencies.md](./dependencies.md) for the exact
import snippet and the list of managed artifacts.

## Caveats

- **Never declare the parent under `<dependencies>`.** It is `pom` packaging and contributes build
  configuration only.
- **Do not mix 0.2.x and 0.1.x artifacts in one application.** They target different generations
  (`jakarta`/Boot 3+ vs `javax`/Boot 2) and different property namespaces
  (`spring.cloud.openfeign.client.config.` vs `feign.client.config.`). Use the
  [1.x line](../1.x/parent.md) for Java 8 / Boot 2 projects.
- The `0.2.x` line requires **Java 17** bytecode. Boot 4.0 (profile `spring-cloud-2025.1`) requires
  Java 17 as well, but check Spring's own baseline notes before adopting it in production.
- Version property interpolation uses `${revision}` plus the flatten plugin; consumers always see a
  concrete version (`0.2.26`) in the published POM.

## See Also

- [dependencies.md](./dependencies.md) — the BOM artifact
- [commons.md](./commons.md) — features and property reference of the core module
- [openfeign.md](./openfeign.md) — Feign component auto-refresh
- [User Guide index](../README.md) · [Docs home](../../README.md)
