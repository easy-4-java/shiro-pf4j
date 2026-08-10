/*
 * Copyright (c) 2018, Loong Wan (https://github.com/loong10k).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.shiro.pf4j.utils;

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.biz.utils.StringUtils2;
import org.apache.shiro.web.util.WebUtils;
import org.apache.shiro.pf4j.annotation.AuthzMapping;
import org.apache.shiro.pf4j.authc.exception.AuthcPluginNotFoundException;
import org.apache.shiro.pf4j.authc.exception.AuthcPointNotFoundException;
import org.apache.shiro.pf4j.authc.exception.AuthzPluginNotFoundException;
import org.apache.shiro.pf4j.authc.exception.AuthzPointNotFoundException;
import org.apache.shiro.pf4j.authc.point.AuthenticatingExtensionPoint;
import org.apache.shiro.pf4j.authz.point.AuthorizationExtensionPoint;
import org.pf4j.ExtensionPoint;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for resolving PF4J extension points from servlet requests.
 * Provides methods to discover authentication and authorization extension points
 * by plugin ID and extension ID, with thread-local caching for performance.
 * <p>Plugin IDs and extension IDs are resolved from request headers, parameters,
 * or cookies in that priority order.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see AuthenticatingExtensionPoint
 * @see AuthorizationExtensionPoint
 */
public class ExtensionPointUtils {

	private static final Logger LOG = LoggerFactory.getLogger(ExtensionPointUtils.class);

	/** Default request parameter name for the plugin identifier. */
	public static final String PLUGINID_PARAM = "plugin";

	/** Default request parameter name for the extension point identifier. */
	public static final String EXTENSION_PARAM = "extension";

	/** Thread-local cache for authentication extension points. */
	public static ThreadLocal<AuthenticatingExtensionPoint> AUTHC_THREAD_LOCAL = new ThreadLocal<AuthenticatingExtensionPoint>();

	/** Thread-local cache for authorization extension points. */
	public static ThreadLocal<AuthorizationExtensionPoint> AUTHZ_THREAD_LOCAL = new ThreadLocal<AuthorizationExtensionPoint>();

	/**
	 * Resolves an {@link AuthenticatingExtensionPoint} from the given plugin and extension identifiers.
	 * Uses a thread-local cache to avoid repeated lookups. If not cached, looks up the plugin via
	 * the {@link PluginManager}, then searches for an extension annotated with {@link AuthzMapping}
	 * whose ID matches and that implements {@link AuthenticatingExtensionPoint}.
	 *
	 * @param request       the incoming servlet request
	 * @param response      the outgoing servlet response
	 * @param pluginManager the PF4J plugin manager
	 * @param pluginId      the plugin identifier to look up
	 * @param extensionId   the extension point identifier to match
	 * @return the resolved authentication extension point
	 * @throws AuthcPluginNotFoundException if no plugin is found for the given plugin ID
	 * @throws AuthcPointNotFoundException  if no matching authentication extension point is found
	 */
	public static AuthenticatingExtensionPoint getAuthcPoint(ServletRequest request, ServletResponse response,
			PluginManager pluginManager, String pluginId, String extensionId) throws AuthenticationException {
		AuthenticatingExtensionPoint authcPoint = AUTHC_THREAD_LOCAL.get();
		if(authcPoint == null) {
			// Check if the plugin is loaded
			PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
			if(wrapper == null) {
				throw new AuthcPluginNotFoundException(String.format("Pf4j plugin not found whith pluginId [%s]", pluginId));
			}
			// Log plugin info
			if(LOG.isDebugEnabled()) {
				LOG.debug(wrapper.toString());
			}
			// Find extension implementations within the plugin
			List<ExtensionPoint> extensions = pluginManager.getExtensions(ExtensionPoint.class, pluginId);
			for (ExtensionPoint extension : extensions) {
				// Check annotation
				AuthzMapping mapping = extension.getClass().getAnnotation(AuthzMapping.class);
				// Match type and ID
				if(mapping != null && StringUtils2.equals(mapping.id(), extensionId)
						&& extension instanceof AuthenticatingExtensionPoint) {
					authcPoint = (AuthenticatingExtensionPoint) extension;
					AUTHC_THREAD_LOCAL.set(authcPoint);
					break;
				}
			}
			if(authcPoint == null) {
				throw new AuthcPointNotFoundException(String.format("Authc Extension Point not found whith pluginId [%s], extensionId [%s]", pluginId, extensionId));
			}
		}
		return authcPoint;
	}

	/**
	 * Resolves an {@link AuthorizationExtensionPoint} from the given plugin and extension identifiers.
	 * Uses a thread-local cache to avoid repeated lookups. If not cached, looks up the plugin via
	 * the {@link PluginManager}, then searches for an extension annotated with {@link AuthzMapping}
	 * whose ID matches and that implements {@link AuthorizationExtensionPoint}.
	 *
	 * @param request       the incoming servlet request
	 * @param response      the outgoing servlet response
	 * @param pluginManager the PF4J plugin manager
	 * @param pluginId      the plugin identifier to look up
	 * @param extensionId   the extension point identifier to match
	 * @return the resolved authorization extension point
	 * @throws AuthzPluginNotFoundException if no plugin is found for the given plugin ID
	 * @throws AuthzPointNotFoundException  if no matching authorization extension point is found
	 */
	public static AuthorizationExtensionPoint getAuthzPoint(ServletRequest request, ServletResponse response,
			PluginManager pluginManager, String pluginId, String extensionId) throws AuthenticationException {
		AuthorizationExtensionPoint authzPoint = AUTHZ_THREAD_LOCAL.get();
		if(authzPoint == null) {
			// Check if the plugin is loaded
			PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
			if(wrapper == null) {
				throw new AuthzPluginNotFoundException(String.format("Pf4j plugin not found whith pluginId [%s]", pluginId));
			}
			// Log plugin info
			if(LOG.isDebugEnabled()) {
				LOG.debug(wrapper.toString());
			}
			// Find extension implementations within the plugin
			List<ExtensionPoint> extensions = pluginManager.getExtensions(ExtensionPoint.class, pluginId);
			for (ExtensionPoint extension : extensions) {
				// Check annotation
				AuthzMapping mapping = extension.getClass().getAnnotation(AuthzMapping.class);
				// Match type and ID
				if(mapping != null && StringUtils2.equals(mapping.id(), extensionId)
						&& extension instanceof AuthorizationExtensionPoint) {
					authzPoint = (AuthorizationExtensionPoint) extension;
					AUTHZ_THREAD_LOCAL.set(authzPoint);
					break;
				}
			}
			if(authzPoint == null) {
				throw new AuthzPointNotFoundException(String.format("Authz Extension Point not found whith pluginId [%s], extensionId [%s]", pluginId, extensionId));
			}
		}
		return authzPoint;
	}

	/**
	 * Resolves a plugin identifier from the servlet request by checking (in order):
	 * request header, request parameter, and cookies.
	 *
	 * @param request         the incoming servlet request
	 * @param response        the outgoing servlet response
	 * @param pluginParamName the name of the header/parameter/cookie to look up
	 * @return the plugin identifier, or {@code null} if not found
	 */
	public static String getPluginId(ServletRequest request, ServletResponse response , String pluginParamName) {
		HttpServletRequest httpRequest = WebUtils.toHttp(request);
        // Try header first
        String pluginId = httpRequest.getHeader(pluginParamName);
        // Fall back to request parameter
        if (StringUtils2.isEmpty(pluginId)) {
            return httpRequest.getParameter(pluginParamName);
        }
        if (StringUtils2.isEmpty(pluginId)) {
            // Fall back to cookies
            Cookie[] cookies = httpRequest.getCookies();
            if (null == cookies || cookies.length == 0) {
                return null;
            }
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(pluginParamName)) {
                    pluginId = cookie.getValue();
                    break;
                }
            }
        }
        return pluginId;
	}

	/**
	 * Resolves an extension point identifier from the servlet request by checking (in order):
	 * request header, request parameter, and cookies.
	 *
	 * @param request            the incoming servlet request
	 * @param response           the outgoing servlet response
	 * @param extensionParamName the name of the header/parameter/cookie to look up
	 * @return the extension point identifier, or {@code null} if not found
	 */
	public static String getExtensionId(ServletRequest request, ServletResponse response, String extensionParamName) {
		HttpServletRequest httpRequest = WebUtils.toHttp(request);
        // Try header first
        String extensionId = httpRequest.getHeader(extensionParamName);
        // Fall back to request parameter
        if (StringUtils2.isEmpty(extensionId)) {
            return httpRequest.getParameter(extensionParamName);
        }
        if (StringUtils2.isEmpty(extensionId)) {
            // Fall back to cookies
            Cookie[] cookies = httpRequest.getCookies();
            if (null == cookies || cookies.length == 0) {
                return null;
            }
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(extensionParamName)) {
                    extensionId = cookie.getValue();
                    break;
                }
            }
        }
        return extensionId;
	}

}
