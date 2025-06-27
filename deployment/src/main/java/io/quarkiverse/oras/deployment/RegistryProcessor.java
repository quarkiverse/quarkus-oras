package io.quarkiverse.oras.deployment;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import jakarta.inject.Singleton;

import org.jboss.jandex.DotName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkiverse.oras.runtime.OrasRegistry;
import io.quarkiverse.oras.runtime.Registries;
import io.quarkiverse.oras.runtime.RegistriesBuildConfiguration;
import io.quarkiverse.oras.runtime.RegistriesConfiguration;
import io.quarkiverse.oras.runtime.RegistriesRecorder;
import io.quarkiverse.oras.runtime.RegistryBuildConfiguration;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.IsNormal;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.DevServicesResultBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedPackageBuildItem;
import io.quarkus.deployment.dev.devservices.DevServicesConfig;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;
import io.quarkus.maven.dependency.ArtifactCoords;
import io.quarkus.maven.dependency.Dependency;
import io.quarkus.paths.PathFilter;
import land.oras.OrasModel;
import land.oras.Registry;

class RegistryProcessor {

    private static final String FEATURE = "oras-registry";

    /**
     * Logger for the {@link RegistryProcessor}.
     */
    private static final Logger LOG = LoggerFactory.getLogger(RegistryProcessor.class);

    // ZSTD is used by ORAS for compression
    private static final ArtifactCoords ZSTD_ARTIFACT = Dependency.of("com.github.luben", "zstd-jni", "*");
    private static final PathFilter ZSTD_RESOURCE_FILTER = PathFilter.forIncludes(List.of(
            "linux/**",
            "win/**",
            "freebsd/**",
            "darwin/**"));

    private static final ArtifactCoords ORAS_ARTIFACT = Dependency.of("land.oras", "oras-java-sdk", "*");

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    IndexDependencyBuildItem indexZstd() {
        return new IndexDependencyBuildItem(ZSTD_ARTIFACT.getGroupId(), ZSTD_ARTIFACT.getArtifactId());
    }

    @BuildStep
    IndexDependencyBuildItem indexOras() {
        return new IndexDependencyBuildItem(ORAS_ARTIFACT.getGroupId(), ORAS_ARTIFACT.getArtifactId());
    }

    @Record(ExecutionTime.RUNTIME_INIT)
    @BuildStep
    void produce(
            RegistriesConfiguration registriesConfiguration,
            RegistriesBuildConfiguration registriesBuildConfiguration,
            RegistriesRecorder registriesRecorder,
            BuildProducer<SyntheticBeanBuildItem> syntheticBeanBuildItemBuildProducer,
            BuildProducer<AdditionalBeanBuildItem> additionalBeans) {
        if (registriesBuildConfiguration.getRegistries().isEmpty()) {
            LOG.debug("No registries configured. No beans will be produced.");
            return;
        }
        for (Map.Entry<String, RegistryBuildConfiguration> registry : registriesBuildConfiguration.getRegistries().entrySet()) {
            LOG.debug("Registry '{}' enabled: '{}'", registry.getKey(), registry.getValue().enabled());
        }

        additionalBeans.produce(AdditionalBeanBuildItem.builder()
                .addBeanClass(OrasRegistry.class).build());
        additionalBeans.produce(AdditionalBeanBuildItem.builder()
                .addBeanClasses(Registries.class)
                .setUnremovable()
                .setDefaultScope(DotNames.SINGLETON).build());

        for (Map.Entry<String, RegistryBuildConfiguration> entry : registriesBuildConfiguration.names()
                .entrySet()) {
            var registryName = entry.getKey();
            syntheticBeanBuildItemBuildProducer.produce(createRegistryBuildItem(
                    registryName,
                    Registry.class,
                    // Pass runtime configuration to ensure initialization order
                    registriesRecorder.registrySupplier(registryName, registriesConfiguration)));
        }
    }

    @BuildStep
    void nativeLib(CurateOutcomeBuildItem curateOutcome, BuildProducer<NativeImageResourceBuildItem> nativeBuildItemProducer) {
        var dependencies = curateOutcome.getApplicationModel().getRuntimeDependencies();
        nativeBuildItemProducer.produce(NativeImageResourceBuildItem.ofDependencyResources(
                dependencies, ZSTD_ARTIFACT, ZSTD_RESOURCE_FILTER));
    }

    @BuildStep
    void registerZstdReflection(BuildProducer<ReflectiveClassBuildItem> reflectiveClassProducer) {
        reflectiveClassProducer.produce(ReflectiveClassBuildItem.builder(
                "com.github.luben.zstd.ZstdCompressCtx",
                "com.github.luben.zstd.ZstdDecompressCtx",
                "com.github.luben.zstd.ZstdDictCompress",
                "com.github.luben.zstd.ZstdDictDecompress",
                "com.github.luben.zstd.ZstdDirectBufferCompressingStream$1",
                "com.github.luben.zstd.ZstdDirectBufferCompressingStreamNoFinalizer",
                "com.github.luben.zstd.ZstdDirectBufferDecompressingStream$1",
                "com.github.luben.zstd.ZstdDirectBufferDecompressingStreamNoFinalizer",
                "com.github.luben.zstd.ZstdInputStreamNoFinalizer",
                "com.github.luben.zstd.ZstdOutputStreamNoFinalizer")
                .fields()
                .build());
    }

    @BuildStep
    ReflectiveClassBuildItem registerOrasReflection(CombinedIndexBuildItem combinedIndex) {

        var annotations = combinedIndex.getIndex().getAnnotations(DotName.createSimple(OrasModel.class.getName()));

        LOG.debug("Found " + annotations.size() + " classes annotated with @OrasModel");

        String[] classNames = annotations.stream()
                .map(annotation -> annotation.target().asClass().name().toString())
                .distinct()
                .toArray(String[]::new);

        LOG.debug("Class names: " + String.join(", ", classNames));

        return ReflectiveClassBuildItem
                .builder(classNames)
                .methods()
                .constructors()
                .fields()
                .build();
    }

    @BuildStep
    void runtimePackages(BuildProducer<RuntimeInitializedPackageBuildItem> packagesProducer) {
        packagesProducer.produce(new RuntimeInitializedPackageBuildItem(
                "com.github.luben.zstd"));
    }

    @BuildStep(onlyIfNot = IsNormal.class, onlyIf = DevServicesConfig.Enabled.class)
    public DevServicesResultBuildItem createContainer(RegistriesBuildConfiguration registriesConfig,
            OrasDevServicesConfig devServicesConfig) {
        if (!devServicesConfig.enabled()) {
            LOG.debug("Dev services for ORAS registries are disabled.");
            return null; // No dev service to create
        }

        int basePort = devServicesConfig.basePort();

        // If no registry entries are defined, don't create any containers
        if (registriesConfig.names().isEmpty()) {
            return null; // No containers to create
        }

        // Create a container for each registry entry
        Map<String, String> configOverrides = new java.util.HashMap<>();
        java.util.List<ZotContainer> containers = new java.util.ArrayList<>();
        int portOffset = 0;

        for (Map.Entry<String, RegistryBuildConfiguration> entry : registriesConfig.names().entrySet()) {
            if (!entry.getValue().enabled()) {
                LOG.debug("Skipping disabled registry: {}", entry.getKey());
                continue; // Skip disabled registries
            }
            String registryName = entry.getKey();
            int port = basePort + portOffset++;

            ZotContainer container = new ZotContainer(devServicesConfig.imageName(), port).withReuse(devServicesConfig.reuse());
            container.start();
            containers.add(container);

            // Create configuration overrides for each registry
            configOverrides.put("quarkus.oras.registries." + registryName + ".host", container.getRegistry());
            configOverrides.put("quarkus.oras.registries." + registryName + ".secure", "false");
        }

        // Get the ID of the first container as the primary ID
        String primaryContainerId = containers.get(0).getContainerId();

        // Create a combined close action for all containers as a Closeable
        java.io.Closeable closeAction = () -> {
            for (ZotContainer container : containers) {
                try {
                    container.close();
                } catch (Exception e) {
                    LOG.warn("Failed to stop container: " + e.getMessage(), e);
                }
            }
        };

        return new DevServicesResultBuildItem.RunningDevService(FEATURE,
                primaryContainerId,
                closeAction,
                configOverrides).toBuildItem();
    }

    private static <T> SyntheticBeanBuildItem createRegistryBuildItem(
            String registryName, Class<T> clientClass, Supplier<T> clientSupplier) {
        SyntheticBeanBuildItem.ExtendedBeanConfigurator configurator = SyntheticBeanBuildItem
                .configure(clientClass)
                .scope(Singleton.class)
                .setRuntimeInit()
                .supplier(clientSupplier);

        configurator
                .addQualifier()
                .annotation(DotNames.NAMED)
                .addValue("value", registryName)
                .done();
        configurator.addQualifier().annotation(DotNames.NAMED).addValue("value", registryName).done();
        configurator.addQualifier().annotation(OrasRegistry.class)
                .addValue("value", registryName).done();

        return configurator.done();
    }
}
