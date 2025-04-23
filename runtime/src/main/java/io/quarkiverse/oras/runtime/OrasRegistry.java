package io.quarkiverse.oras.runtime;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Qualifier;

/**
 * Qualifier used to specify which minio client will be injected.
 * kinda of mandatory cause of BeanInfo#124
 */
@Target({ METHOD, FIELD, PARAMETER, TYPE })
@Retention(RUNTIME)
@Documented
@Qualifier
public @interface OrasRegistry {

    String value();

    /**
     * Literal for the {@link OrasRegistry} annotation.
     */
    class OrasRegistryLiteral extends AnnotationLiteral<OrasRegistry> implements OrasRegistry {

        private final String name;

        public OrasRegistryLiteral(String name) {
            this.name = name;
        }

        @Override
        public String value() {
            return name;
        }
    }

}
