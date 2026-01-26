package io.quarkiverse.oras.runtime;

import java.util.function.Supplier;

import io.quarkus.runtime.annotations.Recorder;
import land.oras.Registry;

@Recorder
public class RegistriesRecorder {

    public Supplier<Registry> registrySupplier(String registryName) {
        return () -> Registries.fromName(registryName);
    }
}
