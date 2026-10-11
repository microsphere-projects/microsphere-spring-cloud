[中文](./zh/openfeign.md) · [0.2.x version line](../current/openfeign.md) · [User Guide index](../README.md)

# `microsphere-spring-cloud-openfeign` — User Guide (0.1.x)

Hot-reloads the per-client components of Spring Cloud OpenFeign — `Decoder`, `Encoder`,
`ErrorDecoder`, `Retryer`, `Contract`, `RequestInterceptor`s, default request headers and
default query parameters — when their `feign.client.config.*` properties change, with
**no restart and no Feign client rebuild**. Built for the Hoxton-era property namespace
that the whole 0.1.x line uses.

## Version Information

| Item | Value |
|---|---|
| Git branch | `release-1.x` |
| Version line | 0.1.x |
| Latest published release | **`0.1.26`** (branch head is `0.1.27-SNAPSHOT`) |
| Maven coordinates | `io.github.microsphere-projects:microsphere-spring-cloud-openfeign` |
| Repository | Maven Central |
| Java baseline | Java 8 |
| Spring Boot range | 2.3.x – 2.6.x |
| Spring Cloud OpenFeign range | 2.2.x – 3.1.x |
| Refreshed property prefix | **`feign.client.config.`** (not `spring.cloud.openfeign.client.config.`) |
| Auto-config registration | `META-INF/spring.factories` (`AutoConfiguration.imports` is also shipped but ignored by Boot 2.3–2.6) |
| Switch | `microsphere.spring.cloud.openfeign.enabled` (default `true`) |

## Purpose and When to Use

Spring Cloud OpenFeign reads `feign.client.config.*` only when a client's child context
is created; afterwards the `Decoder`/`Encoder`/`Retryer`/… instances are frozen. This
module wraps each component in a refreshable delegate (via a Feign `Capability`) so that
when you change a configuration property and trigger a refresh, the *next call* uses the
newly configured class. Typical uses: swapping a `Decoder` during incident triage,
rotating `default-request-headers` / `default-query-parameters`, changing `retryer`
policy at runtime.

## Setup

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-spring-cloud-openfeign</artifactId>
    <version>0.1.26</version> <!-- or manage via the 0.1.x BOM / parent -->
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

Spring Cloud OpenFeign itself is **optional** for this module — you bring your own
(versions 2.2.x – 3.1.x, i.e. Hoxton.SR12 through 2021.0.x). On Hoxton this means the
Ribbon-era stack; note that even under the `spring-cloud-2020`/`spring-cloud-2021`
profiles, the 0.1.x line keeps the **`feign.client.config.`** property namespace for
this module's refresh machinery.

Then activate auto-refresh explicitly:

```java
@SpringBootApplication
@EnableFeignClients
@EnableFeignAutoRefresh   // io.microsphere.spring.cloud.openfeign.autorefresh.EnableFeignAutoRefresh
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

`@EnableFeignAutoRefresh` has **no attributes**; it registers a `Marker` bean that the
`FeignClientAutoRefreshAutoConfiguration` conditions on. The other auto-configuration,
`FeignAutoConfiguration`, is unconditional (given OpenFeign on the classpath and the
switch not disabled) and seeds a no-op `RequestInterceptor` so every Feign builder
always has the interceptor pipeline engaged.

## Configuration Keys (1.x Namespace)

All refreshable settings live under `feign.client.config.`:

| Key (suffix after `feign.client.config.<client>.`) | Feign component | Refreshable |
|---|---|---|
| `decoder` | `feign.codec.Decoder` | yes |
| `encoder` | `feign.codec.Encoder` | yes |
| `error-decoder` | `feign.codec.ErrorDecoder` | yes |
| `retryer` | `feign.Retryer` | yes |
| `contract` | `feign.Contract` | yes |
| `request-interceptors[n]` | `feign.RequestInterceptor` (list) | yes |
| `default-request-headers.<name>` | header applied by the composite interceptor | yes |
| `default-query-parameters.<name>` | query param applied by the composite interceptor | yes |
| `query-map-encoder` | `feign.QueryMapEncoder` | **no on 0.1.x** — see limitation below |
| `feign.client.name` (not under `config`) | resolves the client's contextId inside each child context | n/a (read at build time) |

Dashed canonical form; camelCase (`errorDecoder`) and array indexes
(`requestInterceptors[0]`) are normalized as well.

```yaml
feign:
  client:
    name: default            # contextId fallback used inside Feign child contexts
    config:
      default:
        decoder: com.acme.feign.ADecoder
        encoder: com.acme.feign.AEncoder
        error-decoder: com.acme.feign.AErrorDecoder
        retryer: com.acme.feign.ARetry
        request-interceptors[0]: com.acme.feign.ARequestInterceptor
        default-request-headers:
          app: my-app
        default-query-parameters:
          sign: my-sign
      my-client:             # per-client override, wins for contextId "my-client"
        decoder: com.acme.feign.BDecoder
```

```java
@FeignClient(contextId = "my-client", name = "my-client")
public interface MyClient {
    @GetMapping("echo")
    String echo(@RequestParam("msg") String msg, @RequestParam("v") String v);
}
```

## Refresh Procedure

The listener (`FeignClientConfigurationChangedListener`) reacts to Spring Cloud
Context's `EnvironmentChangeEvent` — the event published by `POST /actuator/refresh`.
Only keys starting with `feign.client.config.` are considered; the first segment after
the prefix is treated as the client name.

1. Change `feign.client.config.*` values in your externalized configuration
   (Config Server, Kubernetes ConfigMap, …).
2. Trigger a refresh:

   ```bash
   curl -X POST http://localhost:8080/actuator/refresh \
        -H 'Content-Type: application/json' \
        -d '["feign.client.config.my-client.decoder"]'
   ```

3. On the **next** method call of `my-client`, the delegate `Decoder` is a freshly
   resolved `BDecoder`. No restart, no client rebuild.

Semantics of the changed keys:

- `feign.client.config.<client>.*` changed → refreshes **only that client's** matching
  component(s) and its composited request interceptor.
- `feign.client.config.default.*` changed → refreshes the matching component type for
  **every** client that has one registered (and all interceptor composites if any
  `RequestInterceptor`-typed key changed).

How it works internally: each client's child context gets the `AutoRefreshCapability`
configuration class injected (through the `SpecificationCustomizer` mechanism from
[commons](./commons.md)); the capability wraps the components in `Decorated*` holders
registered under the client's contextId, and a `CompositedRequestInterceptor` per client
absorbs interceptors, headers and query parameters. `refresh()` simply drops the
delegate, which is lazily re-resolved on the next call.

## Custom Component Requirements

The value of a config key is a **class name** (FQCN). A custom class qualifies if it is:

- a `public` class implementing the matching Feign interface
  (`Decoder`, `Encoder`, `ErrorDecoder`, `Retryer`, `Contract`, `RequestInterceptor`);
- instantiable with a **default (no-arg) constructor**.

It does **not** need to be a Spring bean — if a bean of that type exists it is used;
otherwise the class is instantiated directly. This mirrors the module's own tests, e.g.
`ADecoder`/`BDecoder` are plain classes wired purely through
`@TestPropertySource(properties = "feign.client.config.default.decoder=…ADecoder")`.

## `query-map-encoder` Limitation (0.1.x)

On this version line, `DecoratedQueryMapEncoder` **hard-codes
`PageableSpringQueryMapEncoder.class`** as its component type. Setting
`feign.client.config.<client>.query-map-encoder=…` configures the initial client, but a
later change + refresh will **not** swap the `QueryMapEncoder` delegate to the configured
class. The 0.2.x line resolves the configured class (falling back to
`PageableSpringQueryMapEncoder`) — if you need refreshable `QueryMapEncoder`, you must
upgrade version lines or restart the application.

## Disabling

```properties
# remove all auto-configurations of this module
microsphere.spring.cloud.openfeign.enabled=false
```

Alternatively, simply omit `@EnableFeignAutoRefresh`: without its `Marker` bean,
`FeignClientAutoRefreshAutoConfiguration` backs off and no components are wrapped
(`FeignAutoConfiguration`'s no-op interceptor seed still applies unless the switch is
`false`). Auto-configurations also back off when `feign.Feign` / `feign.Capability` /
`FeignClientFactoryBean` are absent.

## Version Compatibility

| Runs with | Supported |
|---|---|
| Java 8 / 11 / 17 | yes |
| Spring Boot 2.3.x – 2.6.x | yes |
| Spring Cloud Hoxton.SR12 (Ribbon era), 2020.0.x, 2021.0.x — Spring Cloud OpenFeign 2.2.x – 3.1.x | yes |
| `spring.cloud.openfeign.client.config.*` property namespace | **no — that is the 0.2.x behavior** |

## Differences vs 0.2.x

| Aspect | 0.1.x (`release-1.x`) | 0.2.x (`release`) |
|---|---|---|
| Watched config prefix | `feign.client.config.` | `spring.cloud.openfeign.client.config.` |
| contextId property | `feign.client.name` | `spring.cloud.openfeign.client.name` |
| Context factory type | `org.springframework.cloud.openfeign.FeignContext` | `NamedContextFactory<FeignClientSpecification>` |
| `query-map-encoder` refresh | **not refreshable** (`PageableSpringQueryMapEncoder` hard-coded) | configured class resolved (current, then default config), fallback `PageableSpringQueryMapEncoder` |
| `AutoRefreshCapabilityCustomizer` | resolves `FeignClientSpecification` by class name, calls `setConfiguration` reflectively (no compile-time dependency) | direct `instanceof` + setter |
| Auto-config registration | `spring.factories` effective (+ shipped, ignored `.imports`) | `AutoConfiguration.imports` only |
| Switch key | `microsphere.spring.cloud.openfeign.enabled` | identical |
| Hoxton build support | module profile adds test-scope `spring-cloud-starter-netflix-ribbon` | Ribbon excluded entirely |
