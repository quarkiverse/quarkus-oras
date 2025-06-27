package io.quarkiverse.oras.deployment;

import java.nio.file.Files;
import java.nio.file.Path;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

public class ZotContainer extends GenericContainer<ZotContainer> {

    private final int containerPort;

    public ZotContainer(String imageName, int containerPort) {
        super(imageName);
        this.containerPort = containerPort;
        addExposedPort(containerPort);
        // Wait for the catalog endpoint to be available (should return 200 without auth)
        setWaitStrategy(Wait.forHttp("/v2/_catalog").forPort(containerPort).forStatusCode(200));

        try {
            // Zot config file
            Path configFile = Files.createTempFile("zot-config", ".json");
            String configJson = """
                          {
                            "storage": { "rootDirectory": "/var/lib/registry" },
                            "http": {
                              "address": "0.0.0.0",
                              "port": %s
                            },
                            "extensions": {
                              "search": { "enable": true }
                            }
                          }
                    """.formatted(containerPort);

            Files.writeString(configFile, configJson);

            // Copy it into the container
            withCopyFileToContainer(
                    MountableFile.forHostPath(configFile.toAbsolutePath().toString()), "/etc/zot/config.json");

        } catch (Exception e) {
            throw new RuntimeException("Failed to create Zot config", e);
        }
    }

    /**
     * Get the registry URL
     *
     * @return The registry URL
     */
    public String getRegistry() {
        return getHost() + ":" + getMappedPort(containerPort);
    }
}
