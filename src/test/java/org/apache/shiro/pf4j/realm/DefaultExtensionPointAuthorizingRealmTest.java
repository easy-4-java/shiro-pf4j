package org.apache.shiro.pf4j.realm;

import static org.junit.Assert.*;

import org.apache.shiro.pf4j.authc.token.ExtensionPointAuthenticationToken;
import org.apache.shiro.pf4j.utils.ExtensionPointUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link DefaultExtensionPointAuthorizingRealm}.
 */
public class DefaultExtensionPointAuthorizingRealmTest {

    private DefaultExtensionPointAuthorizingRealm realm;

    @Before
    public void setUp() {
        realm = new DefaultExtensionPointAuthorizingRealm();
        ExtensionPointUtils.AUTHC_THREAD_LOCAL.remove();
        ExtensionPointUtils.AUTHZ_THREAD_LOCAL.remove();
    }

    @After
    public void tearDown() {
        ExtensionPointUtils.AUTHC_THREAD_LOCAL.remove();
        ExtensionPointUtils.AUTHZ_THREAD_LOCAL.remove();
    }

    @Test
    public void shouldSupportExtensionPointAuthenticationToken() {
        Class<?> tokenClass = realm.getAuthenticationTokenClass();
        assertEquals(ExtensionPointAuthenticationToken.class, tokenClass);
    }

    @Test
    public void shouldHaveDefaultExtensionParamName() {
        assertEquals("extension", realm.getExtensionParamName());
    }

    @Test
    public void shouldHaveDefaultPluginParamName() {
        assertEquals("plugin", realm.getPluginParamName());
    }

    @Test
    public void shouldSetAndGetExtensionParamName() {
        realm.setExtensionParamName("customExt");
        assertEquals("customExt", realm.getExtensionParamName());
    }

    @Test
    public void shouldSetAndGetPluginParamName() {
        realm.setPluginParamName("customPlugin");
        assertEquals("customPlugin", realm.getPluginParamName());
    }

    @Test
    public void shouldSetAndGetPluginManager() {
        realm.setPluginManager(null);
        assertNull(realm.getPluginManager());
    }

    @Test
    public void shouldSetAndGetRealmsListeners() {
        assertNull(realm.getRealmsListeners());
        java.util.List<org.apache.shiro.biz.realm.AuthorizingRealmListener> listeners =
                new java.util.ArrayList<>();
        realm.setRealmsListeners(listeners);
        assertSame(listeners, realm.getRealmsListeners());
    }

    @Test
    public void shouldReturnNullAuthorizationInfoForNullPrincipals() {
        org.apache.shiro.authz.AuthorizationInfo info = realm.doGetAuthorizationInfo(null);
        assertNull(info);
    }

    @Test
    public void shouldReturnNullAuthorizationInfoForEmptyPrincipals() {
        org.apache.shiro.authz.AuthorizationInfo info =
                realm.doGetAuthorizationInfo(new org.apache.shiro.subject.SimplePrincipalCollection());
        assertNull(info);
    }
}
