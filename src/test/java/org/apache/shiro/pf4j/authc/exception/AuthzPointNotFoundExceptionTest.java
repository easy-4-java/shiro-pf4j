package org.apache.shiro.pf4j.authc.exception;

import static org.junit.Assert.*;

import org.apache.shiro.authc.AuthenticationException;
import org.junit.Test;

/**
 * Tests for {@link AuthzPointNotFoundException}.
 */
public class AuthzPointNotFoundExceptionTest {

    @Test
    public void shouldCreateWithNoArgs() {
        AuthzPointNotFoundException ex = new AuthzPointNotFoundException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    public void shouldCreateWithMessage() {
        AuthzPointNotFoundException ex = new AuthzPointNotFoundException("point missing");
        assertEquals("point missing", ex.getMessage());
    }

    @Test
    public void shouldCreateWithCause() {
        Throwable cause = new RuntimeException("root");
        AuthzPointNotFoundException ex = new AuthzPointNotFoundException(cause);
        assertEquals(cause, ex.getCause());
    }

    @Test
    public void shouldCreateWithMessageAndCause() {
        Throwable cause = new RuntimeException("root");
        AuthzPointNotFoundException ex = new AuthzPointNotFoundException("msg", cause);
        assertEquals("msg", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    public void shouldBeAuthenticationException() {
        AuthzPointNotFoundException ex = new AuthzPointNotFoundException();
        assertTrue(ex instanceof AuthenticationException);
    }
}
