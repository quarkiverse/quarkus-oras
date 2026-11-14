package io.quarkiverse.oras.test;

import static org.junit.jupiter.api.Assertions.assertFalse;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.oras.runtime.RegistriesConfiguration;
import io.quarkiverse.oras.runtime.RegistryConfiguration;
import io.quarkus.test.QuarkusExtensionTest;

class OneRegistryInsecureConfigurationTest {

    @Inject
    RegistriesConfiguration registriesConfiguration;

    @RegisterExtension
    static final QuarkusExtensionTest config = new QuarkusExtensionTest()
            .withConfigurationResource("one-insecure-registry.properties");

    @Test
    void shouldHaveOneInsecureRegistry() {
        RegistryConfiguration configuration = registriesConfiguration.names().get("foobar");
        assertFalse(configuration.secure(), "Registry should be insecure");
        assertFalse(configuration.username().isPresent(), "Registry should not have username");
        assertFalse(configuration.password().isPresent(), "Registry should not have password");
    }

}
