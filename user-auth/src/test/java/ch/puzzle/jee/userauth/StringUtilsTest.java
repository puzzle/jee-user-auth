package ch.puzzle.jee.userauth;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringUtilsTest {

    @Test
    public void shouldReturnNotContainsOnlyDigitsIfInputNull() {
        //given
        String strToValidate = null;
        //when
        boolean containsOnlyDigits = StringUtils.containsOnlyDigits(strToValidate);
        //then
        assertFalse(containsOnlyDigits);
    }

    @Test
    public void shouldReturnNotContainsOnlyDigitsIfInputEmpty() {
        //given
        String strToValidate = "";
        //when
        boolean containsOnlyDigits = StringUtils.containsOnlyDigits(strToValidate);
        //then
        assertFalse(containsOnlyDigits);
    }

    @Test
    public void shouldReturnNotContainsOnlyDigits() {
        //given
        String strToValidate = "124521ds568";
        //when
        boolean containsOnlyDigits = StringUtils.containsOnlyDigits(strToValidate);
        //then
        assertFalse(containsOnlyDigits);
    }

    @Test
    public void shouldReturnContainsOnlyDigits() {
        //given
        String strToValidate = "124500568";
        //when
        boolean containsOnlyDigits = StringUtils.containsOnlyDigits(strToValidate);
        //then
        assertTrue(containsOnlyDigits);
    }
}
