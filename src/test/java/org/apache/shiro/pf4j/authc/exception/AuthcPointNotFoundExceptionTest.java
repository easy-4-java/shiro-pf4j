package org.apache.shiro.pf4j.authc.exception;

import static org.junit.Assert.*;

import org.apache.shiro.authc.AuthenticationException;
import org.junit.Test;

/**
 * Tests for {@link AuthcPointNotFoundException}.
 */
public class AuthcPointNotFoundExceptionTest {

    @Test
    public void shouldCreateWithNoArgs() {
        AuthcPointNotFoundException ex = new AuthcPointNotFoundException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    public void shouldCreateWithMessage() {
        AuthcPointNotFoundException ex = new AuthcPointNotFoundException("point not found");
        assertEquals("point not found", ex.getMessage());
    }

    @Test
    public void shouldCreateWithCause() {
        Throwable cause = new RuntimeException("root");
        AuthcPointNotFoundException ex = new AuthcPointNotFoundException(cause);
        assertEquals(cause, ex.getCause());
    }

    @Test
    public void shouldCreateWithMessageAndCause() {
        Throwable cause = new RuntimeException("root");
        AuthcPointNotFoundException ex = new AuthcPointNotFoundException("msg", cause);
        assertEquals("msg", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    public void shouldBeAuthenticationException() {
        AuthcPointNotFoundException ex = new AuthcPointNotFoundException();
        assertTrue(ex instanceof AuthenticationException);
    }
}
