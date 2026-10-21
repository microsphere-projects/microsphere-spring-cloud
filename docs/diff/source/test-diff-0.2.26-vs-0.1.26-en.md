# src/test Implementation Diff: 0.2.26 vs 0.1.26

> Generated 2026-10-20
> Baseline: tag `0.2.26` (new, release line, Java 17 + Spring Boot 3.x/4.x + Spring Cloud 2022+)
> Target: tag `0.1.26` (old, release-1.x line, Java 8 + Spring Boot 2.3–2.6 + Spring Cloud Hoxton–2021)
> Scope: per Maven module, `src/test` (test Java sources and test resources); for `src/main` see the [sibling report](./main-diff-0.2.26-vs-0.1.26-en.md)
> Chinese version: [test-diff-0.2.26-vs-0.1.26.md](./test-diff-0.2.26-vs-0.1.26.md)

## 1. Exclusion Rules

Same as the src/main comparison: Jakarta EE migration, Java 8/17 syntax & API, comment-only and formatting-equivalent changes (logging-facade switch, static imports, redundant null-guard removal) are flagged in the tables but not counted as implementation differences.

## 2. Module Matching Overview

| Module (0.2.26) | Module (0.1.26) | Matched | src/test files | Files with diffs |
|---|---|---|---|---|
| microsphere-spring-cloud-parent | same | ✅ | 0 | — |
| microsphere-spring-cloud-dependencies | same | ✅ | 0 | — |
| microsphere-spring-cloud-commons | same | ✅ | 54 | 5 (4 Java + 1 resource) |
| microsphere-spring-cloud-openfeign | same | ✅ | 42 | 25 (all Java) |

Test file inventories are identical on both tags — no added or removed files.

## 3. microsphere-spring-cloud-commons (src/test)

| # | File | New (0.2.26) | Old (0.1.26) | Reason |
|---|---|---|---|---|
| T1 | `client/service/registry/DefaultRegistrationTest.java` | `testIsSecure` asserts `assertTrue`; fixture sets `setSecure(true)` and an https URI | asserts `assertFalse` | **Follows main-code M1**: verifies the new secure-propagation behavior |
| T2 | `client/service/util/ServiceInstanceUtilsTest.java` | adds `DefaultServiceInstance.getUri(...)` consistency assertion; fixture adds `setSecure(false)` | absent | **Follows main-code M1**: strengthens URI/secure equivalence coverage |
| T3 | `fault/tolerance/tomcat/autoconfigure/TomcatFaultToleranceAutoConfigurationIntegrationTest.java` | sets `server.max-http-request-header-size` | sets `server.max-http-header-size` | **API compatibility (config-key migration)**: pairs with main-code M2 |
| T4 | `client/service/registry/endpoint/BaseServiceRegistrationEndpointTest.java` | imports `org.springframework.boot.test.web.server.LocalServerPort` | imports `org.springframework.boot.web.server.LocalServerPort` | **API compatibility**: class relocated in Spring Boot 3.x |
| T5 | `resources/META-INF/config/default/test.yaml` | profile activation via `spring.config.activate.on-profile` (5 occurrences) | deprecated `spring.profiles` | **API compatibility**: Spring Boot 2.4+ config-file syntax change |

## 4. microsphere-spring-cloud-openfeign (src/test)

| # | File(s) | New (0.2.26) | Old (0.1.26) | Reason |
|---|---|---|---|---|
| T6 | `autorefresh/BaseTest.java` | all `@TestPropertySource` keys use `spring.cloud.openfeign.client.config.*` | `feign.client.config.*` | **API compatibility (config-key migration)**: pairs with main-code M4/M5 |
| T7 | 5 `*ChangedTest.java` (encoder / errordecoder / querymapencoder / requestInterceptor / retryer) | `afterTestComponentConfigKey()` returns `spring.cloud.openfeign.client.config.my-client.*` | `feign.client.config.my-client.*` | **API compatibility (config-key migration)**: same |
| T8 | `FeignComponentAssert.java` + 6 `*ComponentAssert.java` + `ObservableFeignInvocationHandler.java` (8 files) | assert hook signature `expect(Object configuration, ResponseHandler responseHandler, ...)`; reads `methodHandlerConfiguration`/`responseHandler` fields reflectively with a `ClassUtils.isPresent("feign.MethodHandlerConfiguration")` version probe, falling back to the MethodHandler itself on old Feign | reads component fields directly off `InvocationHandlerFactory.MethodHandler` | **API compatibility + feature need**: newer Feign moved decoder/encoder dependencies from MethodHandler into `ResponseHandler` and `MethodHandlerConfiguration`; the probe keeps cross-version compatibility |
| T9 | `AutoRefreshCapabilityTest.java`, `DecoratedFeignComponentTest.java` | use `FeignClientFactory` / `NamedContextFactory<FeignClientSpecification>` | use `FeignContext` | **API compatibility**: pairs with main-code M7 |
| T10 | `AutoRefreshCapabilityCustomizerTest.java` | direct `new FeignClientSpecification(...)` | reflective constructor lookup | **API compatibility (simplification)**: pairs with main-code M6 |
| T11 | `DecoratedQueryMapEncoderTest.java` | implements `configureDelegateClass` (`setQueryMapEncoder`), re-enables the two inherited componentType tests, adds `testGetQueryMapEncoder` (null config / not-found MethodHandle cases) | no-op override, componentType tests skipped | **Feature need (coverage)**: regression tests for main-code M8 |
| T12 | `DecoratedRetryerTest.java` | asserts via `assertThrows(RetryableException.class, ...)` | calls without asserting the throw | **Test correctness fix**: `Retryer.Default` must throw once retries are exhausted |
| T13 | `FeignConstantsTest.java` | asserts 4 more constants (`FEIGN_CAPABILITY_CLASS_NAME`, etc.) | asserts 2 constants | **Coverage**: constants exist on both tags; assertions catch up |
| T14 | `CompositedRequestInterceptorTest.java` | removes null-init branches for `getDefaultRequestHeaders`/`getDefaultQueryParameters`; collections via `newLinkedList()` | manual null checks with `HashMap`/`ArrayList` | **API compatibility + equivalent refactor**: newer `FeignClientConfiguration` getters return non-null default collections, making guards obsolete |
| T15 | `AErrorDecoder.java`, `A/BQueryMapEncoder.java`, `ARetry.java` | logging via `io.microsphere.logging` facade; `MapUtils.newHashMap()` | direct `org.slf4j`, `new HashMap<>()` | **Dependency unification / equivalent refactor**: no behavior change |

## 5. Root-Cause Categories

| Category | Items | Note |
|---|---|---|
| Config-key migration (`feign.client.*` → `spring.cloud.openfeign.client.*`; `server.max-http-header-size` rename) | T3, T6, T7 | Follow-up of main-code API adaptations |
| Deprecated / relocated / restructured Spring & Feign APIs | T4, T8, T9 | T8's `isPresent` probe is a deliberate cross-version compatibility design |
| Following main-code feature changes | T1, T2, T11 | Regression tests for secure propagation and configurable QueryMapEncoder |
| Test correctness fix | T12 | Stricter exception assertion — standalone quality improvement |
| Coverage | T13 | Constant assertions completed |
| Equivalent refactors (excluded / flagged only) | T5, T10, T14, T15 | Syntax/API write-up updates with no behavioral impact |

## 6. Conclusions

1. 30 test files differ across the tags, but **the vast majority are mechanical follow-ups of src/main diffs** (config keys, type replacements, reflective field paths).
2. Items with standalone value: the `DecoratedRetryerTest` exception-assertion fix (T12), `FeignConstantsTest` coverage (T13), and the Feign version-probe design in `ObservableFeignInvocationHandler` (T8).
3. Test file inventories match exactly — nothing added or removed; expectations were synced to the main-code secure-propagation fix (T1, T2).
