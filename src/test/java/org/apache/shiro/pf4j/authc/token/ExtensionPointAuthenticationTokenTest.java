package org.apache.shiro.pf4j.authc.token;

import static org.junit.Assert.*;

import org.apache.shiro.biz.authc.token.DefaultAuthenticationToken;
import org.junit.Test;

/**
 * Tests for {@link ExtensionPointAuthenticationToken}.
 */
public class ExtensionPointAuthenticationTokenTest {

    @Test
    public void shouldCreateEmptyToken() {
        ExtensionPointAuthenticationToken token = new ExtensionPointAuthenticationToken();
        assertNotNull(token);
        assertTrue(token instanceof DefaultAuthenticationToken);
    }

    @Test
    public void shouldCreateWithUsernameCharArrayPasswordRememberMeAndHost() {
        ExtensionPointAuthenticationToken token =
                new ExtensionPointAuthenticationToken("user1", "pass".toCharArray(), true, "host1");
        assertEquals("user1", token.getPrincipal());
        assertNotNull(token.getCredentials());
    }

    @Test
    public void shouldCreateWithUsernameCharArrayPasswordAndRememberMe() {
        ExtensionPointAuthenticationToken token =
                new ExtensionPointAuthenticationToken("user2", "pass".toCharArray(), false);
        assertEquals("user2", token.getPrincipal());
    }

    @Test
    public void shouldCreateWithCharArrayPasswordCaptchaRememberMeAndHost() {
        ExtensionPointAuthenticationToken token =
                new ExtensionPointAuthenticationToken("user3", "pass".toCharArray(), "captcha1", true, "host3");
        assertEquals("user3", token.getPrincipal());
        assertEquals("captcha1", token.getCaptcha());
    }

    @Test
    public void shouldCreateWithCharArrayPasswordCaptchaAndHost() {
        ExtensionPointAuthenticationToken token =
                new ExtensionPointAuthenticationToken("user4", "pass".toCharArray(), "captcha2", "host4");
        assertEquals("user4", token.getPrincipal());
        assertEquals("captcha2", token.getCaptcha());
    }

    @Test
    public void shouldCreateWithStringPasswordRememberMeAndHost() {
        ExtensionPointAuthenticationToken token =
                new ExtensionPointAuthenticationToken("user5", "secret", true, "host5");
        assertEquals("user5", token.getPrincipal());
    }

    @Test
    public void shouldCreateWithStringPasswordAndRememberMe() {
        ExtensionPointAuthenticationToken token =
                new ExtensionPointAuthenticationToken("user6", "secret", false);
        assertEquals("user6", token.getPrincipal());
    }

    @Test
    public void shouldCreateWithStringPasswordCaptchaRememberMeAndHost() {
        ExtensionPointAuthenticationToken token =
                new ExtensionPointAuthenticationToken("user7", "secret", "cap7", true, "host7");
        assertEquals("user7", token.getPrincipal());
        assertEquals("cap7", token.getCaptcha());
    }

    @Test
    public void shouldCreateWithStringPasswordCaptchaAndHost() {
        ExtensionPointAuthenticationToken token =
                new ExtensionPointAuthenticationToken("user8", "secret", "cap8", "host8");
        assertEquals("user8", token.getPrincipal());
        assertEquals("cap8", token.getCaptcha());
    }

    @Test
    public void shouldHaveRememberMeFalseByDefault() {
        ExtensionPointAuthenticationToken token =
                new ExtensionPointAuthenticationToken("u", "p", false);
        assertFalse(token.isRememberMe());
    }

    @Test
    public void shouldHaveRememberMeTrueWhenSet() {
        ExtensionPointAuthenticationToken token =
                new ExtensionPointAuthenticationToken("u", "p", true, "h");
        assertTrue(token.isRememberMe());
    }
}
