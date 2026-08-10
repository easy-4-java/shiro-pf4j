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
package org.apache.shiro.pf4j.authc.point;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.pf4j.ExtensionPoint;

/**
 * Extension point interface for authentication (authc) logic provided by PF4J plugins.
 * Implementations of this interface are discovered at runtime via the PF4J plugin system
 * and are used to delegate authentication decisions such as login requests, access control,
 * token creation, and login/logout callbacks to plugin-supplied logic.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see org.apache.shiro.pf4j.annotation.AuthcMapping
 * @see org.apache.shiro.pf4j.web.filter.authc.AbstractExtensionPointAuthenticatingFilter
 */
public interface AuthenticatingExtensionPoint extends ExtensionPoint {

	/**
     * Returns {@code true} if this filter should filter the specified request, {@code false} if it should let the
     * request/response pass through immediately to the next element in the {@code FilterChain}.
     * <p>This default implementation merely returns the value of isEnabled(), which is
     * {@code true} by default (to ensure the filter always executes by default), but it can be overridden by
     * subclasses for request-specific behavior if necessary.  For example, a filter could be enabled or disabled
     * based on the request path being accessed.
     * </p>
     * <b>Helpful Hint:</b> if your subclass extends {@link org.apache.shiro.web.filter.PathMatchingFilter PathMatchingFilter},
     * you may wish to instead override the
     * {@link org.apache.shiro.web.filter.PathMatchingFilter#isEnabled(javax.servlet.ServletRequest, javax.servlet.ServletResponse, String, Object)
     * PathMatchingFilter.isEnabled(request,response,path,pathSpecificConfig)}
     * method if you want to make your enable/disable decision based on any path-specific configuration.
     *
     * @param request the incoming servlet request
     * @param response the outbound servlet response
     * @return {@code true} if this filter should filter the specified request, {@code false} if it should let the
     * request/response pass through immediately to the next element in the {@code FilterChain}.
     * @throws IOException in the case of any IO error
     * @throws ServletException in the case of any error
     */
    boolean isEnabled(ServletRequest request, ServletResponse response) throws ServletException, IOException;

    /**
     * Determines whether the given request represents a submission to an extension point
     * (e.g., a POST to an authentication endpoint).
     *
     * @param request  the incoming servlet request
     * @param response the outgoing servlet response
     * @return {@code true} if the request is a point submission, {@code false} otherwise
     */
    boolean isPointSubmission(ServletRequest request, ServletResponse response);

	/**
     * Returns {@code true} if the request is allowed to proceed through the filter normally, or {@code false}
     * if the request should be handled by the
     * {@link #onAccessDenied(ServletRequest,ServletResponse,Object) onAccessDenied(request,response,mappedValue)}
     * method instead.
     *
     * @param request     the incoming servlet request
     * @param response    the outgoing servlet response
     * @param mappedValue the filter-specific config value mapped to this filter in the URL rules mappings
     * @return {@code true} if the request should proceed through the filter normally, {@code false} if the
     *         request should be processed by this filter's
     *         {@link #onAccessDenied(ServletRequest,ServletResponse,Object)} method instead
     */
    boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) ;

    /**
     * Returns {@code true} if the incoming request is a login request, {@code false} otherwise.
     * <p>
     * The default implementation merely returns {@code true} if the incoming request matches the configured
     * loginUrl by calling {@code pathsMatch(loginUrl, request)}.
     *
     * @param request  the incoming servlet request
     * @param response the outgoing servlet response
     * @return {@code true} if the incoming request is a login request, {@code false} otherwise
     */
    boolean isLoginRequest(ServletRequest request, ServletResponse response);

    /**
     * Returns {@code true} if the request is an HTTP {@code POST}, {@code false} otherwise.
     * Can be overridden by subclasses for custom login submission detection behavior.
     *
     * @param request  the incoming servlet request
     * @param response the outgoing servlet response
     * @return {@code true} if the request is an HTTP {@code POST}, {@code false} otherwise
     */
	boolean isLoginSubmission(ServletRequest request, ServletResponse response);

    /**
     * Processes requests where the subject was denied access as determined by the
     * {@link #isAccessAllowed(javax.servlet.ServletRequest, javax.servlet.ServletResponse, Object) isAccessAllowed}
     * method, retaining the {@code mappedValue} that was used during configuration.
     * <p>
     * This method immediately delegates to onAccessDenied(ServletRequest,ServletResponse) as a
     * convenience in that most post-denial behavior does not need the mapped config again.
     *
     * @param request     the incoming servlet request
     * @param response    the outgoing servlet response
     * @param mappedValue the config specified for the filter in the matching request's filter chain
     * @return {@code true} if the request should continue to be processed; {@code false} if the subclass will
     *         handle/render the response directly
     * @throws Exception if there is an error processing the request
     */
    boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception;

    /**
     * Creates an authentication token from the given servlet request and response.
     *
     * @param request  the incoming servlet request
     * @param response the outgoing servlet response
     * @return the authentication token extracted from the request
     */
	AuthenticationToken createToken(ServletRequest request, ServletResponse response);

    /**
     * Callback invoked when login succeeds. Implementations may perform post-login actions
     * such as redirecting the user or recording audit events.
     *
     * @param token   the authentication token used for login
     * @param subject the authenticated subject
     * @param request  the incoming servlet request
     * @param response the outgoing servlet response
     * @return {@code true} if processing should continue, {@code false} otherwise
     * @throws Exception if an error occurs during processing
     */
	boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response)
			throws Exception;

    /**
     * Callback invoked when login fails. Implementations may perform post-failure actions
     * such as incrementing failure counters or recording audit events.
     *
     * @param token   the authentication token used for login
     * @param e       the authentication exception that caused the failure
     * @param request  the incoming servlet request
     * @param response the outgoing servlet response
     * @return {@code true} if processing should continue, {@code false} otherwise
     */
	boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request,
			ServletResponse response);

    /**
     * Callback invoked when access to a resource succeeds for an already-authenticated subject.
     *
     * @param token   the authentication token associated with the subject
     * @param subject the authenticated subject
     * @param request  the incoming servlet request
     * @param response the outgoing servlet response
     * @return {@code true} if processing should continue, {@code false} otherwise
     */
	boolean onAccessSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
			ServletResponse response);

    /**
     * Callback invoked when access to a resource fails.
     *
     * @param token   the authentication token associated with the subject
     * @param e       the exception that caused the failure
     * @param request  the incoming servlet request
     * @param response the outgoing servlet response
     * @return {@code true} if processing should continue, {@code false} otherwise
     */
	boolean onAccessFailure(AuthenticationToken token, Exception e, ServletRequest request,
			ServletResponse response);

	 /**
     * Executes cleanup logic in the {@code finally} code block in the doFilterInternal implementation,
     * as well as handles any exceptions properly.
     *
     * @param request  the incoming servlet request
     * @param response the outgoing servlet response
     * @param existing any exception that might have occurred while executing the {@code FilterChain} or
     *                 pre or post advice, or {@code null} if the pre/chain/post execution did not throw an exception
     * @throws ServletException if any exception other than an {@code IOException} is thrown
     * @throws IOException      if the pre/chain/post execution throw an {@code IOException}
     */
    void cleanup(ServletRequest request, ServletResponse response, Exception existing)
            throws ServletException, IOException;

    /**
     * Performs logout for the given subject. Implementations should clear authentication state
     * and perform any necessary cleanup.
     *
     * @param request  the incoming servlet request
     * @param response the outgoing servlet response
     * @param subject  the subject to log out
     * @return {@code true} if processing should continue, {@code false} otherwise
     */
    boolean logout(ServletRequest request, ServletResponse response, Subject subject);

}
