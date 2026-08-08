package org.apache.shiro.pf4j.utils;

import static org.junit.Assert.*;

import java.util.Collections;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.pf4j.authc.exception.AuthcPluginNotFoundException;
import org.apache.shiro.pf4j.authc.exception.AuthzPluginNotFoundException;
import org.apache.shiro.pf4j.authc.point.AuthenticatingExtensionPoint;
import org.apache.shiro.pf4j.authz.point.AuthorizationExtensionPoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pf4j.DefaultPluginManager;

/**
 * Tests for {@link ExtensionPointUtils}.
 */
public class ExtensionPointUtilsTest {

    private MockHttpServletRequest mockRequest;
    private ServletResponse mockResponse;

    @Before
    public void setUp() {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new javax.servlet.http.HttpServletResponse() {
            @Override public void addCookie(Cookie cookie) {}
            @Override public boolean containsHeader(String name) { return false; }
            @Override public String encodeURL(String url) { return url; }
            @Override public String encodeRedirectURL(String url) { return url; }
            @Override public String encodeUrl(String url) { return url; }
            @Override public String encodeRedirectUrl(String url) { return url; }
            @Override public void sendError(int sc, String msg) {}
            @Override public void sendError(int sc) {}
            @Override public void sendRedirect(String location) {}
            @Override public void setDateHeader(String name, long date) {}
            @Override public void addDateHeader(String name, long date) {}
            @Override public void setHeader(String name, String value) {}
            @Override public void addHeader(String name, String value) {}
            @Override public void setIntHeader(String name, int value) {}
            @Override public void addIntHeader(String name, int value) {}
            @Override public void setStatus(int sc) {}
            @Override public void setStatus(int sc, String sm) {}
            @Override public int getStatus() { return 200; }
            @Override public String getHeader(String name) { return null; }
            @Override public java.util.Collection<String> getHeaders(String name) { return Collections.emptyList(); }
            @Override public java.util.Collection<String> getHeaderNames() { return Collections.emptyList(); }
            @Override public String getCharacterEncoding() { return "UTF-8"; }
            @Override public String getContentType() { return null; }
            @Override public javax.servlet.ServletOutputStream getOutputStream() { return null; }
            @Override public java.io.PrintWriter getWriter() { return null; }
            @Override public void setCharacterEncoding(String charset) {}
            @Override public void setContentLength(int len) {}
            @Override public void setContentType(String type) {}
            @Override public void setBufferSize(int size) {}
            @Override public int getBufferSize() { return 0; }
            @Override public void flushBuffer() {}
            @Override public void resetBuffer() {}
            @Override public boolean isCommitted() { return false; }
            @Override public void reset() {}
            @Override public void setLocale(java.util.Locale loc) {}
            @Override public java.util.Locale getLocale() { return java.util.Locale.getDefault(); }
        };
        ExtensionPointUtils.AUTHC_THREAD_LOCAL.remove();
        ExtensionPointUtils.AUTHZ_THREAD_LOCAL.remove();
    }

    @After
    public void tearDown() {
        ExtensionPointUtils.AUTHC_THREAD_LOCAL.remove();
        ExtensionPointUtils.AUTHZ_THREAD_LOCAL.remove();
    }

    // --- getPluginId tests ---

    @Test
    public void shouldGetPluginIdFromHeader() {
        mockRequest.setHeader("plugin", "myPlugin");
        String result = ExtensionPointUtils.getPluginId(mockRequest, mockResponse, "plugin");
        assertEquals("myPlugin", result);
    }

    @Test
    public void shouldGetPluginIdFromParameterWhenHeaderEmpty() {
        mockRequest.setParameter("plugin", "paramPlugin");
        String result = ExtensionPointUtils.getPluginId(mockRequest, mockResponse, "plugin");
        assertEquals("paramPlugin", result);
    }

    @Test
    public void shouldReturnNullWhenPluginIdNotFoundAnywhere() {
        String result = ExtensionPointUtils.getPluginId(mockRequest, mockResponse, "plugin");
        assertNull(result);
    }

    // --- getExtensionId tests ---

    @Test
    public void shouldGetExtensionIdFromHeader() {
        mockRequest.setHeader("extension", "ext1");
        String result = ExtensionPointUtils.getExtensionId(mockRequest, mockResponse, "extension");
        assertEquals("ext1", result);
    }

    @Test
    public void shouldGetExtensionIdFromParameterWhenHeaderEmpty() {
        mockRequest.setParameter("extension", "ext2");
        String result = ExtensionPointUtils.getExtensionId(mockRequest, mockResponse, "extension");
        assertEquals("ext2", result);
    }

    @Test
    public void shouldReturnNullWhenExtensionIdNotFoundAnywhere() {
        String result = ExtensionPointUtils.getExtensionId(mockRequest, mockResponse, "extension");
        assertNull(result);
    }

    // --- ThreadLocal caching tests ---

    @Test
    public void shouldReturnCachedAuthcPointFromThreadLocal() {
        AuthenticatingExtensionPoint mock = createMockAuthcPoint();
        ExtensionPointUtils.AUTHC_THREAD_LOCAL.set(mock);
        AuthenticatingExtensionPoint result = ExtensionPointUtils.getAuthcPoint(
                mockRequest, mockResponse, null, "p1", "e1");
        assertSame(mock, result);
    }

    @Test
    public void shouldReturnCachedAuthzPointFromThreadLocal() {
        AuthorizationExtensionPoint mock = createMockAuthzPoint();
        ExtensionPointUtils.AUTHZ_THREAD_LOCAL.set(mock);
        AuthorizationExtensionPoint result = ExtensionPointUtils.getAuthzPoint(
                mockRequest, mockResponse, null, "p1", "e1");
        assertSame(mock, result);
    }

    // --- PluginManager exception tests ---

    @Test(expected = AuthcPluginNotFoundException.class)
    public void shouldThrowWhenPluginNotFoundForAuthc() {
        DefaultPluginManager pm = new DefaultPluginManager();
        ExtensionPointUtils.getAuthcPoint(mockRequest, mockResponse, pm, "missing", "ext");
    }

    @Test(expected = AuthzPluginNotFoundException.class)
    public void shouldThrowWhenPluginNotFoundForAuthz() {
        DefaultPluginManager pm = new DefaultPluginManager();
        ExtensionPointUtils.getAuthzPoint(mockRequest, mockResponse, pm, "missing", "ext");
    }

    // --- Constants tests ---

    @Test
    public void shouldHaveCorrectPluginParamConstant() {
        assertEquals("plugin", ExtensionPointUtils.PLUGINID_PARAM);
    }

    @Test
    public void shouldHaveCorrectExtensionParamConstant() {
        assertEquals("extension", ExtensionPointUtils.EXTENSION_PARAM);
    }

    @Test
    public void shouldHaveThreadLocalFields() {
        assertNotNull(ExtensionPointUtils.AUTHC_THREAD_LOCAL);
        assertNotNull(ExtensionPointUtils.AUTHZ_THREAD_LOCAL);
    }

    // --- Helper methods ---

    private AuthenticatingExtensionPoint createMockAuthcPoint() {
        return new AuthenticatingExtensionPoint() {
            @Override public boolean isEnabled(ServletRequest r, ServletResponse s) { return true; }
            @Override public boolean isPointSubmission(ServletRequest r, ServletResponse s) { return false; }
            @Override public boolean isAccessAllowed(ServletRequest r, ServletResponse s, Object mv) { return true; }
            @Override public boolean isLoginRequest(ServletRequest r, ServletResponse s) { return false; }
            @Override public boolean isLoginSubmission(ServletRequest r, ServletResponse s) { return false; }
            @Override public boolean onAccessDenied(ServletRequest r, ServletResponse s, Object mv) { return true; }
            @Override public AuthenticationToken createToken(ServletRequest r, ServletResponse s) { return null; }
            @Override public boolean onLoginSuccess(AuthenticationToken t, org.apache.shiro.subject.Subject s, ServletRequest r, ServletResponse rp) { return true; }
            @Override public boolean onLoginFailure(AuthenticationToken t, AuthenticationException e, ServletRequest r, ServletResponse rp) { return false; }
            @Override public boolean onAccessSuccess(AuthenticationToken t, org.apache.shiro.subject.Subject s, ServletRequest r, ServletResponse rp) { return true; }
            @Override public boolean onAccessFailure(AuthenticationToken t, Exception e, ServletRequest r, ServletResponse rp) { return false; }
            @Override public void cleanup(ServletRequest r, ServletResponse s, Exception e) {}
            @Override public boolean logout(ServletRequest r, ServletResponse s, org.apache.shiro.subject.Subject sub) { return true; }
        };
    }

    private AuthorizationExtensionPoint createMockAuthzPoint() {
        return new AuthorizationExtensionPoint() {
            @Override public boolean isEnabled(ServletRequest r, ServletResponse s) { return true; }
            @Override public boolean isPointSubmission(ServletRequest r, ServletResponse s) { return false; }
            @Override public boolean isAccessAllowed(ServletRequest r, ServletResponse s, Object mv) { return true; }
            @Override public boolean isLoginRequest(ServletRequest r, ServletResponse s) { return false; }
            @Override public boolean onAccessDenied(ServletRequest r, ServletResponse s, Object mv) { return true; }
        };
    }
}
