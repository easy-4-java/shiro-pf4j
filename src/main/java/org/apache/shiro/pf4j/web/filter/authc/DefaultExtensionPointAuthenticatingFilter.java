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

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.pf4j.utils.ExtensionPointUtils;

/**
 * Default ExtensionPoint 认证 (authentication)过滤器
 * @author [@Loong Wan](https://github.com/loong10k)
 */
public class DefaultExtensionPointAuthenticatingFilter extends AbstractExtensionPointAuthenticatingFilter {

	private String extensionParamName = ExtensionPointUtils.EXTENSION_PARAM;
    private String pluginParamName = ExtensionPointUtils.PLUGINID_PARAM;
    
	public DefaultExtensionPointAuthenticatingFilter() {
		super();
	}
	
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
