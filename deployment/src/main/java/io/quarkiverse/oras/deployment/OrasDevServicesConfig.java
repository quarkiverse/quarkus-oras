package io.quarkiverse.oras.deployment;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

/**
 * DevServices configuration for ORAS registry containers
 */
@ConfigMapping(prefix = "quarkus.oras.devservices")
@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
public interface OrasDevServicesConfig {
    /**
     * If DevServices has been explicitly enabled or disabled. By default,
     * DevServices are enabled if the DevServices extension is enabled.
     */
    @WithDefault("true")
    boolean enabled();

    /**
     * The container image name to use, for the Zot registry
     */
    @WithDefault("ghcr.io/project-zot/zot-linux-amd64:v2.1.3")
    String imageName();

    /**
     * The base port for registry containers (default: 5000).
     * For multiple registries, ports will be assigned sequentially starting from this base.
     */
    @WithDefault("5000")
    int basePort();

    /**
     * Indicates if the registry container should be shared or reused.
     * Defaults to true.
     */
    @WithDefault("true")
    boolean reuse();
}
