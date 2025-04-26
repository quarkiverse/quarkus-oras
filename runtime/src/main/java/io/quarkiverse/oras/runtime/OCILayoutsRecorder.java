
package io.quarkiverse.oras.runtime;

import java.util.function.Supplier;

import io.quarkus.runtime.annotations.Recorder;
import land.oras.OCILayout;

@Recorder
public class OCILayoutsRecorder {

    public Supplier<OCILayout> registrySupplier(
            String name) {
        return () -> OCILayouts.fromName(name);
    }
}
