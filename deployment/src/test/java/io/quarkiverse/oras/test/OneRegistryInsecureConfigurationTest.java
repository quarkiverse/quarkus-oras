package io.quarkiverse.oras.test;

import static org.junit.jupiter.api.Assertions.assertFalse;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.oras.runtime.RegistriesConfiguration;
import io.quarkiverse.oras.runtime.RegistryConfiguration;
import io.quarkus.test.QuarkusUnitTest;

public class OneRegistryInsecureConfigurationTest {

    @Inject
    RegistriesConfiguration registriesConfiguration;

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withConfigurationResource("one-insecure-registry.properties");

    @Test
    public void shouldHaveOneInsecureRegistry() {
        RegistryConfiguration configuration = registriesConfiguration.names().get("foobar");
        assertFalse(configuration.secure(), "Registry should be insecure");
        assertFalse(configuration.username().isPresent(), "Registry should not have username");
        assertFalse(configuration.password().isPresent(), "Registry should not have password");
    }

}
