package io.quarkiverse.oras.runtime;

import java.util.Optional;

import io.smallrye.config.WithDefault;

public interface RegistryConfiguration {
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

    /**
     * An optional boolean to enable the registry client.
     * Defaults to `true`
     *
     * @asciidoclet
     */
    @WithDefault("true")
    boolean enabled();

}
