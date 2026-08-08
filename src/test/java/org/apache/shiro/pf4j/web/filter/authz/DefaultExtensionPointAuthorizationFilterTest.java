package org.apache.shiro.pf4j.web.filter.authz;

import static org.junit.Assert.*;

import org.apache.shiro.pf4j.utils.ExtensionPointUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link DefaultExtensionPointAuthorizationFilter}.
 */
public class DefaultExtensionPointAuthorizationFilterTest {

    private DefaultExtensionPointAuthorizationFilter filter;

    @Before
    public void setUp() {
        filter = new DefaultExtensionPointAuthorizationFilter();
        ExtensionPointUtils.AUTHZ_THREAD_LOCAL.remove();
    }

    @After
    public void tearDown() {
        ExtensionPointUtils.AUTHZ_THREAD_LOCAL.remove();
    }

    @Test
    public void shouldHaveDefaultExtensionParamName() {
        assertEquals("extension", filter.getExtensionParamName());
    }

    @Test
    public void shouldHaveDefaultPluginParamName() {
        assertEquals("plugin", filter.getPluginParamName());
    }

    @Test
    public void shouldSetAndGetExtensionParamName() {
        filter.setExtensionParamName("authzExt");
        assertEquals("authzExt", filter.getExtensionParamName());
    }

    @Test
    public void shouldSetAndGetPluginParamName() {
        filter.setPluginParamName("authzPlugin");
        assertEquals("authzPlugin", filter.getPluginParamName());
    }

    @Test
    public void shouldSetAndGetPluginManager() {
        filter.setPluginManager(null);
        assertNull(filter.getPluginManager());
    }
}
