package fuse.pocs.blueprint.beanvalidator;

import javax.validation.ConstraintViolation;
import java.util.Set;

public interface ValidatorBean {

    Set<ConstraintViolation<Object>> validateEvent(Object event);

}