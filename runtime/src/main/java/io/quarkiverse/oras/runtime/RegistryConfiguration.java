package io.quarkiverse.oras.runtime;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "quarkus.oras.registries")
@ConfigGroup
public interface RegistryConfiguration {

    /**
     * The registry default host
     *
     * @asciidoclet
     */
    String host();

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
