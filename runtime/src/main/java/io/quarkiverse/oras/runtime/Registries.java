package io.quarkiverse.oras.runtime;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jakarta.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.Arc;
import land.oras.Registry;

@Singleton
public class Registries {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(Registries.class);

    /**
     * Runtime configuration
     */
    private final RegistriesConfiguration registriesConfiguration;

    /**
     * Runtime configuration
     */
    private final RegistriesBuildConfiguration registriesBuildConfiguration;

    /**
     * Registries
     */
    private final ConcurrentMap<String, Registry> registries = new ConcurrentHashMap<>();

    public Registries(
            RegistriesBuildConfiguration registriesBuildConfiguration, RegistriesConfiguration registriesConfiguration) {
        this.registriesBuildConfiguration = registriesBuildConfiguration;
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

        // Create a builder with defaults
        Registry.Builder builder = Registry.Builder.builder();

        // Default to true
        if (configuration.defaults().orElse(true)) {
            builder.defaults();
        }

        // Set the host if present
        if (configuration.host().isPresent()) {
            builder.withRegistry(configuration.host().get());
        }

        // Set username/password if present
        if (configuration.username().isPresent() && configuration.password().isPresent()) {
            builder.defaults(configuration.username().get(), configuration.password().get());
        }
        if (!configuration.secure()) {
            builder.insecure();
        }

        return builder.build();
    }

    private RegistryConfiguration getConfiguration(String registryName) {

        LOG.debug("Getting registry configuration for '{}'", registryName);
        registriesBuildConfiguration.getRegistries().forEach((key, value) -> {
            LOG.debug("Registry '{}' enabled '{}'", key, value.enabled());
        });

        if (!registriesBuildConfiguration.getRegistries().containsKey(registryName)) {
            throw new IllegalArgumentException("No Registry named '" + registryName + "' exists");
        }

        RegistryConfiguration configuration = registriesConfiguration.names().get(registryName);

        if (configuration == null) {
            throw new IllegalArgumentException("No registry configuration found for '" + registryName + "'");
        }
        return configuration;
    }
}
