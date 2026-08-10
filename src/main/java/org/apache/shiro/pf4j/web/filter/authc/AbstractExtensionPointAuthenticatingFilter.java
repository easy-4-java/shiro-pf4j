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
package org.apache.shiro.pf4j.web.filter.authc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.biz.web.filter.authc.AbstractAuthenticatingFilter;
import org.apache.shiro.pf4j.authc.point.AuthenticatingExtensionPoint;
import org.apache.shiro.pf4j.utils.ExtensionPointUtils;
import org.apache.shiro.subject.Subject;
import org.pf4j.PluginManager;

/**
 * Abstract authentication filter that delegates all authentication logic to a PF4J
 * {@link AuthenticatingExtensionPoint} resolved at runtime. This filter extends Shiro's
 * {@link AbstractAuthenticatingFilter} and forwards each lifecycle method (login detection,
 * access control, token creation, success/failure callbacks, cleanup) to the extension point.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see AuthenticatingExtensionPoint
 * @see ExtensionPointUtils#getAuthcPoint
 * @see DefaultExtensionPointAuthenticatingFilter
 */
public abstract class AbstractExtensionPointAuthenticatingFilter extends AbstractAuthenticatingFilter {

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
		return getAuthcPoint(request, response).isEnabled(request, response);
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
		return getAuthcPoint(request, response).isLoginRequest(request, response);
	}

	/**
	 * Delegates to the extension point to determine if the request is allowed access.
	 *
	 * @param request     the incoming servlet request
	 * @param response    the outgoing servlet response
	 * @param mappedValue the filter-specific config value
	 * @return {@code true} if access is allowed
	 */
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		return getAuthcPoint(request, response).isAccessAllowed(request, response, mappedValue);
	}

	/**
	 * Delegates to the extension point to determine if the request is a login submission.
	 *
	 * @param request  the incoming servlet request
	 * @param response the outgoing servlet response
	 * @return {@code true} if the request is a login submission
	 */
	@Override
	protected boolean isLoginSubmission(ServletRequest request, ServletResponse response) {
		return getAuthcPoint(request, response).isLoginSubmission(request, response);
	}

	/**
	 * If the request is a login submission, delegates to the extension point; otherwise
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
		if(isLoginSubmission(request, response)) {
			return getAuthcPoint(request, response).onAccessDenied(request, response, mappedValue);
		}
		return super.onAccessDenied(request, response, mappedValue);
	}

	/**
	 * Delegates token creation to the extension point.
	 *
	 * @param request  the incoming servlet request
	 * @param response the outgoing servlet response
	 * @return the authentication token
	 */
	@Override
	protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
		return getAuthcPoint(request, response).createToken(request, response);
	}

	/**
	 * Delegates cleanup to the extension point; falls back to the parent implementation on error.
	 *
	 * @param request  the incoming servlet request
	 * @param response the outgoing servlet response
	 * @param existing any exception that occurred during processing
	 * @throws ServletException if a servlet error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected void cleanup(ServletRequest request, ServletResponse response, Exception existing)
			throws ServletException, IOException {
		try {
			getAuthcPoint(request, response).cleanup(request, response, existing);
		} catch (Exception e) {
			super.cleanup(request, response, existing);
		}
	}

	/**
	 * Delegates login success handling to the extension point.
	 *
	 * @param token    the authentication token
	 * @param subject  the authenticated subject
	 * @param request  the incoming servlet request
	 * @param response the outgoing servlet response
	 * @return {@code true} if processing should continue
	 * @throws Exception if an error occurs during processing
	 */
	@Override
	protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
			ServletResponse response) throws Exception{
		return getAuthcPoint(request, response).onLoginSuccess(token, subject, request, response);
	}

	/**
	 * Delegates login failure handling to the extension point.
	 *
	 * @param token    the authentication token
	 * @param e        the authentication exception
	 * @param request  the incoming servlet request
	 * @param response the outgoing servlet response
	 * @return {@code true} if processing should continue
	 */
	@Override
	protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request,
			ServletResponse response) {
		return getAuthcPoint(request, response).onLoginFailure(token, e, request, response);
	}

	/**
	 * Delegates access success handling to the extension point.
	 *
	 * @param token    the authentication token
	 * @param subject  the authenticated subject
	 * @param request  the incoming servlet request
	 * @param response the outgoing servlet response
	 * @return {@code true} if processing should continue
	 */
	@Override
	protected boolean onAccessSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
			ServletResponse response) {
		return getAuthcPoint(request, response).onAccessSuccess(token, subject, request, response);
	}

	/**
	 * Delegates access failure handling to the extension point.
	 *
	 * @param token    the authentication token
	 * @param e        the authentication exception
	 * @param request  the incoming servlet request
	 * @param response the outgoing servlet response
	 * @return {@code true} if processing should continue
	 */
	@Override
	protected boolean onAccessFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request,
			ServletResponse response) {
		return getAuthcPoint(request, response).onAccessFailure(token, e, request, response);
	}

	/**
	 * Resolves the {@link AuthenticatingExtensionPoint} for the current request.
	 * First checks the thread-local cache, then delegates to {@link ExtensionPointUtils}.
	 *
	 * @param request  the incoming servlet request
	 * @param response the outgoing servlet response
	 * @return the resolved authentication extension point
	 */
	protected AuthenticatingExtensionPoint getAuthcPoint(ServletRequest request, ServletResponse response) {
		AuthenticatingExtensionPoint authcPoint = ExtensionPointUtils.AUTHC_THREAD_LOCAL.get();
		if(authcPoint != null) {
			return authcPoint;
		}
		String pluginId =  this.getPluginId(request, response);
		String extensionId = this.getExtensionId(request, response);
		return ExtensionPointUtils.getAuthcPoint(request, response, getPluginManager(), pluginId, extensionId);
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
