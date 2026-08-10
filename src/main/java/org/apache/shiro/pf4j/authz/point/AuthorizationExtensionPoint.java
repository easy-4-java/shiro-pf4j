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
package org.apache.shiro.pf4j.authz.point;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.pf4j.ExtensionPoint;

/**
 * Extension point interface for authorization (authz) logic provided by PF4J plugins.
 * Implementations of this interface are discovered at runtime via the PF4J plugin system
 * and are used to delegate authorization decisions such as access control and login request
 * detection to plugin-supplied logic.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see org.apache.shiro.pf4j.annotation.AuthzMapping
 * @see org.apache.shiro.pf4j.web.filter.authz.AbstracExtensionPointAuthorizationFilter
 */
public interface AuthorizationExtensionPoint extends ExtensionPoint {

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
     * Determines whether the given request represents a submission to an authorization extension point.
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
     * @throws Exception if an error occurs during processing
     */
    boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception;

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

}
