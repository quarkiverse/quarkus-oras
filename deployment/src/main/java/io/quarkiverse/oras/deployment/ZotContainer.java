package io.quarkiverse.oras.deployment;

import java.nio.file.Files;
import java.nio.file.Path;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.MountableFile;

import io.quarkus.deployment.builditem.Startable;

/**
 * A Testcontainers container for a Zot registry for Dev Services.
 */
public class ZotContainer extends GenericContainer<ZotContainer> implements Startable {

    private final int containerPort;
    private final String registryName;

    public ZotContainer(String registryName, String imageName, int containerPort) {
        super(imageName);
        this.registryName = registryName;
        this.containerPort = containerPort;
        try {
            // Zot config file
            Path configFile = Files.createTempFile("zot-config", ".json");
            String configJson = """
                          {
                            "storage": { "rootDirectory": "/var/lib/registry" },
                            "http": {
                              "address": "0.0.0.0",
                              "port": 5000
                            },
                            "extensions": {
                              "search": { "enable": true }
                            }
                          }
                    """;

            Files.writeString(configFile, configJson);

            // Copy it into the container
            withCopyFileToContainer(
                    MountableFile.forHostPath(configFile.toAbsolutePath().toString()), "/etc/zot/config.json");

        } catch (Exception e) {
            throw new RuntimeException("Failed to create Zot config", e);
        }
    }

    /**
     * Get the registry name
     *
     * @return The registry name
     */
    public String getRegistryName() {
        return registryName;
    }

    @Override
    protected void configure() {
        super.configure();
        addFixedExposedPort(containerPort, 5000);
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public String getConnectionInfo() {
        return getHost() + ":" + containerPort;
    }
}
