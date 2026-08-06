package org.apache.shiro.pf4j.web.filter.authc;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.biz.web.filter.authc.AbstractLogoutFilter;
import org.apache.shiro.pf4j.authc.point.AuthenticatingExtensionPoint;
import org.apache.shiro.pf4j.utils.ExtensionPointUtils;
import org.apache.shiro.subject.Subject;
import org.pf4j.PluginManager;

/**
 * 扩展Shiro登出逻辑，增加监听回调接口
 * @author [@Loong Wan](https://github.com/loong10k)
 */
public abstract class AbstractExtensionPointLogoutFilter extends AbstractLogoutFilter {

	private PluginManager pluginManager;
	
	@Override
	protected boolean logout(ServletRequest request, ServletResponse response, Subject subject) throws Exception{
		return getAuthcPoint(request, response).logout(request, response, subject);
	}
	
	protected AuthenticatingExtensionPoint getAuthcPoint(ServletRequest request, ServletResponse response) {
		AuthenticatingExtensionPoint authcPoint = ExtensionPointUtils.AUTHC_THREAD_LOCAL.get();
		if(authcPoint != null) {
			return authcPoint;
		}
		String pluginId =  this.getPluginId(request, response);
		String extensionId = this.getExtensionId(request, response);
		return ExtensionPointUtils.getAuthcPoint(request, response, getPluginManager(), pluginId, extensionId);
	}
	
	protected abstract String getPluginId(ServletRequest request, ServletResponse response);
	protected abstract String getExtensionId(ServletRequest request, ServletResponse response);

	public PluginManager getPluginManager() {
		return pluginManager;
	}

	public void setPluginManager(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}
	
	
}
