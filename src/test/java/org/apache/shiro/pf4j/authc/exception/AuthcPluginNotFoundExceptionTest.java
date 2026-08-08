package org.apache.shiro.pf4j.authc.exception;

import static org.junit.Assert.*;

import org.apache.shiro.authc.AuthenticationException;
import org.junit.Test;

/**
 * Tests for {@link AuthcPluginNotFoundException}.
 */
public class AuthcPluginNotFoundExceptionTest {

    @Test
    public void shouldCreateWithNoArgs() {
        AuthcPluginNotFoundException ex = new AuthcPluginNotFoundException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    public void shouldCreateWithMessage() {
        AuthcPluginNotFoundException ex = new AuthcPluginNotFoundException("plugin not found");
        assertEquals("plugin not found", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    public void shouldCreateWithCause() {
        Throwable cause = new RuntimeException("root cause");
        AuthcPluginNotFoundException ex = new AuthcPluginNotFoundException(cause);
        assertEquals(cause, ex.getCause());
    }

    @Test
    public void shouldCreateWithMessageAndCause() {
        Throwable cause = new RuntimeException("root cause");
        AuthcPluginNotFoundException ex = new AuthcPluginNotFoundException("msg", cause);
        assertEquals("msg", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    public void shouldBeAuthenticationException() {
        AuthcPluginNotFoundException ex = new AuthcPluginNotFoundException();
        assertTrue(ex instanceof AuthenticationException);
    }
}
