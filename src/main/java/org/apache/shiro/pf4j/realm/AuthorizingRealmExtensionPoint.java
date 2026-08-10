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
package org.apache.shiro.pf4j.realm;

import java.util.List;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.biz.realm.AbstractAuthorizingRealm;
import org.apache.shiro.biz.realm.AuthorizingRealmListener;
import org.apache.shiro.biz.utils.StringUtils2;
import org.apache.shiro.biz.utils.SubjectUtils;
import org.apache.shiro.pf4j.annotation.AuthzMapping;
import org.apache.shiro.pf4j.authc.exception.AuthcPluginNotFoundException;
import org.apache.shiro.pf4j.authc.exception.AuthcPointNotFoundException;
import org.apache.shiro.pf4j.authz.point.PrincipalRepositoryExtensionPoint;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.subject.WebSubject;
import org.pf4j.ExtensionPoint;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract Shiro {@link AuthorizingRealm} that is also a PF4J {@link ExtensionPoint}.
 * Provides plugin-based authentication and authorization by delegating to a
 * {@link PrincipalRepositoryExtensionPoint} discovered at runtime via the PF4J plugin system.
 * <p>Key responsibilities:
 * <ul>
 *   <li>Resolves the PF4J plugin and extension from the current web request</li>
 *   <li>Delegates authentication info retrieval to the plugin's principal repository</li>
 *   <li>Delegates authorization info (roles and permissions) to the plugin's principal repository</li>
 *   <li>Notifies registered {@link AuthorizingRealmListener}s of success or failure</li>
 * </ul>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see org.apache.shiro.pf4j.authz.point.PrincipalRepositoryExtensionPoint
 * @see org.apache.shiro.pf4j.annotation.AuthzMapping
 * @see org.apache.shiro.pf4j.realm.DefaultExtensionPointAuthorizingRealm
 */
@SuppressWarnings("unchecked")
public abstract class AuthorizingRealmExtensionPoint extends AuthorizingRealm  implements ExtensionPoint{

	private static final Logger LOG = LoggerFactory.getLogger(AbstractAuthorizingRealm.class);

	/** Listeners notified on authentication success or failure. */
	protected List<AuthorizingRealmListener> realmsListeners;

	/** Thread-local cache for the resolved principal repository extension point. */
	private ThreadLocal<PrincipalRepositoryExtensionPoint> THREAD_LOCAL = new ThreadLocal<PrincipalRepositoryExtensionPoint>();

	/** The PF4J plugin manager used to discover plugins and extensions. */
	private PluginManager pluginManager;

	/**
	 * Retrieves authorization information (roles and permissions) for the given principals
	 * by delegating to the resolved {@link PrincipalRepositoryExtensionPoint}.
	 *
	 * @param principals the identity principals of the subject being authorized
	 * @return the authorization info containing roles and permissions, or {@code null} if principals are empty
	 */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

    	if(principals == null || principals.isEmpty()){
			return null;
		}

    	Set<String> permissionsSet, rolesSet = null;
		if(principals.asList().size() <= 1){
			permissionsSet = getRepositoryPoint().getPermissions( principals.getPrimaryPrincipal());
			rolesSet = getRepositoryPoint().getRoles( principals.getPrimaryPrincipal());
		}else{
			permissionsSet = getRepositoryPoint().getPermissions(principals.asSet());
			rolesSet = getRepositoryPoint().getRoles(principals.asSet());
		}

    	SimpleAccount account = new SimpleAccount();
    	account.setRoles(rolesSet);
    	account.setStringPermissions(permissionsSet);
        return account;
    }

	/**
	 * Retrieves authentication information by delegating to the resolved
	 * {@link PrincipalRepositoryExtensionPoint}. Notifies registered
	 * {@link AuthorizingRealmListener}s of success or failure.
	 *
	 * @param token the authentication token submitted by the subject
	 * @return the authentication info for the subject
	 * @throws AuthenticationException if authentication fails or no matching principal is found
	 */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

    	LOG.info("Handle authentication token {}.", new Object[] { token });


    	AuthenticationException ex = null;
    	AuthenticationInfo info = null;
    	try {
    		info = getRepositoryPoint().getAuthenticationInfo(token);
		} catch (AuthenticationException e) {
			ex = e;
		}

		// Notify realm listeners
		if(getRealmsListeners() != null && getRealmsListeners().size() > 0){
			for (AuthorizingRealmListener realmListener : getRealmsListeners()) {
				if(ex != null || null == info){
					realmListener.onFailure(this, token, ex);
				}else{
					realmListener.onSuccess(this, info);
				}
			}
		}

		if(ex != null){
			throw ex;
		}

		return info;
    }

	/**
	 * Clears the authorization cache for the current subject.
	 */
	public void clearAuthorizationCache(){
		clearCachedAuthorizationInfo(SecurityUtils.getSubject().getPrincipals());
	}

	/**
	 * Resolves and returns the {@link PrincipalRepositoryExtensionPoint} from the current web request.
	 * Uses a thread-local cache to avoid repeated plugin lookups within the same request.
	 *
	 * @return the resolved principal repository extension point
	 * @throws AuthcPluginNotFoundException if the plugin cannot be found by the resolved plugin ID
	 * @throws AuthcPointNotFoundException  if no matching principal repository extension point is found in the plugin
	 */
	protected PrincipalRepositoryExtensionPoint getRepositoryPoint() {

		WebSubject subject = SubjectUtils.getWebSubject();
		ServletRequest request = subject.getServletRequest();
		ServletResponse response = subject.getServletResponse();

		PrincipalRepositoryExtensionPoint authcPoint = THREAD_LOCAL.get();
		if(authcPoint == null) {
			String pluginId =  this.getPluginId(request, response);
			// Check if the plugin is loaded
			PluginWrapper wrapper = getPluginManager().getPlugin(pluginId);
			if(wrapper == null) {
				throw new AuthcPluginNotFoundException(String.format("Pf4j plugin not found whith pluginId [%s]", pluginId));
			}
			// Log plugin info
			if(LOG.isDebugEnabled()) {
				LOG.debug(wrapper.toString());
			}
			// Find extension implementations within the plugin
			List<ExtensionPoint> extensions = getPluginManager().getExtensions(ExtensionPoint.class, pluginId);
			String extensionId = this.getExtensionId(request, response);
			for (ExtensionPoint extension : extensions) {
				// Check annotation
				AuthzMapping mapping = extension.getClass().getAnnotation(AuthzMapping.class);
				// Match type and ID
				if(mapping != null && StringUtils2.equals(mapping.id(), extensionId)
						&& extension instanceof PrincipalRepositoryExtensionPoint) {
					authcPoint = (PrincipalRepositoryExtensionPoint) extension;
					THREAD_LOCAL.set(authcPoint);
					break;
				}
			}
			if(authcPoint == null) {
				throw new AuthcPointNotFoundException(String.format("Principal Repository Extension Point not found whith pluginId [%s], extensionId [%s]", pluginId, extensionId));
			}
		}
		return authcPoint;
	}

	/**
	 * Resolves the PF4J plugin identifier from the current servlet request.
	 *
	 * @param request  the incoming servlet request
	 * @param response the outgoing servlet response
	 * @return the plugin identifier string
	 */
	protected abstract String getPluginId(ServletRequest request, ServletResponse response);

	/**
	 * Resolves the extension point identifier from the current servlet request.
	 *
	 * @param request  the incoming servlet request
	 * @param response the outgoing servlet response
	 * @return the extension point identifier string
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

	/**
	 * Returns the list of realm listeners notified on authentication events.
	 *
	 * @return the list of realm listeners, or {@code null} if none are registered
	 */
	public List<AuthorizingRealmListener> getRealmsListeners() {
		return realmsListeners;
	}

	/**
	 * Sets the list of realm listeners to be notified on authentication events.
	 *
	 * @param realmsListeners the list of realm listeners
	 */
	public void setRealmsListeners(List<AuthorizingRealmListener> realmsListeners) {
		this.realmsListeners = realmsListeners;
	}

}
