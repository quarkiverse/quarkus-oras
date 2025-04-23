package io.quarkiverse.oras.runtime;

import java.util.Map;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithParentName;

@ConfigMapping(prefix = "quarkus.oras.registries")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface RegistriesConfiguration {

    /**
     * Additional named registries
     */
    @WithParentName
    Map<String, RegistryConfiguration> names();

}
