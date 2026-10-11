[中文](./zh/commons.md) · [1.x version line](../1.x/commons.md) · [User Guide index](../README.md)

# `microsphere-spring-cloud-commons` — User Guide (0.2.x)

The core module of microsphere-spring-cloud. It adds capabilities that Spring Cloud Commons does
not provide out of the box: **registering one service into several registries at once**,
**observable registration lifecycle events**, **manual online/offline control over Actuator**,
**a self-contained in-memory registry for demos and tests**, **union aggregation of discovery
clients**, **reactive→blocking discovery bridging**, **web endpoint metadata on service
instances**, **a feature catalogue for `/actuator/features`**, and **runtime-tunable embedded
Tomcat** — all opt-in and class-guarded.

## Version Information

| Item | Value |
|---|---|
| Git branch | `release` |
| Version line | 0.2.x |
| Maven coordinates | `io.github.microsphere-projects:microsphere-spring-cloud-commons` |
| Artifact type | `jar` |
| Latest published release | **`0.2.26`** (branch head is `0.2.27-SNAPSHOT`, not published) |
| Repository | Maven Central |
| Java | **17+** |
| Servlet namespace | `jakarta.*` |
| Spring Boot range | 3.0.x – 4.0.x |
| Spring Cloud range | 2022.0.x – 2025.1.x |
| Requires (compile scope) | `microsphere-spring-boot-core`, `-webmvc`, `-webflux`, `-actuator` (0.2.34), `org.aspectj:aspectjweaver` |
| Parent | `microsphere-spring-cloud-parent` — see [parent.md](./parent.md) |

## Capability Map

| Capability | Switch | Default | Section |
|---|---|---|---|
| Multiple service registry (Nacos + Eureka + … simultaneously) | `microsphere.spring.cloud.multiple-registration.enabled` | `false` | [Multiple registries](#multiple-service-registry) |
| Registration lifecycle events + customizers | none (registers whenever AspectJ + `Registration` are present) | on | [Lifecycle events](#registration-lifecycle-events) |
| Manual register/deregister endpoints | `management.endpoint.serviceRegistration.enabled`, `management.endpoint.serviceDeregistration.enabled` | `false` | [Actuator endpoints](#actuator-endpoints) |
| Simple in-memory registration stack | `microsphere.spring.cloud.service-registry.auto-registration.simple.enabled` | off (opt-in) | [Simple registry](#simple--in-memory-registry-stack) |
| Union aggregation of all discovery clients | `microsphere.spring.cloud.client.discovery.mode=union` | off | [Union discovery](#union-discovery-client) |
| Reactive→blocking discovery adapter | implicit (reactive + blocking discovery enabled) | on when both present | [Reactive adapter](#reactivediscoveryclientadapter) |
| Web mappings/context path in instance metadata | implicit (auto-service registration available) | on | [Web endpoint metadata](#web-endpoint-metadata) |
| Feature catalogue for `/actuator/features` | `spring.cloud.features.enabled`, `microsphere.spring.cloud.features.*` | `true` | [Features](#feature-catalogue) |
| Customizing Feign/LoadBalancer named contexts | requires a `SpecificationCustomizer` bean | on when a `Specification` bean exists | [SpecificationCustomizer](#specificationcustomizer) |
| Dynamic embedded Tomcat settings on refresh | `microsphere.spring.cloud.fault-tolerance.tomcat.enabled` | `true` | [Fault tolerance](#fault-tolerance) |

## Installation

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-spring-cloud-commons</artifactId>
</dependency>
```

Version comes from the [BOM](./dependencies.md) or the [parent](./parent.md). Then add what your
feature needs — every third-party dependency of this module is `optional`, so nothing but
Microsphere Spring Boot and `aspectjweaver` is pulled transitively:

```xml
<dependencies>
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-spring-cloud-commons</artifactId>
    </dependency>
    <!-- registry clients you actually use -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <!-- required for the registration events and the actuator endpoints -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
```

## Version Compatibility

Built and tested against the parent's Spring Cloud profiles
(see [parent.md](./parent.md#version-compatibility-profiles)):

| Profile | Spring Boot | Spring Cloud | Spring Cloud Alibaba |
|---|---|---|---|
| `spring-cloud-2022` | 3.0.13 | 2022.0.5 | 2022.0.0.2 |
| `spring-cloud-2023` | 3.3.13 | 2023.0.6 | 2023.0.3.4 |
| `spring-cloud-2024` | 3.4.13 | 2024.0.3 | 2023.0.3.4 |
| `spring-cloud-2025` **(default)** | 3.5.15 | 2025.0.3 | 2025.0.0.0 |
| `spring-cloud-2025.1` | 4.0.8 | 2025.1.3 | 2025.1.0.0 |

Java 17+ is mandatory. Auto-configurations are registered through
`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` (Spring Boot 3
mechanism) and are class-guarded, so absent providers simply switch their feature off.

## Multiple Service Registry

**Use when** one application instance must appear in two or more registries at the same time —
typical cases: a migration between Nacos and Eureka/Zookeeper, or a service consumed by both a
Spring Cloud and a legacy registry-based stack.

Enable it and the module replaces the single `Registration` / `ServiceRegistry` /
`AutoServiceRegistration` beans with three `@Primary` composites:

| Bean | Type | Built from |
|---|---|---|
| `multipleRegistration` | `MultipleRegistration` (implements `Registration`) | all `Registration` beans |
| `multipleServiceRegistry` | `MultipleServiceRegistry` (implements `ServiceRegistry<MultipleRegistration>`) | all `ServiceRegistry` beans, keyed by bean name |
| `multipleAutoServiceRegistration` | `MultipleAutoServiceRegistration` | the two beans above + `AutoServiceRegistrationProperties` |

```yaml
spring:
  application:
    name: order-service
  cloud:
    service-registry:
      auto-registration:
        enabled: true          # default is true; shown for clarity
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
        namespace: f7ad23e0-f581-4516-9420-8c50aa6a7b89
        metadata:
          key: value
eureka:
  client:
    enabled: true
    service-url:
      defaultZone: http://127.0.0.1:8761/eureka/

microsphere:
  spring:
    cloud:
      multiple-registration:
        enabled: true
      # documented/declared type hints (see the note below)
      default-registration:
        type: com.alibaba.cloud.nacos.registry.NacosRegistration
      default-service-registry:
        type: com.alibaba.cloud.nacos.registry.NacosServiceRegistry
```

Behaviour you must know:

- `register`, `deregister`, `setStatus` and `close` are **iterated over every registry**. For each
  one, the provider-specific `Registration` is resolved and `null` results are skipped, so a
  registry without a matching registration is simply not involved.
- `getStatus(MultipleRegistration)` reads the status **only from the default registry**.
- The **default registration/registry is the last entry in injection order** — that is the
  iteration-order-dependent rule the composites use. The two `…type` keys above are published as
  configuration metadata (and are used by the module's own multi-registry integration test); they
  document your intent, but do not rely on them to reorder the beans. To pin a specific provider,
  control bean ordering or use `special(...)` explicitly.
- `MultipleRegistration.getMetadata()` returns a shared `RegistrationMetaData` map: a `put`,
  `remove`, `putAll` or `clear` on it is **propagated to every underlying registration's metadata**,
  which is why one metadata write covers all registries.

To reach a provider-specific API (for example Eureka's `ApplicationInfoManager`):

```java
@Autowired
private Registration registration; // the @Primary MultipleRegistration

public InstanceInfo eurekaInstanceInfo() {
    EurekaRegistration eureka = this.registration.special(EurekaRegistration.class);
    if (eureka == null) {
        throw new IllegalStateException("No EurekaRegistration in this JVM");
    }
    return eureka.getApplicationInfoManager().getInfo();
}
```

`special(Class<T>)` returns the last registered `T`; asking for `Registration.class` returns the
composite itself. `microsphere.spring.cloud.composite-registration.enabled` is a declared constant
reserved for future use — no auto-configuration in 0.2.x consumes it.

## Registration Lifecycle Events

**Use when** you must react to (or adjust) registration right before/after it reaches a registry:
syncing provider-native instance objects, adding build metadata, draining traffic in your own way.

`EventPublishingRegistrationAspect` is imported by `ServiceRegistryAutoConfiguration` whenever both
`org.aspectj.lang.annotation.Aspect` and `org.springframework.cloud.client.serviceregistry.Registration`
are on the classpath — this is **not** gated by `microsphere.spring.cloud.multiple-registration.enabled`.
It advises `ServiceRegistry.register(..)` and `ServiceRegistry.deregister(..)`:

| Moment | Event published | `RegistrationEvent.Type` |
|---|---|---|
| before `register` | `RegistrationPreRegisteredEvent` | `PRE_REGISTERED` |
| after `register` | `RegistrationRegisteredEvent` | `REGISTERED` |
| before `deregister` | `RegistrationPreDeregisteredEvent` | `PRE_DEREGISTERED` |
| after `deregister` | `RegistrationDeregisteredEvent` | `DEREGISTERED` |

Every event exposes `getRegistration()`, `getRegistry()`, `getSource()` and the predicates
`isPreRegistered()`, `isRegistered()`, `isPreDeregistered()`, `isDeregistered()`.

> Events are published by the **individual** registries: `MultipleServiceRegistry` calls are
> ignored by the aspect (`isIgnored(...)`), so a composite register does not produce duplicate
> events — you get one event per real provider registry.

```java
@Component
public class EurekaMetadataSyncListener implements ApplicationListener<RegistrationPreRegisteredEvent> {

    @Override
    public void onApplicationEvent(RegistrationPreRegisteredEvent event) {
        Registration registration = event.getRegistration();
        registration.getMetadata().put("my-key", "my-value");
        if (registration instanceof EurekaRegistration) {
            EurekaRegistration eurekaRegistration = (EurekaRegistration) registration;
            InstanceInfo instanceInfo = eurekaRegistration.getApplicationInfoManager().getInfo();
            // sync metadata from Registration to Eureka's InstanceInfo before it is sent
            instanceInfo.getMetadata().putAll(registration.getMetadata());
        }
    }
}
```

### `RegistrationCustomizer`

Where a listener *observes*, a `RegistrationCustomizer` *mutates*: all sorted beans of this type are
applied to the registration in the same pre-register step, right after
`RegistrationPreRegisteredEvent` is published.

```java
@Component
public class BuildInfoRegistrationCustomizer implements RegistrationCustomizer {

    @Override
    public void customize(Registration registration) {
        Map<String, String> metadata = registration.getMetadata();
        metadata.put("build.sha", BuildProperties.gitSha());
        metadata.put("region", System.getenv().getOrDefault("REGION", "cn-hangzhou"));
    }
}
```

Interface: `io.microsphere.spring.cloud.client.service.registry.RegistrationCustomizer`
(single method `void customize(Registration registration)`), ordered via `Ordered`/`@Order` because
it is resolved with `getSortedBeans`.

## Actuator Endpoints

**Use when** an operator (or a deployment pipeline) must take an instance online/offline without
restarting it — e.g. before a warm restart, or during a canary rollout.

Two endpoints are provided: `ServiceRegistrationEndpoint` (`id = "serviceRegistration"`) and
`ServiceDeregistrationEndpoint` (`id = "serviceDeregistration"`). **Both are disabled by default**
and both are re-mapped under a Microsphere-specific path. The module ships these defaults
(`META-INF/config/default/endpoints.properties`), loaded as lowest-priority properties:

```properties
management.endpoint.serviceRegistration.enabled = false
management.endpoint.serviceDeregistration.enabled = false
management.endpoints.web.path-mapping.serviceRegistration = microsphere/service/registration
management.endpoints.web.path-mapping.serviceDeregistration = microsphere/service/deregistration
```

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
        include: health,info,serviceRegistration,serviceDeregistration
```

| Effective URL | Operation | Effect | Returns |
|---|---|---|---|
| `GET /actuator/microsphere/service/registration` | read | instance state snapshot | JSON map |
| `POST /actuator/microsphere/service/registration` | write (`start()`) | registers if not running | **previous** running state |
| `POST /actuator/microsphere/service/deregistration` | write (`stop()`) | deregisters if running | state **before** deregistration |

```bash
# inspect
curl -s http://localhost:8080/actuator/microsphere/service/registration | jq

# take the instance out of rotation
curl -s -X POST http://localhost:8080/actuator/microsphere/service/deregistration
# true  -> it *was* registered and is now deregistered
# false -> it was already out of rotation (no-op, logged as a warning)

# put it back
curl -s -X POST http://localhost:8080/actuator/microsphere/service/registration
```

The `GET` payload keys (in order) are: `application-name`, `registration`, `port`, `status`
(read from `ServiceRegistry.getStatus(...)`), `running`, `enabled`, `phase`, `order`,
`managementRegistration`, `config`. Missing values are simply absent or `null`; reflective lookups
that fail return `null` instead of throwing.

Two consequences of the implementation worth knowing:

- `POST` **returns the previous state, not the outcome**: `registration` answers `false` when it
  really registers (it was not running) and `true` when it was already registered and does nothing;
  `deregistration` answers `true` when it really deregisters and `false` when nothing was registered.
  Read `running` from the `GET` if you need the current truth.
- The running flag lives in a `protected static` field of `AbstractServiceRegistrationEndpoint`,
  i.e. it is **shared JVM-wide** between the registration and deregistration endpoints. In
  multi-application-per-JVM setups the two endpoints agree with each other, not with each context.

## Simple / In-Memory Registry Stack

**Use when** you need a running `Registration`/`ServiceRegistry` without any external server:
samples, contract tests, local development, or a fallback when no registry client is on the
classpath.

```yaml
spring:
  application:
    name: test-service
server:
  port: 8080

microsphere:
  spring:
    cloud:
      service-registry:
        auto-registration:
          simple:
            enabled: true     # opt-in — this is the switch
```

```java
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

`SimpleAutoServiceRegistrationAutoConfiguration` then provides:

| Bean | Type | Notes |
|---|---|---|
| `registration` | `DefaultRegistration` (`extends DefaultServiceInstance implements Registration`) | `instanceId = host + ":" + port`, host = first non-loopback IP from `InetUtils`, port = `server.port`, serviceId = `spring.application.name` (fallback `application`) |
| `serviceRegistry` | `InMemoryServiceRegistry` | `@ConditionalOnMissingBean`; a `ConcurrentMap` keyed by instance id |
| `SimpleAutoServiceRegistration` | imported | runs the standard `AbstractAutoServiceRegistration` lifecycle against the in-memory registry |

Requirements: `spring.cloud.service-registry.auto-registration.enabled` must not be `false`
(default `true`), and a `Registration` class must be present. `@AutoConfigureBefore` is applied to
Spring Cloud's own `AutoServiceRegistrationAutoConfiguration`, so your provider's registry beans win
if both are present.

Related but distinct: `SimpleServiceRegistry` is the same idea bound to the *config-file* discovery
model (`spring.cloud.discovery.client.simple.instances`), which is also how the union-client example
below seeds instances. Both simple registries store status under the metadata key `_status_`.

## Union Discovery Client

**Use when** several `DiscoveryClient` implementations coexist (Nacos + Eureka + simple/config-file
instances) and you want one flat view that **keeps duplicates per provider** instead of the
first-wins behaviour of Spring Cloud's `CompositeDiscoveryClient`.

```yaml
microsphere:
  spring:
    cloud:
      client:
        discovery:
          mode: union
      multiple-registration:
        enabled: true

spring:
  cloud:
    discovery:
      client:
        simple:
          order: -1
          instances:
            test:
              - instanceId: 1
                serviceId: test
                host: 127.0.0.1
                port: 8080
                metadata:
                  key-1: value-1
```

The `UnionDiscoveryClient` bean is picked up by Spring Cloud's `CompositeDiscoveryClient` and, due
to `Ordered.HIGHEST_PRECEDENCE`, appears **first** in `getDiscoveryClients()` — exactly what the
module's own integration test asserts.

| Method | Behaviour |
|---|---|
| `description()` | `"Union Discovery Client"` |
| `getInstances(serviceId)` | concatenates every non-empty result from all clients; **duplicates preserved** |
| `getServices()` | union across clients; **deduplicated**, insertion-ordered |
| `getDiscoveryClients()` | all sorted `DiscoveryClient` beans except `CompositeDiscoveryClient` and itself (lazily resolved, then cached) |

## `ReactiveDiscoveryClientAdapter`

**Use when** the only discovery implementation available is reactive (typical for Kubernetes or
Consul reactive stacks) but third-party code — or your own — still calls the blocking
`DiscoveryClient` API.

No switch is needed: when `ReactiveDiscoveryClient`, `spring.cloud.discovery.enabled` and
`spring.cloud.discovery.reactive.enabled` are all in play **and** blocking discovery is still
enabled (`spring.cloud.discovery.blocking.enabled`, default `true`), a
`ReactiveDiscoveryClientAdapter` bean is created around the available `ReactiveDiscoveryClient`.

```java
@Autowired
private DiscoveryClient discoveryClient; // may be the adapter

List<ServiceInstance> instances = discoveryClient.getInstances("order-service");
```

Blocking semantics: the `Flux` is collected with `block()`, except on non-blocking threads
(`Schedulers.isInNonBlockingThread()`), where it switches to `toFuture().get()` so Reactor's
blocking detector does not throw. `description()`, `probe()` and `getOrder()` are delegated
verbatim.

Companion utility: `DiscoveryUtils` converts `SimpleDiscoveryProperties` ⇄
`SimpleReactiveDiscoveryProperties`, so config-file instances stay visible to both APIs.

## Web Endpoint Metadata

**Use when** consumers of your service need to know *which* URLs an instance actually serves
(gateway/traffic tooling, API discovery, contract tests).

`WebMvcServiceRegistryAutoConfiguration` (servlet) and `WebFluxServiceRegistryAutoConfiguration`
(reactive) listen for `WebEndpointMappingsReadyEvent` and write two entries into the
`Registration` metadata:

| Metadata key | Constant | Content |
|---|---|---|
| `web.mappings` | `InstanceConstants.WEB_MAPPINGS_METADATA_NAME` | URL-encoded JSON array of `WebEndpointMapping` |
| `web.context-path` | `InstanceConstants.WEB_CONTEXT_PATH_METADATA_NAME` | `server.servlet.context-path` (WebMvc) or `""` (WebFlux) |

Built-in `org.springframework.*` filter/servlet mappings and Actuator endpoints
(any pattern under `management.endpoints.web.base-path`, default `/actuator`) are **excluded**.

```java
// on the consumer side, from any ServiceInstance
Collection<WebEndpointMapping> mappings = ServiceInstanceUtils.getWebEndpointMappings(instance);
String baseUri = ServiceInstanceUtils.getUriString(instance); // http(s)://host:port, 443/80 fallback
```

`ServiceInstanceUtils` also offers `getUri(ServiceInstance)` (cheaper than
`DefaultServiceInstance.getUri()`), `getMetadata/setMetadata/removeMetadata`, and
`setProperties(source, target)` — which in 0.2.x copies the **`secure`** flag (the 1.x line copied
the URI; Boot 3's `DefaultServiceInstance` derives the URI from `secure`).

## Feature Catalogue

**Use when** you want `/actuator/features` to report which Microsphere features and optional
classes are actually present in the running application, instead of grepping the classpath.

```yaml
spring:
  cloud:
    features:
      enabled: true            # default; the endpoint is enabled by the module defaults too

management:
  endpoints:
    web:
      exposure:
        include: health,info,features

microsphere:
  spring:
    cloud:
      features:
        abstract:
          rest: org.springframework.web.client.RestOperations
          redis: org.springframework.data.redis.connection.RedisConnectionFactory,
                 org.springframework.data.redis.core.RedisTemplate,
                 org.springframework.data.redis.core.StringRedisTemplate
        named:
          web:
            WebClient: org.springframework.web.reactive.function.client.WebClient
          rest:
            RestTemplate: org.springframework.web.client.RestTemplate
```

Rules:

- One `HasFeatures` singleton is registered **per module key**, named `<module>.features`
  (e.g. `microsphere-spring-cloud-commons.features`).
- **Abstract** features: if a listed class has Spring bean types, it is expanded into one **named**
  feature per bean type; otherwise the class itself is recorded as an abstract feature. Classes that
  are not on the classpath make `FeaturesEndpoint` report
  `"The following features are absent: …"`.
- **Named** features are qualified as `<module>:<FeatureName>` — the snippet above yields
  `web:WebClient` and `rest:RestTemplate`.
- Unknown/missing classes are logged at `WARN` with the exact property name and skipped — the
  application still starts.
- A default catalogue ships in `META-INF/config/default/features.yaml` and already documents the
  0.2.x `commons` and `openfeign` modules (e.g. `UnionDiscoveryClient`,
  `ReactiveDiscoveryClientAdapter`, `MultipleRegistration`, `MultipleServiceRegistry`,
  `MultipleAutoServiceRegistration`, `EventPublishingRegistrationAspect`, `SimpleServiceRegistry`,
  `SimpleAutoServiceRegistration`, `InMemoryServiceRegistry`, `ServiceRegistrationEndpoint`,
  `ServiceDeregistrationEndpoint`, `SpecificationBeanPostProcessor`, `SpecificationCustomizer`,
  `TomcatDynamicConfigurationListener`).
- Guard conditions: `spring.cloud.features.enabled` (default `true`) **and** the presence of
  `org.springframework.cloud.client.actuator.FeaturesEndpoint` **and** that endpoint being
  available. (The Javadoc of `ConditionalOnFeaturesAvailable` mentions
  `microsphere.spring.cloud.features.enabled`; the key actually bound is `spring.cloud.features.enabled`.)

## `SpecificationCustomizer`

**Use when** you need to observe or alter the `NamedContextFactory.Specification` beans that define
per-client contexts for Feign and LoadBalancer — this is exactly how the OpenFeign module injects
its auto-refresh capability.

```java
@Component
public class AuditingSpecificationCustomizer implements SpecificationCustomizer {

    private static final Logger log = LoggerFactory.getLogger(AuditingSpecificationCustomizer.class);

    @Override
    public void customize(NamedContextFactory.Specification specification, String beanName) {
        log.info("named context '{}' -> specification '{}' with {} configuration class(es)",
                beanName, specification.getName(), specification.getConfiguration().length);
    }
}
```

`SpecificationAutoConfiguration` installs a `SpecificationBeanPostProcessor` (only when a
`Specification` bean exists, which requires `spring-cloud-context`'s `NamedContextFactory`) that
runs every sorted `SpecificationCustomizer` after initialization. The OpenFeign module registers one
such customizer for you — see [openfeign.md](./openfeign.md).

## Fault Tolerance

### Dynamic Embedded Tomcat

**Use when** you need to raise thread pools, connection limits or header sizes on a running node
instead of rolling a restart.

Enabled by default (`microsphere.spring.cloud.fault-tolerance.tomcat.enabled=true`) for servlet
applications using embedded Tomcat, with `spring-cloud-context` present. On
`WebServerInitializedEvent` a `TomcatDynamicConfigurationListener` is attached; on each
`EnvironmentChangeEvent` it re-reads `server.*` properties and applies the changed, positive,
convertible values:

| Property | Applies to | Tomcat setting |
|---|---|---|
| `server.tomcat.threads.min-spare` | `AbstractProtocol` | core (min spare) threads |
| `server.tomcat.threads.max` | `AbstractProtocol` | max threads |
| `server.tomcat.accept-count` | `AbstractProtocol` | network backlog |
| `server.tomcat.connection-timeout` | `AbstractProtocol` | connection timeout (ms) |
| `server.tomcat.max-connections` | `AbstractProtocol` | max connections |
| `server.max-http-request-header-size` | `AbstractHttp11Protocol` | max HTTP header size (bytes) |
| `server.tomcat.max-swallow-size` | `AbstractHttp11Protocol` | max swallow size (bytes) |
| `server.tomcat.max-http-form-post-size` | `Connector` | max POST size (bytes) |

```yaml
management:
  endpoint:
    refresh:
      enabled: true      # the module's default properties set this to false
  endpoints:
    web:
      exposure:
        include: health,info,refresh,env
```

```bash
curl -s -X POST http://localhost:8080/actuator/refresh \
     -H 'Content-Type: application/json' \
     -d '[{"server.tomcat.threads.max":"400","server.tomcat.max-connections":"16384"}]'
```

Not refreshable: `server.port`, any `server.ssl.*`, and the context path — those still need a
restart. If no `ConfigurationPropertiesRebinder` bean is present, the listener re-binds just the
changed `server` keys itself.

### Weighted Round-Robin Primitives

**Use when** you implement your own load balancer and want the same smooth weighted round-robin and
warm-up ramp semantics the Microsphere stack uses. These are **library building blocks**: nothing in
0.2.x auto-configures them, and the two properties below are read by downstream balancers, not by
this module.

```java
WeightedRoundRobin a = new WeightedRoundRobin("10.0.0.1:8080");
a.setWeight(3);                       // resetting the weight also resets the accumulator
WeightedRoundRobin b = new WeightedRoundRobin("10.0.0.2:8080");
b.setWeight(1);

int total = a.getWeight() + b.getWeight();          // 4
long currentA = a.increaseCurrent();                // +3
a.sel(total);                                       // -1  -> the smooth weighted round-robin step
                                                     // the candidate with the highest current value wins

// a freshly started node gets a quadratic ramp during warm-up
long warmupMillis = TimeUnit.MINUTES.toMillis(10);
long uptime = System.currentTimeMillis() - startTime;
int effectiveWeight = LoadBalancerUtils.calculateWarmupWeight(uptime, warmupMillis, 100); // clamped to [1, weight]
```

| Property | Type | Default | Meaning |
|---|---|---|---|
| `microsphere.spring.cloud.fault-tolerance.weight` | int | `100` | nominal weight of this instance |
| `microsphere.spring.cloud.fault-tolerance.warmup-time` | long (ms) | `600000` (10 minutes) | warm-up window |
| `microsphere.spring.cloud.fault-tolerance.load-balancer.` | prefix | — | namespace reserved for per-balancer settings |

### `ServiceInstancesChangedEvent`

The module publishes/consumes `io.microsphere.spring.cloud.client.event.ServiceInstancesChangedEvent`
(`getServiceName()`, `getServiceInstances()`, `processed()`/`isProcessed()` for idempotent handling).
The instance list **must not be empty** — constructing the event with an empty list throws.

```java
@EventListener
public void onInstancesChanged(ServiceInstancesChangedEvent event) {
    if (event.isProcessed()) {
        return;
    }
    cache.refresh(event.getServiceName(), event.getServiceInstances());
    event.processed();
}
```

## Conditional Annotations Reference

All are marker annotations (no attributes, so no aliases). Use them on your own
`@Configuration`/`@Bean` to gate on the same conditions the library uses.

| Annotation | Package | Checks |
|---|---|---|
| `ConditionalOnFeaturesEnabled` | `client.condition` | `spring.cloud.features.enabled` (`matchIfMissing=true`) |
| `ConditionalOnFeaturesAvailable` | `client.condition` | the property **and** `FeaturesEndpoint` class **and** the endpoint being available |
| `ConditionalOnBlockingDiscoveryAvailable` | `client.discovery.condition` | `DiscoveryClient` class + `spring.cloud.discovery.enabled` + `spring.cloud.discovery.blocking.enabled` |
| `ConditionalOnReactiveDiscoveryAvailable` | `client.discovery.condition` | `ReactiveDiscoveryClient` class + `spring.cloud.discovery.enabled` + `spring.cloud.discovery.reactive.enabled` |
| `ConditionalOnAutoServiceRegistrationEnabled` | `client.service.registry.condition` | `spring.cloud.service-registry.auto-registration.enabled` (`matchIfMissing=true`) |
| `ConditionalOnAutoServiceRegistrationAvailable` | `client.service.registry.condition` | `Registration` class **and** an existing `Registration` bean **and** the property above |
| `ConditionalOnMultipleRegistrationEnabled` | `client.service.registry.condition` | `microsphere.spring.cloud.multiple-registration.enabled` must be explicitly present (strict opt-in) |
| `ConditionalOnLoadBalancerEnabled` | `loadbalancer.condition` | `spring.cloud.loadbalancer.enabled` (declared for downstream modules; not consumed here) |
| `ConditionalOnUtilEnabled` | `commons.condition` | `spring.cloud.util.enabled` (declared for downstream modules; not consumed here) |

Inherited from microsphere-spring-boot and used here:
`ConditionalOnWebMvcAvailable`, `ConditionalOnWebFluxAvailable`, `ConditionalOnActuatorEndpointPresent`.

## Auto-Configuration Reference

Registered in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`:

| # | Auto-configuration | Activated by | Provides |
|---|---|---|---|
| 1 | `SpecificationAutoConfiguration` | `NamedContextFactory` present; a `Specification` bean | `SpecificationBeanPostProcessor` |
| 2 | `DiscoveryClientAutoConfiguration` | blocking discovery available | nested `UnionConfiguration` → `UnionDiscoveryClient` when mode is `union` |
| 3 | `ReactiveDiscoveryClientAutoConfiguration` | reactive discovery available | nested `BlockingConfiguration` → `ReactiveDiscoveryClientAdapter` |
| 4 | `ServiceRegistryAutoConfiguration` | auto service registration enabled + AspectJ + `Registration` | `EventPublishingRegistrationAspect`; nested `MultipleConfiguration` → the three `@Primary` multiple beans |
| 5 | `WebMvcServiceRegistryAutoConfiguration` | WebMvc + auto service registration available | publishes web mappings/context path into `Registration` metadata |
| 6 | `WebFluxServiceRegistryAutoConfiguration` | WebFlux + auto service registration available | same for reactive applications (no context path, no exclusions) |
| 7 | `SimpleAutoServiceRegistrationAutoConfiguration` | opt-in simple flag | `DefaultRegistration`, `InMemoryServiceRegistry`, `SimpleAutoServiceRegistration` |
| 8 | `ServiceRegistrationEndpointAutoConfiguration` | discovery + auto registration + actuator endpoint present | `ServiceRegistrationEndpoint`, `ServiceDeregistrationEndpoint` |
| 9 | `TomcatFaultToleranceAutoConfiguration` | enabled by default + Tomcat + servlet app | attaches `TomcatDynamicConfigurationListener` on web-server start |
| 10 | `ConfigurationPropertyHasFeaturesAutoConfiguration` | features available | one `HasFeatures` singleton per module from `microsphere.spring.cloud.features.*` |

`WebServiceRegistryAutoConfiguration` is the abstract base of rows 5 and 6 and is deliberately **not**
registered.

## Property Reference

| Property | Type | Default | Effect |
|---|---|---|---|
| `microsphere.spring.cloud.multiple-registration.enabled` | boolean | `false` | turns on the composite registration/registry/auto-registration beans |
| `microsphere.spring.cloud.default-registration.type` | class name | — | declared metadata: intended default `Registration` type (last bean in injection order is what the code uses) |
| `microsphere.spring.cloud.default-service-registry.type` | class name | — | declared metadata: intended default `ServiceRegistry` type |
| `microsphere.spring.cloud.composite-registration.enabled` | boolean | `false` | constant declared, no consumer in 0.2.x (reserved) |
| `microsphere.spring.cloud.client.discovery.mode` | string | — | set to `union` to activate `UnionDiscoveryClient` |
| `microsphere.spring.cloud.service-registry.auto-registration.simple.enabled` | boolean | off (opt-in) | activates the simple/in-memory registration stack |
| `microsphere.spring.cloud.fault-tolerance.tomcat.enabled` | boolean | `true` | dynamic Tomcat settings on `EnvironmentChangeEvent` |
| `microsphere.spring.cloud.fault-tolerance.weight` | int | `100` | nominal weight for downstream balancers |
| `microsphere.spring.cloud.fault-tolerance.warmup-time` | long | `600000` | warm-up window (ms) for the ramp function |
| `microsphere.spring.cloud.features.abstract.<module>` | list of class names | — | abstract features reported per module |
| `microsphere.spring.cloud.features.named.<module>.<FeatureName>` | class name | — | named feature `<module>:<FeatureName>` |
| `spring.cloud.service-registry.auto-registration.enabled` | boolean | `true` | native Spring Cloud switch, gates most of this module |
| `spring.cloud.features.enabled` | boolean | `true` | native Spring Cloud switch for the features catalogue |
| `spring.cloud.loadbalancer.enabled` | boolean | `true` | guarded by `ConditionalOnLoadBalancerEnabled` |
| `spring.cloud.util.enabled` | boolean | `true` | guarded by `ConditionalOnUtilEnabled` |
| `management.endpoint.serviceRegistration.enabled` | boolean | `false` (module default) | enables the registration endpoint |
| `management.endpoint.serviceDeregistration.enabled` | boolean | `false` (module default) | enables the deregistration endpoint |
| `management.endpoints.web.path-mapping.serviceRegistration` | string | `microsphere/service/registration` | endpoint path |
| `management.endpoints.web.path-mapping.serviceDeregistration` | string | `microsphere/service/deregistration` | endpoint path |
| `management.endpoint.features.enabled` | boolean | `true` (module default) | `/actuator/features` |
| `management.endpoint.refresh.enabled` | boolean | `false` (module default) | `/actuator/refresh` — required for the Tomcat feature |
| `management.endpoints.web.base-path` | string | `/actuator` | actuator prefix; mappings below it are excluded from `web.mappings` |

The module's shipped defaults (`META-INF/config/default/endpoints.properties`) additionally set
`management.endpoint.archaius/restart/pause/resume/env.post.enabled=false`; they are lowest-priority
values and any of your own declarations override them.

## Troubleshooting

| Symptom | Cause / Fix |
|---|---|
| `registration` bean is `MultipleRegistration` but only one registry is registered | Other registries have no matching `Registration` type; `special(...)` returned `null` and the iteration skipped them |
| `/actuator/microsphere/service/registration` → 404 | Endpoints are disabled by default **and** must be in `management.endpoints.web.exposure.include` |
| No registration events at all | `aspectjweaver`/AOP not active, or `spring.cloud.service-registry.auto-registration.enabled=false` |
| Tomcat settings unchanged after refresh | `management.endpoint.refresh.enabled` left `false`, value not positive, or the property does not start with `server` |
| `web.mappings` missing | auto service registration unavailable (`Registration` bean absent) — the web feature needs a real or simple registration |
| Duplicate services in `getInstances` | expected with `mode=union`; use the default `CompositeDiscoveryClient` for first-wins behaviour |

## See Also

- [openfeign.md](./openfeign.md) — Feign component hot-swap built on `SpecificationCustomizer`
- [parent.md](./parent.md) · [dependencies.md](./dependencies.md)
- [User Guide index](../README.md)
