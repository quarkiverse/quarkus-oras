package io.quarkiverse.oras.runtime;

import java.util.function.Supplier;

import org.slf4j.LoggerFactory;

import io.quarkus.runtime.annotations.Recorder;
import land.oras.Registry;

@Recorder
public class RegistriesRecorder {

    public Supplier<Registry> registrySupplier(
            String registryName) {
        LoggerFactory.getLogger(RegistriesConfiguration.class).info("Creating registry supplier for {}", registryName);
        return () -> Registries.fromName(registryName);
    }
}
