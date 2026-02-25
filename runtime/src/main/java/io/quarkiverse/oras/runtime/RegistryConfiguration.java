package io.quarkiverse.oras.runtime;

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
     * An optional boolean to enable secure connection.
     * Defaults to `true`
     *
     * @asciidoclet
     */
    @WithDefault("true")
    boolean secure();

}
