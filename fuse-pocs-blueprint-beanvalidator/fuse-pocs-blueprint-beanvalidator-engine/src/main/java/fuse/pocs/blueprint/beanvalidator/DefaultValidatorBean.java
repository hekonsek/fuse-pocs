package fuse.pocs.blueprint.beanvalidator;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;

import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class DefaultValidatorBean implements ValidatorBean {

    @Override
    public Set<ConstraintViolation<Object>> validateEvent(Object event) {
        Configuration<?> configuration =
                Validation.byDefaultProvider().providerResolver(new HibernateValidationProviderResolver()).configure();
        PlatformResourceBundleLocator platformResourceBundleLocator = new PlatformResourceBundleLocator("fuse.pocs.blueprint.beanvalidator.messages.ValidationMessages");
        ResourceBundleMessageInterpolator resourceBundleMessageInterpolator = new ResourceBundleMessageInterpolator(platformResourceBundleLocator);
        ValidatorFactory factory = configuration
                .messageInterpolator(resourceBundleMessageInterpolator)
                .buildValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(event);
    }

}