package fuse.pocs.blueprint.beanvalidator;

import org.junit.Assert;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class DefaultValidatorBeanTest extends Assert {

    ValidatorBean defaultValidatorBean = new DefaultValidatorBean();

    Event validEvent = new Event("foo@gmail.com", "bar@gmail.com", "baz@gmail.com");

    Event invalidEvent = new Event("foo@gmail.com", "foo@gmail.com", "bar@gmail.com", "baz@gmail.com");

    @Test
    public void shouldNotReturnValidationErrors() {
        // When
        Set<ConstraintViolation<Event>> errors = defaultValidatorBean.validateEvent(validEvent);

        // Then
        assertEquals(0, errors.size());
    }

    @Test
    public void shouldReturnValidationError() {
        // When
        Set<ConstraintViolation<Event>> errors = defaultValidatorBean.validateEvent(invalidEvent);

        // Then
        assertEquals(1, errors.size());
    }

    @Test
    public void shouldInterpolateMessage() {
        // When
        Set<ConstraintViolation<Event>> errors = defaultValidatorBean.validateEvent(invalidEvent);

        // Then
        assertEquals("Interpolated message!", errors.iterator().next().getMessage());
    }

}