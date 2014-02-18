package fuse.pocs.blueprint.beanvalidator.event;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoDuplicatesValidator.class)
@Documented
public @interface NoDuplicates {

    String message() default "{fuse.pocs.blueprint.beanvalidator.noduplicates}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String value();
}