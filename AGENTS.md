# AGENTS.md

This file provides guidance to AI coding agents when working with code in this repository.

## Project Overview

Quarkus ORAS is a Quarkiverse extension that integrates the [ORAS Java SDK](https://github.com/oras-project/oras-java) into Quarkus applications. Its primary purpose is to make ORAS work in GraalVM/Mandrel native executables while providing auto-configuration and CDI-based dependency injection for OCI registries.

## Build & Test Commands

```bash
# Compile and run all tests (JVM mode)
mvn verify

# Build and test native image
mvn verify -Dnative

# Run a specific test class
mvn -pl deployment test -Dtest=EmptyConfigurationTest

# Run a specific test method
mvn -pl deployment test -Dtest=EmptyConfigurationTest#shouldHaveEmptyRegistry

# Run integration tests only
mvn -pl integration-tests verify
```

## Module Structure

| Module | Artifact | Role |
|---|---|---|
| `runtime/` | `quarkus-oras` | CDI beans, configuration mappings, recorders |
| `deployment/` | `quarkus-oras-deployment` | Build-time processors, native image config, dev services |
| `integration-tests/` | — | Test JAX-RS application exercising the extension |
| `docs/` | — | AsciiDoc documentation |

## Architecture

### Quarkus Extension Split

The hard constraint in any Quarkus extension: **deployment code runs at build time; runtime code runs at application start.** Classes in `deployment/` must never be on the runtime classpath.

- **`RegistryProcessor`** (`deployment/`) — the main `@BuildStep` class. It synthesizes a CDI bean per configured registry name, registers ZSTD native library JNI/resource access, marks ORAS model classes for reflection, and provisions the Zot dev service container.
- **`OCILayoutProcessor`** (`deployment/`) — analogous processor for local OCI layout beans.
- **`RegistriesRecorder`** / **`OCILayoutsRecorder`** (`runtime/`) — `@Recorder`-annotated classes that bridge build-time decisions to runtime initialization.

### Runtime Beans

- **`Registries`** — `@ApplicationScoped` singleton that lazily creates and caches `Registry` instances in a `ConcurrentHashMap`, keyed by registry name.
- **`OCILayouts`** — same pattern for local OCI layout instances.
- **`@OrasRegistry("name")`** — custom CDI qualifier used to inject a specific named `Registry` bean.

### Dev Services

`ZotContainer` (in `deployment/`) is a TestContainers-based container that automatically starts a [Zot](https://zotregistry.dev/) OCI registry during development and testing when dev services are enabled.

## Configuration

All configuration is under the `quarkus.oras.*` prefix.

**Per-registry runtime config** (`quarkus.oras.registries.<name>.*`):
- `host`, `username`, `password`, `token`, `secure` (default: `true`), `parallelism`, `ca-file`, `ca-bundle`

**OCI layouts** (`quarkus.oras.layouts.*`):
- `path` (default: `"."`)

**Dev services** (`quarkus.oras.devservices.*`):
- `image-name`, `base-port` (default: `5000`), `reuse`

## Testing Patterns

- **Deployment unit tests** (`deployment/src/test/`) use `QuarkusUnitTest` and load configuration from `.properties` files in `deployment/src/test/resources/`.
- **Integration tests** (`integration-tests/`) use `@QuarkusTest` + REST-assured against a running `OrasResource` JAX-RS endpoint.
- Dev services automatically start a Zot registry container when tests run, so no manual registry setup is required.

## Native Image Notes

- **ZSTD**: Native binaries and JNI access are configured in `RegistryProcessor`. The `com.github.luben.zstd` package is set for runtime initialization (not build time).
- **Reflection**: ORAS model classes annotated with `@OrasModel` are registered for reflection automatically by `RegistryProcessor`.
- When adding new ORAS SDK classes that need reflection in native mode, add the `@OrasModel` annotation or register them explicitly in `RegistryProcessor`.
