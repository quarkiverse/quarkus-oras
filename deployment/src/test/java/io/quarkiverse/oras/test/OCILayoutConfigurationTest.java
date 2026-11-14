package io.quarkiverse.oras.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.oras.runtime.OCILayoutsConfiguration;
import io.quarkiverse.oras.runtime.RegistriesConfiguration;
import io.quarkus.test.QuarkusExtensionTest;

class OCILayoutConfigurationTest {

    @Inject
    RegistriesConfiguration registriesConfiguration;

    @Inject
    OCILayoutsConfiguration ociLayoutsConfiguration;

    @RegisterExtension
    static final QuarkusExtensionTest config = new QuarkusExtensionTest()
            .withConfigurationResource("oci-layout.properties");

    @Test
    void shouldHaveConfiguration() {
        assertTrue(registriesConfiguration.names().isEmpty(), "No registries should be configured");
        assertEquals("/var/storage", ociLayoutsConfiguration.path());
    }

}
