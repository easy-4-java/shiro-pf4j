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
 * 基于Pf4插件的抽象的授权 (authorization)过滤器
 * @author [@Loong Wan](https://github.com/loong10k)
 */
public abstract class AbstracExtensionPointAuthorizationFilter extends AuthorizationFilter  {

	private PluginManager pluginManager;
	
	@Override
	protected boolean isEnabled(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		return getAuthzPoint(request, response).isEnabled(request, response);
	}
	
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {
		return getAuthzPoint(request, response).isAccessAllowed(request, response, mappedValue);
	}

	@Override
	protected boolean isLoginRequest(ServletRequest request, ServletResponse response) {
		return getAuthzPoint(request, response).isLoginRequest(request, response);
	}
	
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {
		AuthorizationExtensionPoint authzPoint = getAuthzPoint(request, response);
		if(authzPoint.isPointSubmission(request, response)) {
			return authzPoint.onAccessDenied(request, response, mappedValue);	
		}
		return super.onAccessDenied(request, response, mappedValue);
	}
	
	protected AuthorizationExtensionPoint getAuthzPoint(ServletRequest request, ServletResponse response) {
		AuthorizationExtensionPoint authzPoint = ExtensionPointUtils.AUTHZ_THREAD_LOCAL.get();
		if(authzPoint != null) {
			return authzPoint;
		}
		String pluginId =  this.getPluginId(request, response);
		String extensionId = this.getExtensionId(request, response);
		return ExtensionPointUtils.getAuthzPoint(request, response, getPluginManager(), pluginId, extensionId);
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
