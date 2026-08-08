package org.apache.shiro.pf4j.authc.exception;

import static org.junit.Assert.*;

import org.apache.shiro.authc.AuthenticationException;
import org.junit.Test;

/**
 * Tests for {@link AuthzPluginNotFoundException}.
 */
public class AuthzPluginNotFoundExceptionTest {

    @Test
    public void shouldCreateWithNoArgs() {
        AuthzPluginNotFoundException ex = new AuthzPluginNotFoundException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    public void shouldCreateWithMessage() {
        AuthzPluginNotFoundException ex = new AuthzPluginNotFoundException("plugin missing");
        assertEquals("plugin missing", ex.getMessage());
    }

    @Test
    public void shouldCreateWithCause() {
        Throwable cause = new RuntimeException("root");
        AuthzPluginNotFoundException ex = new AuthzPluginNotFoundException(cause);
        assertEquals(cause, ex.getCause());
    }

    @Test
    public void shouldCreateWithMessageAndCause() {
        Throwable cause = new RuntimeException("root");
        AuthzPluginNotFoundException ex = new AuthzPluginNotFoundException("msg", cause);
        assertEquals("msg", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    public void shouldBeAuthenticationException() {
        AuthzPluginNotFoundException ex = new AuthzPluginNotFoundException();
        assertTrue(ex instanceof AuthenticationException);
    }
}
