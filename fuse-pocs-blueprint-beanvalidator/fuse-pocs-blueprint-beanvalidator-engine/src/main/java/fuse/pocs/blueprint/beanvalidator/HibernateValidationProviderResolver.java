package fuse.pocs.blueprint.beanvalidator;

import org.hibernate.validator.HibernateValidator;

import javax.validation.ValidationProviderResolver;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * Provided since Camel 2.13.0 (CAMEL-7168).
 */
public class HibernateValidationProviderResolver implements ValidationProviderResolver {

    @Override
    public List getValidationProviders() {
        return singletonList(new HibernateValidator());
    }

}