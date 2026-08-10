package org.apache.shiro.pf4j.web.filter.authc;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.biz.web.filter.authc.AbstractLogoutFilter;
import org.apache.shiro.pf4j.authc.point.AuthenticatingExtensionPoint;
import org.apache.shiro.pf4j.utils.ExtensionPointUtils;
import org.apache.shiro.subject.Subject;
import org.pf4j.PluginManager;

/**
 * Abstract logout filter that delegates logout logic to a PF4J
 * {@link AuthenticatingExtensionPoint} resolved at runtime. This filter extends Shiro's
 * {@link AbstractLogoutFilter} and forwards the logout operation to the extension point.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see AuthenticatingExtensionPoint
 * @see ExtensionPointUtils#getAuthcPoint
 * @see DefaultExtensionPointLogoutFilter
 */
public abstract class AbstractExtensionPointLogoutFilter extends AbstractLogoutFilter {

	/** The PF4J plugin manager used to resolve extension points. */
	private PluginManager pluginManager;

	/**
	 * Delegates the logout operation to the resolved authentication extension point.
	 *
	 * @param request  the incoming servlet request
	 * @param response the outgoing servlet response
	 * @param subject  the subject to log out
	 * @return {@code true} if processing should continue
	 * @throws Exception if an error occurs during logout
	 */
	@Override
	protected boolean logout(ServletRequest request, ServletResponse response, Subject subject) throws Exception{
		return getAuthcPoint(request, response).logout(request, response, subject);
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
