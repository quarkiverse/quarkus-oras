package io.quarkiverse.oras.deployment;

import io.quarkiverse.oras.runtime.OCILayouts;
import io.quarkiverse.oras.runtime.OCILayoutsRecorder;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;

public class OCILayoutProcessor {

    @Record(ExecutionTime.RUNTIME_INIT)
    @BuildStep
    void produce(
            OCILayoutsRecorder ociLayoutsRecorder,
            BuildProducer<SyntheticBeanBuildItem> syntheticBeanBuildItemBuildProducer,
            BuildProducer<AdditionalBeanBuildItem> additionalBeans) {

        additionalBeans.produce(AdditionalBeanBuildItem.builder()
                .addBeanClasses(OCILayouts.class)
                .setUnremovable()
                .setDefaultScope(DotNames.SINGLETON).build());
    }

}
