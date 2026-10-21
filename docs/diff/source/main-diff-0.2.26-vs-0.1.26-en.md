# src/main Implementation Diff: 0.2.26 vs 0.1.26

> Generated 2026-10-20
> Baseline: tag `0.2.26` (new, release line, Java 17 + Spring Boot 3.x/4.x + Spring Cloud 2022+)
> Target: tag `0.1.26` (old, release-1.x line, Java 8 + Spring Boot 2.3–2.6 + Spring Cloud Hoxton–2021)
> Scope: per Maven module, `src/main` (Java sources and resources); for `src/test` see the [sibling report](./test-diff-0.2.26-vs-0.1.26-en.md)
> Chinese version: [main-diff-0.2.26-vs-0.1.26.md](./main-diff-0.2.26-vs-0.1.26.md)

## 1. Exclusion Rules

Not counted as implementation differences:

| Excluded category | Note |
|---|---|
| Java EE → Jakarta EE API | namespace migration such as `javax.servlet.*` → `jakarta.servlet.*` |
| Java 8 / Java 17 syntax & JDK API | purely language-level differences |
| Comment-only changes | Javadoc / `@see` edits with no behavioral impact |
| Formatting-equivalent refactors | trailing newline, static-import rewrite, etc. |

## 2. Module Matching Overview

| Module (0.2.26) | Module (0.1.26) | Matched | src/main/java | src/main/resources |
|---|---|---|---|---|
| microsphere-spring-cloud-parent | same | ✅ | no Java sources | no diff |
| microsphere-spring-cloud-dependencies | same | ✅ | no Java sources | no diff |
| microsphere-spring-cloud-commons | same | ✅ | 61 files, 3 differ (2 substantive) | `spring.factories` slimmed (auto-configs moved to `AutoConfiguration.imports`, same list) |
| microsphere-spring-cloud-openfeign | same | ✅ | 21 files, 14 differ (6 substantive) | `spring.factories` deleted (moved to `AutoConfiguration.imports`, same list) |

Java file inventories are identical on both tags; the only file-level change is the deletion of openfeign's `META-INF/spring.factories` at 0.2.26.

## 3. src/main/java Differences

### 3.1 microsphere-spring-cloud-commons

| # | File | New (0.2.26) | Old (0.1.26) | Reason |
|---|---|---|---|---|
| M1 | `client/service/util/ServiceInstanceUtils.java` | `setProperties` calls `target.setSecure(source.isSecure())` | `target.setUri(source.getUri())`, secure flag not propagated | **Bug fix / feature need**: propagate the secure flag so HTTPS instances are not flattened to http URIs |
| M2 | `fault/tolerance/tomcat/event/TomcatDynamicConfigurationListener.java` | calls `getMaxHttpRequestHeaderSize()` | calls `getMaxHttpHeaderSize()` | **API compatibility**: Spring Boot 3.x renamed the `ServerProperties` accessor; dynamic refresh must follow |
| M3 | `client/service/registry/autoconfigure/WebMvcServiceRegistryAutoConfiguration.java` | `jakarta.servlet.Filter`; one `@see` removed | `javax.servlet.Filter` | **Excluded** (Jakarta EE migration + comment-only) |

### 3.2 microsphere-spring-cloud-openfeign

| # | File | New (0.2.26) | Old (0.1.26) | Reason |
|---|---|---|---|---|
| M4 | `autorefresh/AutoRefreshCapability.java` | reads contextId from `spring.cloud.openfeign.client.name` | reads `feign.client.name` | **API compatibility (config-key migration)**: Spring Cloud 2022 renamed all OpenFeign properties under `spring.cloud.openfeign.` |
| M5 | `autorefresh/FeignClientConfigurationChangedListener.java` | listens on prefix `spring.cloud.openfeign.client.config.` | listens on prefix `feign.client.config.` | **API compatibility (config-key migration)**: determines which clients refresh on config change |
| M6 | `autorefresh/AutoRefreshCapabilityCustomizer.java` | `instanceof FeignClientSpecification` + direct `setConfiguration(...)` | dynamic `resolveClass` + reflective setter via `BeanUtils.findWriteMethod` | **API compatibility (simplification)**: 1.x used reflection because the specification class name/package was unstable across versions; now compile-time available. Logically equivalent |
| M7 | `components/DecoratedFeignComponent.java` | depends on `NamedContextFactory<FeignClientSpecification>`; `instantiate` parameter relaxed to `Class<? extends T>`; trace log wrapped with `isTraceEnabled()` | depends on concrete `FeignContext` | **API compatibility**: `FeignContext` deprecated/removed in new Spring Cloud; log guard is a defensive performance improvement |
| M8 | `components/DecoratedQueryMapEncoder.java` | static `MethodHandle` invokes `FeignClientConfiguration.getQueryMapEncoder()`; `componentType()` resolves the configured class with fallback to `PageableSpringQueryMapEncoder` | `componentType()` hard-returns `PageableSpringQueryMapEncoder.class` | **Feature need + API compatibility**: the property only exists in the newer `FeignClientConfiguration`; the MethodHandle not-found fallback degrades gracefully on old configs |
| M9 | `components/DecoratedRetryer.java` | `componentType()` falls back to `Retryer.class` (interface) | falls back to `Retryer.Default.class` | **Behavioral difference**: unified with M8 — resolve by interface from the context, letting `NamedContextFactory` pick the bean |
| M10 | `DecoratedContract/Decoder/Encoder/ErrorDecoder.java` (4 files) | constructor parameter `FeignContext` → `NamedContextFactory<FeignClientSpecification>` | `FeignContext` | **API compatibility**: mechanical follow-up of M7, no standalone logic change |
| — | `CompositedRequestInterceptor.java`, `NoOpRequestInterceptor.java`, `ConditionalOnOpenFeignAvailable/Enabled.java` (4 files) | Javadoc/`@see` only | — | **Excluded** (comment-only) |

## 4. src/main/resources Differences

| # | Module | File | New (0.2.26) | Old (0.1.26) | Reason |
|---|---|---|---|---|---|
| R1 | commons | `META-INF/spring.factories` | keeps only the `NacosServiceRegistry = NacosRegistration` mapping; the 10 auto-configuration classes live in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` (present and identical on both tags) | registers the 10 auto-configurations via `EnableAutoConfiguration=` in spring.factories (in addition to the imports file) | **API compatibility**: Spring Boot 3 dropped spring.factories auto-config registration; 0.1.26 registered through both channels, the duplicate was cleaned up |
| R2 | openfeign | `META-INF/spring.factories` | file deleted; `FeignAutoConfiguration` and `FeignClientAutoRefreshAutoConfiguration` are registered via `AutoConfiguration.imports` (identical on both tags) | registered the same 2 auto-configurations via spring.factories as well | **API compatibility**: same as R1 |

## 5. Root-Cause Categories

| Category | Items | Note |
|---|---|---|
| Spring Cloud config-key migration (`feign.client.*` → `spring.cloud.openfeign.client.*`) | M4, M5 | **Downstream users must migrate their config** |
| Spring Boot 3 auto-config registration migration (spring.factories → AutoConfiguration.imports) | R1, R2 | Mandatory on Boot 3; registered class list unchanged |
| Deprecated / renamed Spring APIs (`FeignContext`, `getMaxHttpHeaderSize`) | M2, M7, M10 | API-compatibility adaptations |
| Feature needs (secure-propagation fix, configurable QueryMapEncoder) | M1, M8 | Real behavior changes |
| Behavioral difference (fallback `Retryer.Default` → `Retryer`) | M9 | Semantics unified to interface-based resolution |
| Simplification (reflection → typed calls) | M6 | Logically equivalent refactor |
| Excluded (Jakarta EE, comment-only, formatting) | M3 and 4 comment-only files | No behavioral impact |

## 6. Conclusions

1. src/main carries **8 substantive diffs** (commons 2 + openfeign 6), concentrated on OpenFeign 4.x upgrade adaptation: type generalization, config-key migration, and the new configurable QueryMapEncoder.
2. Resources changes are registration-mechanism migration only (spring.factories → AutoConfiguration.imports); auto-configuration content is unchanged.
3. The only runtime-visible behavior changes for users are the secure propagation in `ServiceInstanceUtils.setProperties` and the `DecoratedRetryer` default fallback; everything else is framework-mandated adaptation.
