[中文](./zh/parent.md) · [0.2.x version line](../current/parent.md) · [User Guide index](../README.md)

# `microsphere-spring-cloud-parent` — User Guide (0.1.x)

The parent POM of the microsphere-spring-cloud 0.1.x version line. It centralizes the
dependency management of Spring Boot / Spring Cloud / Spring Cloud Alibaba / Microsphere
Spring Boot BOMs and exposes Maven profiles to switch between supported Spring Cloud
release trains.

## Version Information

| Item | Value |
|---|---|
| Git branch | `release-1.x` |
| Version line | 0.1.x |
| Latest published release | **`0.1.26`** (branch head is `0.1.27-SNAPSHOT`) |
| Maven coordinates | `io.github.microsphere-projects:microsphere-spring-cloud-parent` |
| Repository | Maven Central |
| Java baseline | **Java 8** (the 1.x root POM does not set `java.version`; the build parent `microsphere-build` 0.3.14 supplies the Java 8 baseline) |
| Servlet namespace | `javax.*` |
| Spring Boot range | 2.3.x – 2.6.x (per profile) |
| Spring Cloud range | Hoxton.SR12 – 2021.0.x (per profile) |
| Upstream | `microsphere-spring-boot` **0.1.34** |
| Test stack | JUnit Jupiter 5.14.4, Testcontainers 1.21.4 |
| Artifact type | `pom` (no code) |

## When to Use

Use this parent POM when:

- your application or library builds on the 0.1.x line and you want one place to pin
  Spring Boot / Spring Cloud / Spring Cloud Alibaba versions;
- you want to test the same code against Hoxton, 2020.x and 2021.x by flipping a
  Maven profile instead of editing POMs;
- you build sibling Microsphere Spring Cloud modules (e.g. a company-internal module
  that depends on `microsphere-spring-cloud-commons`).

If you only need version pinning of the Microsphere Spring Cloud artifacts themselves,
import the [dependencies BOM](./dependencies.md) instead — that works even when your
application already inherits `spring-boot-starter-parent`.

## Inheriting the Parent

```xml
<project>
    <parent>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-spring-cloud-parent</artifactId>
        <version>0.1.26</version>
    </parent>

    <dependencies>
        <!-- versions come from the imported BOMs; no <version> needed -->
        <dependency>
            <groupId>io.github.microsphere-projects</groupId>
            <artifactId>microsphere-spring-cloud-commons</artifactId>
        </dependency>
    </dependencies>
</project>
```

The parent's `dependencyManagement` imports, in order:

| Imported BOM | Version property |
|---|---|
| `org.junit:junit-bom` | `junit-jupiter.version` = 5.14.4 |
| `org.testcontainers:testcontainers-bom` | `testcontainers.version` = 1.21.4 |
| `org.springframework.boot:spring-boot-dependencies` | `spring-boot.version` (set by profile) |
| `org.springframework.cloud:spring-cloud-dependencies` | `spring-cloud.version` (set by profile) |
| `com.alibaba.cloud:spring-cloud-alibaba-dependencies` | `spring-cloud-alibaba.version` (set by profile) |
| `io.github.microsphere-projects:microsphere-spring-boot-dependencies` | `microsphere-spring-boot.version` = 0.1.34 |

It also manages the two runnable artifacts of this repository
(`microsphere-spring-cloud-commons`, `microsphere-spring-cloud-openfeign`) at `${revision}`.

## Profile Switching

Three Spring Cloud profiles are defined. **`spring-cloud-2021` is active by default.**

| Profile | Spring Boot | Spring Cloud | Spring Cloud Alibaba |
|---|---|---|---|
| `spring-cloud-hoxton` | 2.3.12.RELEASE | Hoxton.SR12 | 2.2.10 |
| `spring-cloud-2020` | 2.4.13 | 2020.0.5 | 2021.1 |
| `spring-cloud-2021` **(default)** | 2.6.15 | 2021.0.9 | 2021.0.6.1 |

Switch profiles at build time:

```bash
mvn clean install -Pspring-cloud-hoxton
mvn clean install -Pspring-cloud-2020
mvn clean install                    # spring-cloud-2021 (default)
```

Notes:

- On Hoxton the stack is Ribbon-era. In the sibling `microsphere-spring-cloud-openfeign`
  module, the `spring-cloud-hoxton` profile additionally pulls
  `spring-cloud-starter-netflix-ribbon` in **test scope** so the tests run against the
  Ribbon-era Feign stack. This does not affect consumers.
- Profile choice only changes the managed dependency versions. The published 0.1.x
  artifacts are built once and are compatible across the whole 2.3.x–2.6.x /
  Hoxton.SR12–2021.0.x range; auto-configurations are class-guarded so features
  degrade gracefully when optional classes (Eureka, Nacos, Consul, Zookeeper, Feign…)
  are absent.
- Regardless of the Spring Cloud profile, the 0.1.x OpenFeign module always listens to
  the `feign.client.config.` property prefix (the Hoxton-era property namespace, still
  valid under 2020.x/2021.x in this line). See the [OpenFeign guide](./openfeign.md).

## Java 8 Note

The 0.1.x line compiles to **Java 8 bytecode** (`maven.compiler.source/target = 8`
inherited from `microsphere-build` 0.3.14) and uses the **`javax.*` servlet namespace**.
It is therefore consumable from Java 8, 11 and 17 runtimes, but:

- on Spring Boot 2.x the servlet API remains `javax.servlet`, so your application's
  filters/servlets must also be `javax`-based;
- no 0.1.x artifact requires anything newer than Java 8 — if you see records, sealed
  types or `jakarta.*` in a dependency, you have mixed in a 0.2.x artifact by mistake.

## Caveats

- **Do not mix 0.1.x and 0.2.x artifacts in one application.** They target different
  Spring Boot generations (`javax`/Boot 2 vs `jakarta`/Boot 3+) and different property
  namespaces (e.g. `feign.client.config.` vs `spring.cloud.openfeign.client.config.`).
  Pin exactly one version line, ideally through this parent or the BOM of the same line.
- The 0.1.x auto-configuration registration uses `META-INF/spring.factories` (Spring
  Boot 2 mechanism). The `AutoConfiguration.imports` file is shipped in the jars as
  well, but Boot 2.3–2.6 ignores it. This is internal to the library; you do not
  register anything manually.
- `microsphere-spring-cloud-parent` is a build/dependency convenience, not a runtime
  dependency. Never list it under `<dependencies>`.

## Version Compatibility

| Runs with | Supported |
|---|---|
| Java 8 / 11 / 17 JVMs | yes |
| Spring Boot 2.3.x, 2.4.x, 2.5.x, 2.6.x | yes (profiles above) |
| Spring Cloud Hoxton.SR12, 2020.0.x, 2021.0.x | yes |
| Spring Cloud Alibaba 2.2.10, 2021.1, 2021.0.6.x | yes |
| Spring Boot 3.x / 4.x, `jakarta.*` | **no — use the [0.2.x line](../current/parent.md)** |

## Differences vs 0.2.x

| Aspect | 0.1.x (`release-1.x`) | 0.2.x (`release`) |
|---|---|---|
| Java baseline | 8 (via build parent 0.3.14) | 17 (explicit `java.version`) |
| Servlet namespace | `javax.*` | `jakarta.*` |
| Default profile | `spring-cloud-2021` (Boot 2.6.15 / Cloud 2021.0.9 / Alibaba 2021.0.6.1) | `spring-cloud-2025` (Boot 3.5.15 / Cloud 2025.0.3) |
| Other profiles | `spring-cloud-hoxton`, `spring-cloud-2020` | 2022 / 2023 / 2024 / 2025.1 |
| Upstream `microsphere-spring-boot` | 0.1.34 | 0.2.34 |
| Auto-config registration (runtime effect) | `spring.factories` is authoritative (Boot 2) | `AutoConfiguration.imports` is authoritative (Boot 3) |
| Latest release | 0.1.26 | 0.2.26 |
