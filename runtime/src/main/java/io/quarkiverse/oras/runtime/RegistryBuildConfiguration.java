package io.quarkiverse.oras.runtime;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;

@ConfigGroup
public interface RegistryBuildConfiguration {

    /**
     * An optional boolean to enable the registry client.
     * Defaults to `true`
     *
     * @asciidoclet
     */
    @WithDefault("true")
    boolean enabled();

    /**
     * An optional boolean to enable devserice for the registry.
     * Defaults to `true`
     *
     * @asciidoclet
     */
    @WithDefault("true")
    boolean devservice();
}