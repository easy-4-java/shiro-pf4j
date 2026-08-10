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
package org.apache.shiro.pf4j.web.filter.authz;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.pf4j.authz.point.AuthorizationExtensionPoint;
import org.apache.shiro.pf4j.utils.ExtensionPointUtils;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.pf4j.PluginManager;

/**
 * Abstract authorization filter that delegates all authorization logic to a PF4J
 * {@link AuthorizationExtensionPoint} resolved at runtime. This filter extends Shiro's
 * {@link AuthorizationFilter} and forwards each lifecycle method (enabled check, access
 * control, login request detection, access denial handling) to the extension point.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see AuthorizationExtensionPoint
 * @see ExtensionPointUtils#getAuthzPoint
 * @see DefaultExtensionPointAuthorizationFilter
 */
public abstract class AbstracExtensionPointAuthorizationFilter extends AuthorizationFilter  {

	/** The PF4J plugin manager used to resolve extension points. */
	private PluginManager pluginManager;

	/**
	 * Delegates to the extension point to determine if this filter is enabled for the request.
	 *
	 * @param request  the incoming servlet request
	 * @param response the outgoing servlet response
	 * @return {@code true} if the filter should process the request
	 * @throws ServletException if a servlet error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected boolean isEnabled(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		return getAuthzPoint(request, response).isEnabled(request, response);
	}

	/**
	 * Delegates to the extension point to determine if the request is allowed access.
	 *
	 * @param request     the incoming servlet request
	 * @param response    the outgoing servlet response
	 * @param mappedValue the filter-specific config value
	 * @return {@code true} if access is allowed
	 * @throws Exception if an error occurs during processing
	 */
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {
		return getAuthzPoint(request, response).isAccessAllowed(request, response, mappedValue);
	}

	/**
	 * Delegates to the extension point to determine if the request is a login request.
	 *
	 * @param request  the incoming servlet request
	 * @param response the outgoing servlet response
	 * @return {@code true} if the request is a login request
	 */
	@Override
	protected boolean isLoginRequest(ServletRequest request, ServletResponse response) {
		return getAuthzPoint(request, response).isLoginRequest(request, response);
	}

	/**
	 * If the request is a point submission, delegates to the extension point; otherwise
	 * falls back to the parent implementation.
	 *
	 * @param request     the incoming servlet request
	 * @param response    the outgoing servlet response
	 * @param mappedValue the filter-specific config value
	 * @return {@code true} if the request should continue processing
	 * @throws Exception if an error occurs during processing
	 */
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {
		AuthorizationExtensionPoint authzPoint = getAuthzPoint(request, response);
		if(authzPoint.isPointSubmission(request, response)) {
			return authzPoint.onAccessDenied(request, response, mappedValue);
		}
		return super.onAccessDenied(request, response, mappedValue);
	}

	/**
	 * Resolves the {@link AuthorizationExtensionPoint} for the current request.
	 * First checks the thread-local cache, then delegates to {@link ExtensionPointUtils}.
	 *
	 * @param request  the incoming servlet request
	 * @param response the outgoing servlet response
	 * @return the resolved authorization extension point
	 */
	protected AuthorizationExtensionPoint getAuthzPoint(ServletRequest request, ServletResponse response) {
		AuthorizationExtensionPoint authzPoint = ExtensionPointUtils.AUTHZ_THREAD_LOCAL.get();
		if(authzPoint != null) {
			return authzPoint;
		}
		String pluginId =  this.getPluginId(request, response);
		String extensionId = this.getExtensionId(request, response);
		return ExtensionPointUtils.getAuthzPoint(request, response, getPluginManager(), pluginId, extensionId);
	}

	/**
	 * Resolves the plugin identifier from the current request.
	 *
	 * @param request  the incoming servlet request
	 * @param response the outgoing servlet response
	 * @return the plugin identifier
	 */
	protected abstract String getPluginId(ServletRequest request, ServletResponse response);

	/**
	 * Resolves the extension point identifier from the current request.
	 *
	 * @param request  the incoming servlet request
	 * @param response the outgoing servlet response
	 * @return the extension point identifier
	 */
	protected abstract String getExtensionId(ServletRequest request, ServletResponse response);

	/**
	 * Returns the PF4J plugin manager.
	 *
	 * @return the plugin manager instance
	 */
	public PluginManager getPluginManager() {
		return pluginManager;
	}

	/**
	 * Sets the PF4J plugin manager.
	 *
	 * @param pluginManager the plugin manager instance to use
	 */
	public void setPluginManager(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}

}
