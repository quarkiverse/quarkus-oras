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

}
