[中文](./zh/openfeign.md) · [1.x version line](../1.x/openfeign.md) · [User Guide index](../README.md)

# `microsphere-spring-cloud-openfeign` — User Guide (0.2.x)

Runtime **hot-swap of Feign components without a restart**: encoder, decoder, contract, retryer,
error decoder, query-map encoder, request interceptors, default headers and default query
parameters. The module wraps each component in a delegating proxy and re-resolves the delegate
whenever `spring.cloud.openfeign.client.config.*` changes, so a `POST /actuator/refresh` is enough
to change behaviour — no client rebuild, no JVM restart.

## Version Information

| Item | Value |
|---|---|
| Git branch | `release` |
| Version line | 0.2.x |
| Maven coordinates | `io.github.microsphere-projects:microsphere-spring-cloud-openfeign` |
| Artifact type | `jar` |
| Latest published release | **`0.2.26`** (branch head is `0.2.27-SNAPSHOT`, not published) |
| Repository | Maven Central |
| Java | **17+** |
| Spring Boot range | 3.0.x – 4.0.x |
| Spring Cloud range | 2022.0.x – 2025.1.x |
| Compile dependency | `microsphere-spring-cloud-commons` (see [commons.md](./commons.md)) |
| Required from you | `spring-cloud-starter-openfeign` (declared `optional` here) |
| Property namespace | `spring.cloud.openfeign.client.config.*` (the Boot 3 / Cloud 2022+ namespace; 1.x uses `feign.client.config.*`) |

## When to Use

- You need to change serialization, retry policy, an error mapping or an auth header **on a running
  node** — e.g. during an incident, a gradual rollout of a new decoder, or a contract fix that must
  land without redeploying.
- You want per-client overrides driven purely by externalized configuration (config server,
  Kubernetes ConfigMap, `/actuator/env`).
- You do **not** need it if your Feign configuration is static — skip the `@EnableFeignAutoRefresh`
  annotation entirely; nothing in this module then applies.

## Installation

```xml
<dependencies>
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-spring-cloud-openfeign</artifactId>
    </dependency>
    <!-- NOT transitive: this module declares the starter as optional -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    <!-- the refresh trigger comes from Spring Cloud Context -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
```

`microsphere-spring-cloud-commons` arrives transitively (the auto-refresh capability is injected
through its `SpecificationCustomizer` extension point). Load balancing is up to you, e.g.
`spring-cloud-starter-loadbalancer`.

## Version Compatibility

| Profile | Spring Boot | Spring Cloud | OpenFeign property namespace used |
|---|---|---|---|
| `spring-cloud-2022` | 3.0.13 | 2022.0.5 | `spring.cloud.openfeign.client.config.` |
| `spring-cloud-2023` | 3.3.13 | 2023.0.6 | same |
| `spring-cloud-2024` | 3.4.13 | 2024.0.3 | same |
| `spring-cloud-2025` **(default)** | 3.5.15 | 2025.0.3 | same |
| `spring-cloud-2025.1` | 4.0.8 | 2025.1.3 | same |

Java 17+ is mandatory. The module's context factory type is
`NamedContextFactory<FeignClientSpecification>` (the Spring Cloud OpenFeign 4.x-compatible
signature) and the configured `query-map-encoder` class is honoured on refresh (resolved through a
`MethodHandle` so it tolerates Spring Cloud versions that expose the accessor differently).

## Enabling: `@EnableFeignAutoRefresh`

```java
@SpringBootApplication
@EnableFeignClients(clients = MyClient.class)
@EnableFeignAutoRefresh          // no attributes
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
```

The annotation takes **no attributes** — not even `enabled`. Mechanically it only
`@Import`s the nested marker class `EnableFeignAutoRefresh.Marker`, and the auto-configuration
`FeignClientAutoRefreshAutoConfiguration` is gated by `@ConditionalOnBean(Marker.class)`. That is
why the module's own test can activate the feature by registering `EnableFeignAutoRefresh.Marker`
directly, without the annotation. Use the property switch (see *Disabling*) to turn the module off.

## Configurable Properties

Everything lives under `spring.cloud.openfeign.client.config.<client-name>.`, where
`<client-name>` is the Feign **context id** (`contextId`, else `name`), or the literal `default`:

| Key suffix | Feign component replaced | Notes |
|---|---|---|
| `retryer` | `feign.Retryer` | |
| `encoder` | `feign.codec.Encoder` | |
| `decoder` | `feign.codec.Decoder` | |
| `error-decoder` | `feign.codec.ErrorDecoder` | fallback `ErrorDecoder.Default` |
| `contract` | `feign.Contract` | |
| `query-map-encoder` | `feign.QueryMapEncoder` | fallback `PageableSpringQueryMapEncoder` |
| `request-interceptors[n]` | `feign.RequestInterceptor` (list) | index is ignored when resolving the component type |
| `default-request-headers.<name>` | injected by a generated `RequestInterceptor` | added only when the header is absent |
| `default-query-parameters.<name>` | injected by a generated `RequestInterceptor` | added only when the parameter is absent |

Accepted spellings: dashed (`error-decoder`) and camelCase (`errorDecoder`) are equivalent; the
component registry strips array indices (`request-interceptors[0]` → `request-interceptors`) before
mapping to the Feign type.

```yaml
spring:
  cloud:
    openfeign:
      client:
        config:
          default:
            encoder: com.example.feign.AEncoder
            decoder: com.example.feign.ADecoder
            error-decoder: com.example.feign.AErrorDecoder
            query-map-encoder: com.example.feign.AQueryMapEncoder
            retryer: com.example.feign.ARetry
            request-interceptors[0]: com.example.feign.ARequestInterceptor
            default-request-headers:
              app: my-app
            default-query-parameters:
              sign: my-sign
          my-client:            # per-client override, wins over default
            connect-timeout: 2000
            read-timeout: 5000
```

## Changing a Component at Runtime

Mirror of the module's own test case (`DecoderChangedTest`): the decoder of client `my-client`
switches from `ADecoder` to `BDecoder`.

```java
public class ADecoder implements Decoder {
    @Override
    public Object decode(Response response, Type type) { /* … */ return null; }
}

public class BDecoder implements Decoder {
    @Override
    public Object decode(Response response, Type type) { /* new behaviour */ return null; }
}

@FeignClient(contextId = "my-client", name = "my-client")
public interface MyClient {
    @GetMapping("echo")
    String echo(@RequestBody String value, @RequestParam("version") String version);
}
```

```bash
# 1) make the new value available to the environment — e.g. config server, ConfigMap, or /actuator/env
# 2) trigger the refresh
curl -s -X POST http://localhost:8080/actuator/refresh \
     -H 'Content-Type: application/json' \
     -d '[{"spring.cloud.openfeign.client.config.my-client.decoder":"com.example.feign.BDecoder"}]'
```

Sequence:

```
/actuator/refresh  →  ContextRefresher  →  EnvironmentChangeEvent (spring-cloud-context)
        →  FeignClientConfigurationChangedListener  (filters the "spring.cloud.openfeign.client.config." prefix)
        →  FeignComponentRegistry.refresh(clientName, changedKeys)
        →  DecoratedDecoder.refresh()   // delegate := null
        →  next MyClient.echo(..) call resolves the new bean/class lazily
```

No Feign client is rebuilt: the delegating wrapper stays in place and re-resolves its target on the
next invocation. `EnvironmentManager.setProperty(...)` (used by the module's tests) and publishing
`EnvironmentChangeEvent` yourself are equivalent triggers — the listener only cares about the event.

> `management.endpoint.refresh.enabled` defaults to `false` because of commons'
> `endpoints.properties`; set it to `true` and expose `refresh` before using the curl above.

## `default.*` vs Per-Client Semantics

| You change | Effect |
|---|---|
| `spring.cloud.openfeign.client.config.my-client.decoder` | only `my-client`'s decoder(s) are re-resolved, plus its interceptor composite if interceptors changed |
| `spring.cloud.openfeign.client.config.default.decoder` | **every** registered client whose component type matches `feign.codec.Decoder` is re-resolved, and all interceptor composites refresh when any `RequestInterceptor` change is present |

Resolution order inside a decorated component is **default config first, then the client's own
config** — a per-client value therefore overrides the default, while a deleted per-client value
falls back to the default. The same merge order (`putIfAbsent`, default first) applies to
`default-request-headers` and `default-query-parameters`.

## Requirements for Custom Component Classes

A class named in these properties must be instantiable by the module:

- **either** a `public` class with a `public` no-arg constructor implementing the Feign SPI
  (`feign.codec.Decoder`, `feign.RequestInterceptor`, …) — it does **not** need to be a Spring bean;
- **or** a bean of that type available in the client's **Feign child context** — the registry first
  asks the `NamedContextFactory` for a provider of that type and only instantiates the class when no
  bean exists.

Prefer the bean route when your component needs injection (ObjectMapper, an HTTP client, a metrics
registry): declare it in the `configuration = { … }` of `@FeignClient` or in the default
configuration, then name the class in the property.

## Disabling

| Method | Effect |
|---|---|
| `microsphere.spring.cloud.openfeign.enabled=false` | turns off **both** auto-configurations of this module (`ConditionalOnOpenFeignEnabled` uses `matchIfMissing=true`, so anything but `false` keeps them on) |
| Omit `@EnableFeignAutoRefresh` (or the `Marker` bean) | auto-refresh stays inactive: `FeignClientAutoRefreshAutoConfiguration` requires a `Marker` bean; `FeignAutoConfiguration` still registers the seed interceptor |
| Remove `feign.Feign` / `feign.Capability` / `FeignClientFactoryBean` from the classpath | class guards make the auto-configurations back off |
| Keep the module but stop using it | simply never change `spring.cloud.openfeign.client.config.*`; the delegating wrappers behave exactly like the original components |

## How It Works — 10 Facts

| # | Fact |
|---|---|
| 1 | Two auto-configurations are registered via `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`: `FeignAutoConfiguration` and `FeignClientAutoRefreshAutoConfiguration` (no `spring.factories` on this branch). |
| 2 | `FeignAutoConfiguration` (needs `FeignBuilderCustomizer`, runs after OpenFeign's own auto-config) contributes one `FeignBuilderCustomizer` that adds `NoOpRequestInterceptor.INSTANCE`, guaranteeing every builder has at least one interceptor so the interceptor pipeline is always engaged. |
| 3 | `FeignClientAutoRefreshAutoConfiguration` requires `feign.Capability` + `FeignClientFactoryBean` + a `EnableFeignAutoRefresh.Marker` bean, and runs after `SpecificationAutoConfiguration` from commons. |
| 4 | It defines two beans: `FeignComponentRegistry` (built with `FeignClientProperties.getDefaultConfig()` and the `BeanFactory`) and `AutoRefreshCapabilityCustomizer`; both `@ConditionalOnMissingBean`. |
| 5 | On `ApplicationReadyEvent` it registers `FeignClientConfigurationChangedListener` **manually** (not as a bean) so it lands after `ConfigurationPropertiesRebinder` — rebinding of `FeignClientProperties` happens before the refresh logic reads the environment. |
| 6 | The capability reaches Feign by being **prepended** into each `default.`-prefixed `FeignClientSpecification`'s `configuration()` array; Spring then creates one `AutoRefreshCapability` per Feign child context. |
| 7 | `AutoRefreshCapability` resolves the owning client name from `spring.cloud.openfeign.client.name` inside the child context and wraps `Retryer`, `Contract`, `Decoder`, `Encoder`, `ErrorDecoder`, `QueryMapEncoder` in `Decorated*` proxies, registering each with the registry; `RequestInterceptor`s are folded into one `CompositedRequestInterceptor` per client (the composite is handed to the builder exactly once; later interceptors become no-ops). |
| 8 | `refresh()` on any `DecoratedFeignComponent` only sets its `volatile delegate` to `null`; the next call lazily re-resolves the current bean or instantiates the currently configured class. |
| 9 | `CompositedRequestInterceptor.refresh()` rebuilds its ordered set from default + client config: configured interceptor classes, a generated header interceptor for `default-request-headers.*`, and a generated parameter interceptor for `default-query-parameters.*`; `apply()` iterates under a lock so refresh is request-safe. |
| 10 | Only keys starting with `spring.cloud.openfeign.client.config.` trigger anything; the first path segment after the prefix selects the client (`default` fans out to all clients of the same component type). |

## Limitations and Troubleshooting

| Symptom / limit | Detail |
|---|---|
| Component did not change | the key did not start with `spring.cloud.openfeign.client.config.`, the client name is neither the context id nor `default`, or no `EnvironmentChangeEvent` was published |
| `NoSuchMethod`/version noise around `query-map-encoder` | the accessor is resolved via a `MethodHandle`; on Spring Cloud versions without `FeignClientConfiguration.getQueryMapEncoder()`, the fallback `PageableSpringQueryMapEncoder` is used |
| Interceptor ran twice | expected only if you add your own `RequestInterceptor` bean *and* configure classes; the composite de-duplicates by class within one client but is insertion-ordered |
| A component needing dependencies fails to construct | give it as a bean in the Feign child context instead of a bare class name |
| `InvocationHandlerFactory`, `Client`, `Feign.Builder` | **not** hot-swapped — only the seven component types in the table above, plus headers/parameters, are decorated |
| Timeout / logger settings (`connect-timeout`, `read-timeout`, `logger-level`) | handled by Spring Cloud OpenFeign itself, not by this module |

## See Also

- [commons.md](./commons.md) — `SpecificationCustomizer`, the refresh/actuator defaults, feature catalogue
- [parent.md](./parent.md) · [dependencies.md](./dependencies.md)
- [1.x line](../1.x/openfeign.md) — same behaviour with the `feign.client.config.` namespace
- [User Guide index](../README.md)
