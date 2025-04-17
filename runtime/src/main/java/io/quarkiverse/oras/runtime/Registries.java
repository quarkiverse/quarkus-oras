package io.quarkiverse.oras.runtime;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jakarta.inject.Singleton;

import io.quarkus.arc.Arc;
import land.oras.Registry;

@Singleton
public class Registries {

    /**
     * Build time configuration
     */
    private final RegistriesConfiguration registriesConfiguration;

    /**
     * Registries
     */
    private final ConcurrentMap<String, Registry> registries = new ConcurrentHashMap<>();

    public Registries(
            RegistriesConfiguration registriesConfiguration) {
        this.registriesConfiguration = registriesConfiguration;
    }

    public static Registry fromName(String registryName) {
        return Arc.container().instance(Registries.class).get().getRegistry(registryName);
    }

    public Registry getRegistry(String registryName) {
        return registries.computeIfAbsent(registryName, this::createRegistry);
    }

    public Registry createRegistry(String registryName) {
        RegistryConfiguration configuration = getConfiguration(registryName);

        Registry.Builder builder = Registry.Builder.builder();

        // Set username/password if present
        if (configuration.username().isPresent() && configuration.password().isPresent()) {
            builder.defaults(configuration.username().get(), configuration.password().get());
        }
        // Set host if present
        if (configuration.host().isPresent()) {
            builder.withRegistry(configuration.host().get());
        }
        if (!configuration.secure()) {
            builder.insecure();
        }

        return builder.build();
    }

    private RegistryConfiguration getConfiguration(String registryName) {
        if (!registriesConfiguration.names().containsKey(registryName)) {
            throw new IllegalArgumentException("No Registry named '" + registryName + "' exists");
        }

        RegistryConfiguration configuration = registriesConfiguration.names().get(registryName);

        if (configuration == null) {
            throw new IllegalArgumentException("No registry configuration found for '" + registryName + "'");
        }
        return configuration;
    }
}
