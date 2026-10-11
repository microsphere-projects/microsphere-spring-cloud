[中文](./zh/commons.md) · [0.2.x version line](../current/commons.md) · [User Guide index](../README.md)

# `microsphere-spring-cloud-commons` — User Guide (0.1.x)

Spring Cloud Commons extensions for Spring Boot 2.x applications: richer service
registration lifecycle (events, customizers, multi-registry), Actuator endpoints for
manual online/offline control, union/reactive discovery composition, web-mapping
metadata propagation, a feature catalogue for `/actuator/features`, and runtime-tunable
embedded Tomcat — all opt-in, all class-guarded.

## Version Information

| Item | Value |
|---|---|
| Git branch | `release-1.x` |
| Version line | 0.1.x |
| Latest published release | **`0.1.26`** (branch head is `0.1.27-SNAPSHOT`) |
| Maven coordinates | `io.github.microsphere-projects:microsphere-spring-cloud-commons` |
| Repository | Maven Central |
| Java baseline | Java 8 |
| Servlet namespace | `javax.*` |
| Spring Boot range | 2.3.x – 2.6.x |
| Spring Cloud range | Hoxton.SR12 – 2021.0.x |
| Auto-config registration | `META-INF/spring.factories` (Boot 2 mechanism; the shipped `AutoConfiguration.imports` file is ignored by Boot 2.3–2.6) |

## Setup

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-spring-cloud-commons</artifactId>
    <version>0.1.26</version> <!-- or manage via the 0.1.x BOM / parent -->
</dependency>
```

All third-party dependencies of this module (`spring-cloud-commons`,
`spring-cloud-context`, registry starters, `spring-boot-starter-web`/`-webflux`/`-actuator`, …)
are declared **optional**; the module hard-depends only on the Microsphere Spring Boot
0.1.34 modules and `aspectjweaver`. You must supply your own Spring Boot / Spring Cloud /
registry client; each feature switches itself on only when the relevant classes are on
your classpath, and stays silent otherwise.

---

## 1. Registration Lifecycle Events

**When to use:** audit, log, or hook logic around every register/deregister call of *any*
Spring Cloud `ServiceRegistry` (Eureka, Nacos, Consul, Zookeeper, …) without touching
registry-specific code.

`ServiceRegistryAutoConfiguration` always imports `EventPublishingRegistrationAspect`
(it requires AspectJ, present via the module's mandatory `aspectjweaver` dependency).
The aspect intercepts `ServiceRegistry.register(..)` / `deregister(..)` and publishes:

| Event | Fired | Type enum |
|---|---|---|
| `RegistrationPreRegisteredEvent` | before `register` | `PRE_REGISTERED` |
| `RegistrationRegisteredEvent` | after `register` | `REGISTERED` |
| `RegistrationPreDeregisteredEvent` | before `deregister` | `PRE_DEREGISTERED` |
| `RegistrationDeregisteredEvent` | after `deregister` | `DEREGISTERED` |

All extend `RegistrationEvent` and expose `getRegistration()` and `getRegistry()`, plus
`isPreRegistered()`, `isRegistered()`, `isPreDeregistered()`, `isDeregistered()`.

```java
@Component
public class RegistrationLogger {

    @EventListener
    public void onRegistered(RegistrationRegisteredEvent event) {
        Registration r = event.getRegistration();
        System.out.println("Registered " + r.getServiceId() + " at " + r.getHost() + ":" + r.getPort());
    }
}
```

### `RegistrationCustomizer`

`public interface RegistrationCustomizer { void customize(Registration registration); }`
is applied to the registration **before** each register call (during the pre-registered
phase). Register beans of this type to stamp metadata, instance ids, etc.:

```java
@Component
public class ZoneCustomizer implements RegistrationCustomizer {
    @Override
    public void customize(Registration registration) {
        registration.getMetadata().put("zone", "sh-a");
    }
}
```

Events are published by the **individual** registries: the aspect ignores beans of type
`MultipleServiceRegistry` to avoid double publishing when [multiple registration](#2-multiple-registration) is active.

Separately, the module defines `ServiceInstancesChangedEvent`
(`getServiceName()`, `getServiceInstances()`, `processed()` / `isProcessed()`) as a
standard event other modules can publish; it is not auto-published by this module.

---

## 2. Multiple Registration

**When to use:** one application must register into **two or more registries at the
same time** (e.g. Nacos + Zookeeper) while the rest of Spring Cloud still sees a single
`Registration` / `ServiceRegistry`.

This is **opt-in**:

```properties
microsphere.spring.cloud.multiple-registration.enabled=true
```

When enabled (and at least one `Registration` and one `ServiceRegistry` bean exist),
three `@Primary` beans are installed:

| Bean | Role |
|---|---|
| `MultipleRegistration` | composite `Registration`; delegates id/service/host/port/secure/URI to the *default* registration; exposes `special(NacosRegistration.class)` to fetch a specific underlying registration; `getMetadata()` returns a shared `RegistrationMetaData` map that propagates every `put`/`remove` to **all** underlying registrations |
| `MultipleServiceRegistry` | fans `register`/`deregister`/`setStatus`/`close` out to every registry, pairing each with its matching registration type; `getStatus` reads only the default registry |
| `MultipleAutoServiceRegistration` | drives the composite through the standard `AbstractAutoServiceRegistration` lifecycle |

Which registration is "default" depends on bean iteration order (the **last** wins). To
pin the defaults explicitly, the keys
`microsphere.spring.cloud.default-registration.type` and
`microsphere.spring.cloud.default-service-registry.type` (declaring a `Class` value) are
defined for this area; see the [0.1.x sources](https://github.com/microsphere-projects/microsphere-spring-cloud/tree/release-1.x)
for current wiring. The related `microsphere.spring.cloud.composite-registration.enabled`
key exists but is not consumed by any auto-configuration.

---

## 3. Service Registration / Deregistration Actuator Endpoints

**When to use:** take an instance offline before a deployment (graceful drain) or push it
back online manually, over HTTP.

Two Actuator endpoints are provided:

| Endpoint id | Operation | HTTP | Effective URL (module defaults) |
|---|---|---|---|
| `serviceRegistration` | `metadata()` — status snapshot | GET | `/actuator/microsphere/service/registration` |
| `serviceRegistration` | `start()` — register if not running | POST | `/actuator/microsphere/service/registration` |
| `serviceDeregistration` | `stop()` — deregister if running | POST | `/actuator/microsphere/service/deregistration` |

Both are **disabled by default** and path-mapped by the module's
`META-INF/config/default/endpoints.properties` (loaded as lowest-priority defaults).
Enable and expose them:

```yaml
management:
  endpoint:
    serviceRegistration:
      enabled: true
    serviceDeregistration:
      enabled: true
  endpoints:
    web:
      exposure:
        include: health,info,refresh,serviceRegistration,serviceDeregistration
```

Then:

```bash
# inspect registration state
curl http://localhost:8080/actuator/microsphere/service/registration

# (re-)register the service; returns the *previous* running state
curl -X POST http://localhost:8080/actuator/microsphere/service/registration

# deregister the service; returns the state *before* deregistration
curl -X POST http://localhost:8080/actuator/microsphere/service/deregistration
```

The GET response includes `application-name`, `registration`, `port`, `status`
(from `ServiceRegistry.getStatus`), `running`, `enabled`, `phase`, `order`,
`managementRegistration`, `config` where available.

> Caveat: the "running" flag the endpoints track is a **static** field shared JVM-wide
> across the two endpoints of one instance — treat it as a coarse indicator, the
> registry itself stays authoritative.

---

## 4. Simple / In-Memory Registration (No External Registry)

**When to use:** demos, integration tests, or environments where no real registry is
deployed, but you still want a `Registration` bean, lifecycle events and the Actuator
endpoints above to work.

```properties
microsphere.spring.cloud.service-registry.auto-registration.simple.enabled=true
```

Opt-in. When enabled it installs:

- `DefaultRegistration` (a `Registration` built from `spring.application.name`,
  `server.port` and the first non-loopback host; instance id = `host:port`);
- `InMemoryServiceRegistry` (a `ConcurrentMap`-backed `ServiceRegistry`, only if no
  other `ServiceRegistry` bean exists) — `register` puts, `deregister` removes,
  `setStatus`/`getStatus` store under the metadata key `_status_`.

A sibling primitive, `SimpleServiceRegistry`, writes into the *config-file instances* of
`SimpleDiscoveryProperties` (or its reactive counterpart), so "discovered" services match
what you registered without any network.

---

## 5. Union Discovery Client

**When to use:** several `DiscoveryClient` implementations coexist (e.g. simple
config-driven + a real registry) and you want **all** instances of a service, not just
those of the composite winner.

```properties
microsphere.spring.cloud.client.discovery.mode=union
```

Installs `UnionDiscoveryClient` (order = `HIGHEST_PRECEDENCE`), which aggregates every
`DiscoveryClient` bean except `CompositeDiscoveryClient` and itself. Semantics:
`getInstances(serviceId)` concatenates all non-empty results (**duplicates preserved**);
`getServices()` returns the deduplicated union; `description()` = `"Union Discovery Client"`.

---

## 6. Reactive Discovery Adapter

**When to use:** your registry only offers a **reactive** client (e.g. a reactive
Kubernetes/Consul setup) but non-reactive code calls `DiscoveryClient` (Ribbon-era
load balancers, blocking Feign in the 0.1.x line, library integrations).

Automatic — no property. When `ReactiveDiscoveryClient` beans exist and discovery is
enabled, `ReactiveDiscoveryClientAdapter` is registered as a blocking `DiscoveryClient`
that subscribes and blocks-collects the `Flux` (using `mono.toFuture().get()` when called
from a non-blocking thread, `mono.block()` otherwise).

Companion utility `DiscoveryUtils` converts `SimpleDiscoveryProperties` ⇄
`SimpleReactiveDiscoveryProperties`, keeping the file-based instances in sync.
(1.x note: the copy uses `DefaultServiceInstance.setUri(...)`; see
[Differences vs 0.2.x](#differences-vs-02x).)

---

## 7. Web Metadata in Service Registration

**When to use:** you want consumers of discovery metadata to know each instance's
context path and URL mappings (API-gateway tooling, contract checks, dashboards).

Automatic on servlet (`WebMvcServiceRegistryAutoConfiguration`) and reactive
(`WebFluxServiceRegistryAutoConfiguration`) applications, whenever a `Registration`
bean exists. On `WebEndpointMappingsReadyEvent` it writes into the registration metadata:

| Metadata key | Content |
|---|---|
| `web.context-path` | `${server.servlet.context-path:}` (empty for WebFlux) |
| `web.mappings` | URL-encoded JSON array of `WebEndpointMapping`; built-in `org.springframework.*` filter mappings, DispatcherServlet mappings and Actuator endpoints (under `${management.endpoints.web.base-path:/actuator}`) are excluded |

On the 0.1.x line the Web MVC code is `javax.servlet`-based (it inspects
`javax.servlet.Filter` registrations). Read the data back with
`ServiceInstanceUtils.getWebEndpointMappings(serviceInstance)`; helpers
`getUri(instance)` / `getUriString(instance)` resolve `http(s)://host:port` (falling back
to 443/80 when the port is ≤ 0), and `getMetadata` / `setMetadata` / `removeMetadata`
give typed access to the metadata map.

---

## 8. Features Catalogue for `/actuator/features`

**When to use:** inspect at runtime which optional capabilities are actually active on
your classpath.

`ConfigurationPropertyHasFeaturesAutoConfiguration` reads
`META-INF/config/default/features.yaml` (shipped catalogue) merged with your own
properties under:

```yaml
microsphere:
  spring:
    cloud:
      features:
        abstract:      # module -> list of class names ("abstract" features)
          my-module:
            - com.acme.MyFeature
        named:         # module -> feature name -> class
          my-module:
            my-feature: com.acme.MyFeature
```

Each module key becomes a `HasFeatures` singleton named `<module>.features`, rendered by
Spring Cloud's `/actuator/features` endpoint (missing classes are reported as absent with
a WARN, never a failure). The catalogue shipped with the commons module documents:
`UnionDiscoveryClient`, `ReactiveDiscoveryClientAdapter`, `MultipleRegistration`,
`MultipleServiceRegistry`, `MultipleAutoServiceRegistration`,
`EventPublishingRegistrationAspect`, `SimpleServiceRegistry`,
`SimpleAutoServiceRegistration`, `InMemoryServiceRegistry`,
`ServiceRegistrationEndpoint`, `ServiceDeregistrationEndpoint`,
`SpecificationBeanPostProcessor`, `SpecificationCustomizer`,
`TomcatDynamicConfigurationListener`.

Requirements: `spring.cloud.features.enabled` not `false` (default on), the
`FeaturesEndpoint` class present, and the `features` endpoint available
(`management.endpoint.features.enabled=true`, which the module's default properties
already set).

---

## 9. `SpecificationCustomizer` (Named-Context Extension)

**When to use:** observe or alter the per-client `NamedContextFactory.Specification`
definitions used by Feign (and Ribbon in this line) **before** the child contexts start —
the hook that the sibling OpenFeign module itself uses to inject its refresh capability.

Automatic when a `Specification` bean exists. Register customizers:

```java
@Component
public class LoggingSpecificationCustomizer implements SpecificationCustomizer {
    @Override
    public void customize(NamedContextFactory.Specification specification, String beanName) {
        System.out.println("Feign/Ribbon specification: " + specification.getName()
                + " <- " + beanName);
    }
}
```

`SpecificationBeanPostProcessor` applies every sorted `SpecificationCustomizer` bean to
each specification. This is also documented in the
[OpenFeign 0.1.x guide](./openfeign.md).

---

## 10. Fault Tolerance

### 10.1 Runtime-Tunable Embedded Tomcat

**When to use:** adjust thread pools, backlog, timeouts and header/POST sizes on a
running Tomcat after `/actuator/refresh`, instead of rolling restarts.

Enabled by default (`microsphere.spring.cloud.fault-tolerance.tomcat.enabled`,
default `true`) when embedded Tomcat, Spring Cloud Context and a servlet web app are
present. On `WebServerInitializedEvent` the module registers a
`TomcatDynamicConfigurationListener`; on each `EnvironmentChangeEvent` it re-binds
changed `server.*` keys and applies only values that are **changed, positive and
convertible**:

| Property key (0.1.x) | Tomcat setting |
|---|---|
| `server.tomcat.threads.min-spare` | core thread count |
| `server.tomcat.threads.max` | max threads |
| `server.tomcat.accept-count` | endpoint network backlog |
| `server.tomcat.connection-timeout` | connection timeout (ms) |
| `server.tomcat.max-connections` | max connections |
| `server.max-http-header-size` | HTTP headers max size (bytes) |
| `server.tomcat.max-swallow-size` | HTTP body max swallow size (bytes) |
| `server.tomcat.max-http-form-post-size` | max form POST size (bytes) |

> **1.x key names:** the header-size property is **`server.max-http-header-size`** —
> Spring Boot 2's `ServerProperties.getMaxHttpHeaderSize()`. (0.2.x / Boot 3 renamed it
> to `server.max-http-request-header-size`.) Not refreshable: `server.port`, `server.ssl.*`,
> context path.

Change a value in your externalized configuration, then:

```bash
curl -X POST http://localhost:8080/actuator/refresh \
     -H 'Content-Type: application/json' -d '["server.tomcat.threads.max"]'
```

No restart, no client rebuild — the live Tomcat `Protocol`/`Connector` is updated.

### 10.2 Warm-Up Weight Primitives

**When to use:** building a custom load balancer that should ramp traffic onto fresh
instances. These are **library primitives only** — nothing in this module consumes the
`warmup-time`/`weight` properties; downstream balancer implementations do.

| Property | Default | Meaning |
|---|---|---|
| `microsphere.spring.cloud.fault-tolerance.warmup-time` | 600000 (ms, 10 min) | warm-up window |
| `microsphere.spring.cloud.fault-tolerance.weight` | 100 | full target weight |
| `microsphere.spring.cloud.fault-tolerance.load-balancer.` | — | namespace reserved for per-balancer settings |

`WeightedRoundRobin` implements smooth weighted round-robin accumulation;
`LoadBalancerUtils.calculateWarmupWeight(uptime, warmup, weight)` applies a quadratic
ramp clamped to `[1, weight]`.

---

## Conditional Annotations Reference

Meta-annotations you can put on your own `@Configuration` to mirror the module's
activation logic:

| Annotation | Checks |
|---|---|
| `@ConditionalOnFeaturesEnabled` | `spring.cloud.features.enabled` (default on) |
| `@ConditionalOnFeaturesAvailable` | above **and** `FeaturesEndpoint` class **and** endpoint available |
| `@ConditionalOnBlockingDiscoveryAvailable` | `DiscoveryClient` class + `spring.cloud.discovery.enabled` + blocking discovery enabled |
| `@ConditionalOnReactiveDiscoveryAvailable` | `ReactiveDiscoveryClient` class + `spring.cloud.discovery.enabled` + reactive discovery enabled |
| `@ConditionalOnAutoServiceRegistrationEnabled` | `spring.cloud.service-registry.auto-registration.enabled` (default on) |
| `@ConditionalOnAutoServiceRegistrationAvailable` | `Registration` class + a `Registration` **bean** + above |
| `@ConditionalOnMultipleRegistrationEnabled` | `microsphere.spring.cloud.multiple-registration.enabled` explicitly present and not `false` (pure opt-in) |
| `@ConditionalOnLoadBalancerEnabled` | `spring.cloud.loadbalancer.enabled` (default on; consumed by downstream modules) |
| `@ConditionalOnUtilEnabled` | `spring.cloud.util.enabled` (default on; consumed by downstream modules) |

(External, inherited from microsphere-spring-boot 0.1.x: `@ConditionalOnWebMvcAvailable`,
`@ConditionalOnWebFluxAvailable`, `@ConditionalOnActuatorEndpointPresent`.)

## Property Reference

| Property | Type | Default | Purpose |
|---|---|---|---|
| `spring.cloud.service-registry.auto-registration.enabled` | boolean | `true` | master switch Spring Cloud's auto-registration honors |
| `spring.cloud.features.enabled` | boolean | `true` | features endpoint support switch |
| `spring.cloud.loadbalancer.enabled` | boolean | `true` | guarded by `@ConditionalOnLoadBalancerEnabled` |
| `spring.cloud.util.enabled` | boolean | `true` | guarded by `@ConditionalOnUtilEnabled` |
| `microsphere.spring.cloud.multiple-registration.enabled` | boolean | `false` | enable multi-registry aggregation |
| `microsphere.spring.cloud.default-registration.type` | class | — | default underlying registration for the composite |
| `microsphere.spring.cloud.default-service-registry.type` | class | — | default underlying registry for the composite |
| `microsphere.spring.cloud.composite-registration.enabled` | boolean | `false` | declared; not consumed by any auto-configuration |
| `microsphere.spring.cloud.client.discovery.mode` | string | — | set to `union` to install `UnionDiscoveryClient` |
| `microsphere.spring.cloud.service-registry.auto-registration.simple.enabled` | boolean | off | opt into simple/in-memory registration stack |
| `microsphere.spring.cloud.fault-tolerance.tomcat.enabled` | boolean | `true` | runtime-tunable Tomcat listener |
| `microsphere.spring.cloud.fault-tolerance.warmup-time` | long | `600000` | warm-up window (ms), for downstream balancers |
| `microsphere.spring.cloud.fault-tolerance.weight` | int | `100` | target weight, for downstream balancers |
| `microsphere.spring.cloud.fault-tolerance.load-balancer.` | prefix | — | reserved for per-balancer settings |
| `microsphere.spring.cloud.features.abstract.*` | map | shipped catalogue | abstract feature declarations |
| `microsphere.spring.cloud.features.named.*` | map | shipped catalogue | named feature declarations |
| `management.endpoint.serviceRegistration.enabled` | boolean | `false` (module default) | enable registration endpoint |
| `management.endpoint.serviceDeregistration.enabled` | boolean | `false` (module default) | enable deregistration endpoint |
| `management.endpoints.web.path-mapping.serviceRegistration` | string | `microsphere/service/registration` | endpoint path remap |
| `management.endpoints.web.path-mapping.serviceDeregistration` | string | `microsphere/service/deregistration` | endpoint path remap |

## Version Compatibility

Works with Spring Boot **2.3.x – 2.6.x** and Spring Cloud **Hoxton.SR12 – 2021.0.x**
(the parent profiles `spring-cloud-hoxton`, `spring-cloud-2020`, `spring-cloud-2021`),
on Java **8+** with the `javax.*` servlet namespace. Registry clients (Eureka, Nacos,
Zookeeper, Consul) are optional; when present they activate the corresponding
composition (events, multiple registration, endpoints, web metadata).

## Differences vs 0.2.x

| Aspect | 0.1.x (`release-1.x`) | 0.2.x (`release`) |
|---|---|---|
| Auto-config registration | `META-INF/spring.factories` is effective (Boot 2 ignores `AutoConfiguration.imports`, which is also shipped) | `AutoConfiguration.imports` is authoritative (Boot 3+) |
| Web MVC servlet code | `javax.servlet.Filter` | `jakarta.servlet.Filter` |
| Tomcat dynamic header property | `server.max-http-header-size` (`getMaxHttpHeaderSize`) | `server.max-http-request-header-size` (`getMaxHttpRequestHeaderSize`) |
| `ServiceInstanceUtils.setProperties` | copies `getUri()` onto `DefaultServiceInstance` (0.2.x Spring Cloud removed that setter) | copies the `secure` flag |
| Java baseline / language level | Java 8 | 17 |
| Upstream `microsphere-spring-boot` | 0.1.34 | 0.2.34 |
| Testcontainers artifact in builds | `org.testcontainers:junit-jupiter` | `org.testcontainers:testcontainers-junit-jupiter` |
| Everything else | same classes, same property keys, same behavior | — |
