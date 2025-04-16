package io.quarkiverse.oras.deployment;

import java.util.Map;
import java.util.function.Supplier;

import jakarta.inject.Named;
import jakarta.inject.Singleton;

import io.quarkiverse.oras.runtime.Registries;
import io.quarkiverse.oras.runtime.RegistriesConfiguration;
import io.quarkiverse.oras.runtime.RegistriesRecorder;
import io.quarkiverse.oras.runtime.RegistryConfiguration;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import land.oras.Registry;

class RegistryProcessor {

    @Record(ExecutionTime.RUNTIME_INIT)
    @BuildStep
    void produce(
            RegistriesConfiguration registriesConfiguration,
            RegistriesRecorder registriesRecorder,
            BuildProducer<SyntheticBeanBuildItem> syntheticBeanBuildItemBuildProducer,
            BuildProducer<AdditionalBeanBuildItem> additionalBeans) {
        if (registriesConfiguration.names().isEmpty()) {
            return;
        }

        additionalBeans.produce(AdditionalBeanBuildItem.builder()
                .addBeanClass(Named.class).build());
        additionalBeans.produce(AdditionalBeanBuildItem.builder()
                .addBeanClasses(Registries.class)
                .setUnremovable()
                .setDefaultScope(DotNames.SINGLETON).build());

        for (Map.Entry<String, RegistryConfiguration> entry : registriesConfiguration.names()
                .entrySet()) {
            var registryName = entry.getKey();
            syntheticBeanBuildItemBuildProducer.produce(createRegistryBuildItem(
                    registryName,
                    Registry.class,
                    // Pass runtime configuration to ensure initialization order
                    registriesRecorder.registrySupplier(registryName)));
        }
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
        configurator.addQualifier().annotation(Named.class)
                .addValue("value", registryName).done();

        return configurator.done();
    }
}
