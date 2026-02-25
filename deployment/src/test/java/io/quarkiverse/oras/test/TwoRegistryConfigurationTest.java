package io.quarkiverse.oras.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.oras.runtime.RegistriesConfiguration;
import io.quarkiverse.oras.runtime.RegistryConfiguration;
import io.quarkus.test.QuarkusUnitTest;

class TwoRegistryConfigurationTest {

    @Inject
    RegistriesConfiguration registriesConfiguration;

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withConfigurationResource("two-secure-registry.properties");

    @Test
    void shouldHaveOneInsecureRegistry() {
        Map<String, RegistryConfiguration> registries = registriesConfiguration.names();
        assertEquals(2, registries.size(), "There should be 2 registries configured");
        assertTrue(registries.containsKey("foobar"), "Registry foobar should be configured");
        assertTrue(registries.containsKey("foobar2"), "Registry baz should be configured");

        RegistryConfiguration configuration1 = registriesConfiguration.names().get("foobar");
        RegistryConfiguration configuration2 = registriesConfiguration.names().get("foobar2");

        // Check registry 1
        assertTrue(configuration1.secure(), "Registry1 should be secure");
        assertTrue(configuration1.username().isPresent(), "Registry1 should have username");
        assertTrue(configuration1.password().isPresent(), "Registry1 should have password");
        assertEquals("toto", configuration1.username().get(), "Registry1 should have username set");
        assertEquals("titi", configuration1.password().get(), "Registry1 should have password set");
        assertEquals("localhost:5000", configuration1.host().orElseThrow(), "Registry2 should have default host set");

        // Check registry 2
        assertTrue(configuration2.secure(), "Registry2 should secure");
        assertTrue(configuration2.username().isPresent(), "Registry2 should have username");
        assertTrue(configuration2.password().isPresent(), "Registry2 should have password");
        assertEquals("toto2", configuration2.username().get(), "Registry2 should have username set");
        assertEquals("titi2", configuration2.password().get(), "Registry2 should have password set");
        assertEquals("localhost:5001", configuration2.host().orElseThrow(), "Registry2 should have default host set");

    }

}
