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
     * Registries that accept a long living Bearer token. Conflict with username / password flow
     *
     * @asciidoclet
     */
    Optional<String> token();

    /**
     * Current default parallelism (only to download layers)
     *
     * @return Current parallelism
     */
    Optional<Integer> parallelism();

    /**
     * The policy file to use. If not set policies are not loaded
     * Typically found on /etc/containers/policy.json
     *
     * @return The policy file
     */
    Optional<Path> policy();

    /**
     * The max page for listing tags and referrers (avoid infinite loop)
     *
     * @return The max pages
     */
    Optional<Integer> maxPages();

    /**
     * The number of max retry for fail requests
     *
     * @return May retry
     */
    Optional<Integer> maxRetries();

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
