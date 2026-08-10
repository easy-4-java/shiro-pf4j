package org.apache.shiro.pf4j.realm;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.pf4j.authc.token.ExtensionPointAuthenticationToken;
import org.apache.shiro.pf4j.utils.ExtensionPointUtils;

/**
 * Default concrete implementation of {@link AuthorizingRealmExtensionPoint}.
 * Resolves the plugin ID and extension ID from request headers, parameters, or cookies
 * using {@link ExtensionPointUtils}. Supports only {@link ExtensionPointAuthenticationToken}
 * as the authentication token type.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see AuthorizingRealmExtensionPoint
 * @see ExtensionPointUtils#getPluginId
 * @see ExtensionPointUtils#getExtensionId
 */
public class DefaultExtensionPointAuthorizingRealm extends AuthorizingRealmExtensionPoint {

	/** The request parameter/header/cookie name used to resolve the extension point identifier. */
	private String extensionParamName = ExtensionPointUtils.EXTENSION_PARAM;

	/** The request parameter/header/cookie name used to resolve the plugin identifier. */
    private String pluginParamName = ExtensionPointUtils.PLUGINID_PARAM;

    /**
     * Returns the authentication token class supported by this realm.
     *
     * @return {@link ExtensionPointAuthenticationToken}{@code .class}
     */
	@Override
	public Class<? extends AuthenticationToken> getAuthenticationTokenClass() {
		return ExtensionPointAuthenticationToken.class;
	}

	/**
	 * Resolves the plugin identifier from the servlet request using the configured parameter name.
	 *
	 * @param request  the incoming servlet request
	 * @param response the outgoing servlet response
	 * @return the plugin identifier, or {@code null} if not found
	 */
	@Override
	protected String getPluginId(ServletRequest request, ServletResponse response) {
        return ExtensionPointUtils.getPluginId(request, response, getPluginParamName());
	}

	/**
	 * Resolves the extension point identifier from the servlet request using the configured parameter name.
	 *
	 * @param request  the incoming servlet request
	 * @param response the outgoing servlet response
	 * @return the extension point identifier, or {@code null} if not found
	 */
	@Override
	protected String getExtensionId(ServletRequest request, ServletResponse response) {
        return ExtensionPointUtils.getExtensionId(request, response, getExtensionParamName());
	}

	/**
	 * Returns the request parameter name used to resolve the extension point identifier.
	 *
	 * @return the extension parameter name
	 */
	public String getExtensionParamName() {
		return extensionParamName;
	}

	/**
	 * Sets the request parameter name used to resolve the extension point identifier.
	 *
	 * @param extensionParamName the extension parameter name
	 */
	public void setExtensionParamName(String extensionParamName) {
		this.extensionParamName = extensionParamName;
	}

	/**
	 * Returns the request parameter name used to resolve the plugin identifier.
	 *
	 * @return the plugin parameter name
	 */
	public String getPluginParamName() {
		return pluginParamName;
	}

	/**
	 * Sets the request parameter name used to resolve the plugin identifier.
	 *
	 * @param pluginParamName the plugin parameter name
	 */
	public void setPluginParamName(String pluginParamName) {
		this.pluginParamName = pluginParamName;
	}

}
