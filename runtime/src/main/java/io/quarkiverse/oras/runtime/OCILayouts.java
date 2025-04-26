package io.quarkiverse.oras.runtime;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jakarta.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.Arc;
import land.oras.OCILayout;

@Singleton
public class OCILayouts {

    @ConfigProperty(name = "quarkus.oras.layouts.path")
    String path;

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(OCILayouts.class);

    /**
     * OCI layouts
     */
    private final ConcurrentMap<String, OCILayout> ociLayouts = new ConcurrentHashMap<>();

    public static OCILayout fromName(String name) {
        return Arc.container().instance(OCILayouts.class).get().getLayout(name);
    }

    public OCILayout getLayout(String name) {
        return ociLayouts.computeIfAbsent(name, this::createLayout);
    }

    private OCILayout createLayout(String name) {
        // Ensure the storage exists or create it
        Path storagePath = Path.of(path);
        if (!storagePath.toFile().exists()) {
            if (storagePath.toFile().mkdirs()) {
                LOG.info("Created OCI storage directory: {}", storagePath);
            }
        }
        LOG.debug("Creating OCI layout for {}", name);
        return OCILayout.builder().defaults(Path.of(path).resolve(name)).build();
    }

}
