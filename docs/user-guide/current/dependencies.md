[中文](./zh/dependencies.md) · [1.x version line](../1.x/dependencies.md) · [User Guide index](../README.md)

# `microsphere-spring-cloud-dependencies` — User Guide (0.2.x)

The BOM (bill of materials) of the microsphere-spring-cloud **0.2.x** version line (branch `release`).
It is a `pom` artifact whose only job is to publish the version of every runnable module in this
repository, so consumers can declare Microsphere Spring Cloud dependencies without version tags.

## Version Information

| Item | Value |
|---|---|
| Git branch | `release` |
| Version line | 0.2.x |
| Maven coordinates | `io.github.microsphere-projects:microsphere-spring-cloud-dependencies` |
| Artifact type | `pom` (imported with `<scope>import</scope>`) |
| Latest published release | **`0.2.26`** (branch head is `0.2.27-SNAPSHOT`, not published) |
| Repository | Maven Central |
| Java of the managed artifacts | **17+** |
| Spring Boot range of the managed artifacts | 3.0.x – 4.0.x |
| Spring Cloud range of the managed artifacts | 2022.0.x – 2025.1.x |
| Parent | `microsphere-spring-cloud-parent` 0.2.x (see [parent.md](./parent.md)) |
| Upstream BOM it also inherits | `microsphere-spring-boot-dependencies` **0.2.34** |

## What It Does

`microsphere-spring-cloud-dependencies` inherits
[microsphere-spring-cloud-parent](./parent.md) and adds a `dependencyManagement` section for the
modules built in this repository. Importing it gives you:

1. **Version alignment for Microsphere Spring Cloud artifacts** — the two entries it declares itself.
2. **The inherited dependency management of the parent** — because Maven computes the *effective*
   POM of an imported BOM, the six aggregated BOMs of the parent (`junit-bom`,
   `testcontainers-bom`, `spring-boot-dependencies`, `spring-cloud-dependencies`,
   `spring-cloud-alibaba-dependencies`, `microsphere-spring-boot-dependencies`) come along as well.

## Managed Artifacts

Declared directly by this BOM:

| Artifact | GroupId | Version |
|---|---|---|
| `microsphere-spring-cloud-commons` | `io.github.microsphere-projects` | `${revision}` → `0.2.26` |
| `microsphere-spring-cloud-openfeign` | `io.github.microsphere-projects` | `${revision}` → `0.2.26` |

Inherited from the parent chain (managed, not declared): everything in Spring Boot's, Spring
Cloud's, Spring Cloud Alibaba's, Testcontainers', JUnit's and
`microsphere-spring-boot-dependencies` BOMs — including `microsphere-spring-boot-core`,
`microsphere-spring-boot-webmvc`, `microsphere-spring-boot-webflux`,
`microsphere-spring-boot-actuator`, which `microsphere-spring-cloud-commons` requires at compile
scope.

> There is **no** `microsphere-spring-cloud-starter-*` artifact in this version line. Depend on the
> modules directly, and add the third-party starters (`spring-cloud-starter-openfeign`,
> `spring-boot-starter-actuator`, a registry client) yourself — the Microsphere modules declare
> those as `optional`.

## Import Usage

```xml
<project>
    <!-- your own parent stays untouched, e.g. spring-boot-starter-parent -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.6</version>
        <relativePath/>
    </parent>

    <properties>
        <microsphere-spring-cloud.version>0.2.26</microsphere-spring-cloud.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- Microsphere Spring Cloud BOM -->
            <dependency>
                <groupId>io.github.microsphere-projects</groupId>
                <artifactId>microsphere-spring-cloud-dependencies</artifactId>
                <version>${microsphere-spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <!-- no <version> needed -->
        <dependency>
            <groupId>io.github.microsphere-projects</groupId>
            <artifactId>microsphere-spring-cloud-commons</artifactId>
        </dependency>
        <dependency>
            <groupId>io.github.microsphere-projects</groupId>
            <artifactId>microsphere-spring-cloud-openfeign</artifactId>
        </dependency>
    </dependencies>
</project>
```

Verify the resolved versions:

```bash
mvn -q dependency:tree -Dincludes=io.github.microsphere-projects
mvn -q help:effective-pom | grep -A2 "microsphere-spring-cloud"
```

### Import Order Rules

Maven applies "first declaration wins" inside `dependencyManagement`, and BOM imports are expanded
at the position where they are declared:

- If you manage Spring Boot or Spring Cloud versions yourself, **declare your BOMs before**
  `microsphere-spring-cloud-dependencies` so your platform versions are not overwritten by the
  versions baked into the Microsphere parent (`spring-boot.version` / `spring-cloud.version`).
- Pin a single Microsphere artifact to a different version by declaring it in your own
  `dependencyManagement` before the import — a direct entry always beats an imported BOM.

## When to Choose the BOM vs the Parent

| Need | Use |
|---|---|
| Your project already inherits `spring-boot-starter-parent` / a corporate parent | **BOM** (import scope) — a POM can have only one parent |
| You want Microsphere's build plugin configuration too (compiler 17, flattened `${revision}`, source/javadoc) | **Parent** ([parent.md](./parent.md)) |
| You want to switch Spring Boot/Spring Cloud trains with `-P…` profiles while building a library | **Parent** — profiles are not carried into an imported BOM of your own build |
| Multi-module reactor: pin versions once in the root, use everywhere | **BOM** in the root POM |
| You only add `microsphere-spring-cloud-commons` to one module | **BOM**, or even a plain versioned dependency |

Rule of thumb: **applications → BOM; Microsphere-based libraries → parent.**

## Version Compatibility

The BOM itself is version-neutral metadata, but the artifacts it manages require:

| Requirement | Supported by 0.2.x artifacts |
|---|---|
| Java | 17+ |
| Spring Boot | 3.0.x – 4.0.x (tested matrix: 3.0.13 / 3.3.13 / 3.4.13 / 3.5.15 / 4.0.8) |
| Spring Cloud | 2022.0.x – 2025.1.x (2022.0.5 / 2023.0.6 / 2024.0.3 / 2025.0.3 / 2025.1.3) |
| Spring Cloud Alibaba | 2022.0.0.2 / 2023.0.3.4 / 2025.0.0.0 / 2025.1.0.0 |
| Upstream `microsphere-spring-boot` | 0.2.34 (managed transitively via the parent) |
| Servlet namespace | `jakarta.*` |

Because the BOM's inherited managed versions are computed from the parent's **default** profile
(`spring-cloud-2025` → Boot 3.5.15 / Cloud 2025.0.3 / Alibaba 2025.0.0.0), importing the BOM into a
Boot 3.0.x application is fine — but then import `spring-boot-dependencies` (your version) **first**
so the platform stays where you want it. To build this repository against another train, use the
parent's profiles — see [parent.md](./parent.md).

## Caveats

- **Never put the BOM in `<dependencies>`.** It must be `<scope>import</scope>` inside
  `<dependencyManagement>`; as a compile dependency it resolves to a POM with no classes.
- Importing the BOM does **not** activate the parent's Maven profiles in your build. Profiles are
  only meaningful when you build this repository itself.
- The managed artifacts are `optional`-dependency heavy: adding the BOM plus
  `microsphere-spring-cloud-commons` does **not** bring Spring Cloud, a registry client, Feign or
  Actuator onto your classpath. Declare those starters yourself.
- Use one version line at a time: for Spring Boot 2.x / Java 8 use the
  [1.x BOM](../1.x/dependencies.md) (`0.1.26`).

## See Also

- [parent.md](./parent.md) — the parent POM this BOM inherits, profile matrix
- [commons.md](./commons.md) — what `microsphere-spring-cloud-commons` provides
- [openfeign.md](./openfeign.md) — what `microsphere-spring-cloud-openfeign` provides
- [User Guide index](../README.md)
