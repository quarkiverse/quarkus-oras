# Quarkus ORAS Extension Skill

Quarkus ORAS integrates the ORAS Java SDK to push and pull OCI artifacts from container registries. It provides CDI injection of named `Registry` and `OCILayout` instances, configured via `application.properties`.

## Configuration

Declare each registry under a logical name. The name is used as the CDI qualifier.

```properties
# Secure registry with credentials
quarkus.oras.registries.production.host=registry.example.com
quarkus.oras.registries.production.username=myuser
quarkus.oras.registries.production.password=mypassword

# Insecure local registry (HTTP)
quarkus.oras.registries.local.host=localhost:5000
quarkus.oras.registries.local.secure=false

# Registry backed by Quarkus DevServices (auto-started Zot container)
quarkus.oras.registries.dev.devservice=true

# Mark a registry as the default (built with Registry.defaults())
quarkus.oras.registries.primary.defaults=true

# OCI layout storage path (local filesystem)
quarkus.oras.layouts.path=target/oci-storage
```

## Injecting Registries

Use `@OrasRegistry("name")` to inject a configured registry. The name must match a key declared in `application.properties`.

```java
import io.quarkiverse.oras.runtime.OrasRegistry;
import land.oras.Registry;

@ApplicationScoped
public class MyService {

    @OrasRegistry("production")
    Registry production;

    @OrasRegistry("local")
    Registry local;

    public void pull(String ref) {
        production.getManifest(ContainerRef.parse(ref));
    }
}
```

## Injecting OCI Layouts

Use `OCILayouts` to access local OCI layout storage. Call `getLayout(name)` with the sub-path under `quarkus.oras.layouts.path`.

```java
import io.quarkiverse.oras.runtime.OCILayouts;
import land.oras.OCILayout;

@Inject
OCILayouts ociLayouts;

OCILayout layout = ociLayouts.getLayout("myartifact");
layout.pushBlob(LayoutRef.of(layout), data);
byte[] blob = layout.getBlob(LayoutRef.of(layout).withDigest(digest));
```

## Common Operations

```java
// Pull an index from a remote registry
Index index = registry.getIndex(ContainerRef.parse("registry.example.com/myimage:latest"));

// Pull a manifest
Manifest manifest = registry.getManifest(ContainerRef.parse("registry.example.com/myimage:latest"));

// List tags
List<String> tags = registry.getTags(ContainerRef.parse("registry.example.com/myimage"));

// List repositories (DevServices or private registries)
List<String> repos = registry.getRepositories();

// Push a raw blob
registry.pushBlob(ContainerRef.parse("registry.example.com/myartifact"), data);
```

## Archive Compression

Use `ArchiveUtils` to prepare directory artifacts before pushing:

```java
import land.oras.utils.ArchiveUtils;
import land.oras.LocalPath;
import land.oras.Const;

LocalPath tar = ArchiveUtils.tar(LocalPath.of("src/"));
LocalPath gzip = ArchiveUtils.compress(tar, Const.DEFAULT_BLOB_DIR_MEDIA_TYPE);  // gzip
LocalPath zstd = ArchiveUtils.compress(tar, Const.BLOB_DIR_ZSTD_MEDIA_TYPE);    // zstd
LocalPath zip  = ArchiveUtils.compress(tar, Const.ZIP_MEDIA_TYPE);               // zip
```

## Testing

In tests, use DevServices to get an auto-started registry — no external setup required:

```properties
# src/test/resources/application.properties
quarkus.oras.registries.test.devservice=true
```

```java
@QuarkusTest
class MyTest {

    @OrasRegistry("test")
    Registry registry;

    @Test
    void pushAndPull() {
        registry.pushBlob(ContainerRef.parse("localhost/myartifact"), new byte[0]);
    }
}
```

For `@QuarkusUnitTest` in deployment tests, place a `.properties` file in `src/test/resources/` and load it with `.overrideConfigKey(...)` or `withConfigurationResource("my-config.properties")`.

## Common Pitfalls

- **Missing `devservice=false` in production**: By default, if no `host` is set and `devservice` is not explicitly configured, no registry is created. Always set either `host` or `devservice=true`.
- **Insecure registries**: Set `secure=false` explicitly for HTTP registries — the default is HTTPS.
- **Qualifier name mismatch**: The `@OrasRegistry("name")` value must exactly match the key in `quarkus.oras.registries.<name>`. A typo causes an unsatisfied injection at startup.
- **Native image**: ZSTD compression is supported natively. No additional configuration is needed; the extension registers the required native libraries automatically.
