package io.quarkiverse.oras.runtime;

import java.nio.file.Path;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "quarkus.oras.registries")
@ConfigGroup
public interface RegistryConfiguration {

    /**
     * Enable the registry without any specific host or credentials so any FQDN reference (or via alias) can be resolved.
     *
     * @asciidoclet
     */
    @SuppressWarnings("unused")
    Optional<Boolean> defaults();

    /**
     * The registry default host
     *
     * @asciidoclet
     */
    Optional<String> host();

    /**
     * The registry username
     *
     * @asciidoclet
     */
    Optional<String> username();

    /**
     * The registry password
     *
     * @asciidoclet
     */
    Optional<String> password();

    /**
     * Current default parallelism (only to download layers)
     *
     * @return Current parallelism
     */
    Optional<Integer> parallelism();

    /**
     * If using custom CA. provide the CA bundle file (pem encoded)
     * Note it replace any system CA for this registry to only use the ones provided
     *
     * @return The path
     */
    Optional<Path> caFile();

    /**
     * If using custom CA. provide the CA bundle (pem encoded)
     * Note it replace any system CA for this registry to only use the ones provided
     *
     * @return The CA bundle
     */
    Optional<String> caBundle();

    /**
     * An optional boolean to enable secure connection.
     * Defaults to `true`
     *
     * @asciidoclet
     */
    @WithDefault("true")
    boolean secure();

}
