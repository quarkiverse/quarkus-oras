package io.quarkiverse.oras.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.oras.runtime.RegistriesConfiguration;
import io.quarkiverse.oras.runtime.RegistryConfiguration;
import io.quarkus.test.QuarkusUnitTest;

class OneDefaultRegistryConfigurationTest {

    @Inject
    RegistriesConfiguration registriesConfiguration;

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withConfigurationResource("one-default-registry.properties");

    @Test
    void shouldHaveOneDefaultRegistry() {
        RegistryConfiguration configuration = registriesConfiguration.names().get("foobar");
        assertTrue(configuration.secure(), "Registry should be secure");
        assertFalse(configuration.host().isPresent(), "Registry should not have username");
        assertFalse(configuration.username().isPresent(), "Registry should not have username");
        assertFalse(configuration.password().isPresent(), "Registry should not have password");
        assertTrue(configuration.defaults().orElseThrow(), "Registry should have defaults enabled");
    }

}
