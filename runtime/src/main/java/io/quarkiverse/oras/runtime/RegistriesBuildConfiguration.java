package io.quarkiverse.oras.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithParentName;

@ConfigMapping(prefix = "quarkus.oras.registries")
@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public interface RegistriesBuildConfiguration {

    /**
     * Additional named registries
     */
    @WithParentName
    Map<String, RegistryBuildConfiguration> names();

    default Map<String, RegistryBuildConfiguration> getRegistries() {
        return new HashMap<>(names().entrySet().stream()
                .filter(entry -> entry.getValue().enabled())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

}
