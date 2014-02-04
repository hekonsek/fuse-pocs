package fuse.pocs.blueprint.beanvalidator;

import org.junit.Assert;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class DefaultValidatorBeanTest extends Assert {

    // Fixtures

    ValidatorBean validatorBean = new DefaultValidatorBean();

    Event eventWithoutDuplicatedAttendees = new Event("foo@gmail.com", "bar@gmail.com", "baz@gmail.com");

    Event eventWithDuplicatedAttendees = new Event("foo@gmail.com", "foo@gmail.com", "bar@gmail.com", "baz@gmail.com");

    // Tests

    @Test
    public void shouldNotReturnValidationErrors() {
        // When
        Set<ConstraintViolation<Event>> errors = validatorBean.validateEvent(eventWithoutDuplicatedAttendees);

        // Then
        assertEquals(0, errors.size());
    }

    @Test
    public void shouldReturnValidationError() {
        // When
        Set<ConstraintViolation<Event>> errors = validatorBean.validateEvent(eventWithDuplicatedAttendees);

        // Then
        assertEquals(1, errors.size());
    }

    @Test
    public void shouldInterpolateMessage() {
        // When
        Set<ConstraintViolation<Event>> errors = validatorBean.validateEvent(eventWithDuplicatedAttendees);

        // Then
        assertEquals("Interpolated message!", errors.iterator().next().getMessage());
    }

}