package io.quarkiverse.oras.runtime;

import java.nio.file.Path;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "quarkus.oras.layouts")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface OCILayoutsConfiguration {

    /**
     * Path to storage
     */
    @WithDefault(".")
    String path();

}
