package io.quarkiverse.oras.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.oras.runtime.OCILayoutsConfiguration;
import io.quarkiverse.oras.runtime.RegistriesConfiguration;
import io.quarkus.test.QuarkusUnitTest;

public class EmptyConfigurationTest {

    @Inject
    RegistriesConfiguration registriesConfiguration;

    @Inject
    OCILayoutsConfiguration ociLayoutsConfiguration;

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @Test
    public void shouldHaveEmptyRegistry() {
        assertTrue(registriesConfiguration.names().isEmpty(), "No registries should be configured");
        assertEquals(".", ociLayoutsConfiguration.path());
    }

}
