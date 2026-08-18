package org.apache.shiro.pf4j.web.filter.authz;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.pf4j.utils.ExtensionPointUtils;

/**
 * Default ExtensionPoint 授权 (authorization) 过滤器
 * @author ： <a href="https://github.com/easy-4-java">hiwepy</a>
 */
public class DefaultExtensionPointAuthorizationFilter extends AbstracExtensionPointAuthorizationFilter {

	private String extensionParamName = ExtensionPointUtils.EXTENSION_PARAM;
    private String pluginParamName = ExtensionPointUtils.PLUGINID_PARAM;
   
    @Override
	protected String getPluginId(ServletRequest request, ServletResponse response) {
        return ExtensionPointUtils.getPluginId(request, response, getPluginParamName());
	}

	@Override
	protected String getExtensionId(ServletRequest request, ServletResponse response) {
        return ExtensionPointUtils.getExtensionId(request, response, getExtensionParamName());
	}

	public String getExtensionParamName() {
		return extensionParamName;
	}

	public void setExtensionParamName(String extensionParamName) {
		this.extensionParamName = extensionParamName;
	}

	public String getPluginParamName() {
		return pluginParamName;
	}

	public void setPluginParamName(String pluginParamName) {
		this.pluginParamName = pluginParamName;
	}
	
}
