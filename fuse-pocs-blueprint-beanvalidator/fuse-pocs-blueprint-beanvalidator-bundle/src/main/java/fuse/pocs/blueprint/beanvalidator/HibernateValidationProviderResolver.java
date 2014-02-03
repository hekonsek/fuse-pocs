package fuse.pocs.blueprint.beanvalidator;

import org.hibernate.validator.HibernateValidator;

import javax.validation.ValidationProviderResolver;
import java.util.Arrays;
import java.util.List;

public class HibernateValidationProviderResolver implements ValidationProviderResolver {

    @Override
    public List getValidationProviders() {
        return Arrays.asList(new HibernateValidator());
    }

}