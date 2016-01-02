package ch.puzzle.jee.userauth.validation.boundary;

import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolationException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class EntityValidatorTest {

    private EntityValidator validator;

    @Before
    public void init() {
        validator = new EntityValidator();
    }

    @Test
    public void shouldWriteValidationErrorForEachMissingValue() throws Exception {
        // when
        try {
            validator.validate(new DummyPerson("Linus", null, null));
        } catch (ConstraintViolationException e) {

            // then
            assertThat(e.getConstraintViolations().size(), is(2));
        }
    }

    @Test
    public void shouldWriteValidationErrorForHibernateConstraints() throws Exception {
        // when
        try {
            validator.validate(new DummyPerson("Linus", "Torvalds", "malformed-email-address"));
        } catch (ConstraintViolationException e) {

            // then
            assertThat(e.getConstraintViolations().size(), is(1));
        }
    }
}