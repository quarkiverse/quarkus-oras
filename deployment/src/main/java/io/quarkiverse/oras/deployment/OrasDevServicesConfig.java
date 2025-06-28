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
     * The container image name to use, for the Zot registry
     */
    @WithDefault("ghcr.io/project-zot/zot-linux-amd64:v2.1.5")
    String imageName();

    /**
     * The base port for registry containers (default: 5000).
     * For multiple registries, ports will be assigned sequentially starting from this base.
     */
    @WithDefault("5000")
    int basePort();

    /**
     * Indicates if the registry container should be shared or reused.
     * Defaults to false.
     */
    @WithDefault("false")
    boolean reuse();

}
