package ch.puzzle.jee.userauth;

import org.junit.BeforeClass;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class BaseValidationTest {

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public Validator getValidator() {
        return validator;
    }

    protected <T> void assertContainsUniqueConstraintViolationForField(Set<ConstraintViolation<T>> constraintViolations, String fieldName) {
        assertContainsUniqueConstraintViolation(constraintViolations);
        ConstraintViolation<T> constraintViolation = constraintViolations.iterator().next();
        assertThat(constraintViolation.getPropertyPath().toString(), is(fieldName));
    }

    protected <T> void assertContainsUniqueConstraintViolationWithMessage(Set<ConstraintViolation<T>> constraintViolations, String message) {
        assertContainsUniqueConstraintViolation(constraintViolations);
        ConstraintViolation<T> constraintViolation = constraintViolations.iterator().next();
        assertThat(constraintViolation.getMessage(), is(message));
    }

    protected <T> void assertContainsNoConstraintViolation(Set<ConstraintViolation<T>> constraintViolations) {
        assertNotNull(constraintViolations);
        assertTrue(constraintViolations.isEmpty());
    }

    private <T> void assertContainsUniqueConstraintViolation(Set<ConstraintViolation<T>> constraintViolations) {
        assertNotNull(constraintViolations);
        assertThat(constraintViolations.size(), is(1));
    }

}
