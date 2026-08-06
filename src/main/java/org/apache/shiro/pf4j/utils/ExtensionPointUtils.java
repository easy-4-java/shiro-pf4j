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
package org.apache.shiro.pf4j.utils;

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.biz.utils.StringUtils2;
import org.apache.shiro.web.util.WebUtils;
import org.apache.shiro.pf4j.annotation.AuthzMapping;
import org.apache.shiro.pf4j.authc.exception.AuthcPluginNotFoundException;
import org.apache.shiro.pf4j.authc.exception.AuthcPointNotFoundException;
import org.apache.shiro.pf4j.authc.exception.AuthzPluginNotFoundException;
import org.apache.shiro.pf4j.authc.exception.AuthzPointNotFoundException;
import org.apache.shiro.pf4j.authc.point.AuthenticatingExtensionPoint;
import org.apache.shiro.pf4j.authz.point.AuthorizationExtensionPoint;
import org.pf4j.ExtensionPoint;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionPointUtils {

	private static final Logger LOG = LoggerFactory.getLogger(ExtensionPointUtils.class);
	public static final String PLUGINID_PARAM = "plugin";
	public static final String EXTENSION_PARAM = "extension";
	public static ThreadLocal<AuthenticatingExtensionPoint> AUTHC_THREAD_LOCAL = new ThreadLocal<AuthenticatingExtensionPoint>();
	public static ThreadLocal<AuthorizationExtensionPoint> AUTHZ_THREAD_LOCAL = new ThreadLocal<AuthorizationExtensionPoint>();
	
	public static AuthenticatingExtensionPoint getAuthcPoint(ServletRequest request, ServletResponse response, 
			PluginManager pluginManager, String pluginId, String extensionId) throws AuthenticationException {
		AuthenticatingExtensionPoint authcPoint = AUTHC_THREAD_LOCAL.get();
		if(authcPoint == null) {
			// 检查插件是否加载
			PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
			if(wrapper == null) {
				throw new AuthcPluginNotFoundException(String.format("Pf4j plugin not found whith pluginId [%s]", pluginId));
			}
			// 记录日志
			if(LOG.isDebugEnabled()) {
				LOG.debug(wrapper.toString());
			}
			// 查找插件内的实现对象
			List<ExtensionPoint> extensions = pluginManager.getExtensions(ExtensionPoint.class, pluginId);
			for (ExtensionPoint extension : extensions) {
				// 注解信息
				AuthzMapping mapping = extension.getClass().getAnnotation(AuthzMapping.class);
				// 判断类型
				if(mapping != null && StringUtils2.equals(mapping.id(), extensionId) 
						&& extension instanceof AuthenticatingExtensionPoint) {
					authcPoint = (AuthenticatingExtensionPoint) extension;
					AUTHC_THREAD_LOCAL.set(authcPoint);
					break;
				}
			}
			if(authcPoint == null) {
				throw new AuthcPointNotFoundException(String.format("Authc Extension Point not found whith pluginId [%s], extensionId [%s]", pluginId, extensionId));
			}
		}
		return authcPoint;
	}
	
	public static AuthorizationExtensionPoint getAuthzPoint(ServletRequest request, ServletResponse response, 
			PluginManager pluginManager, String pluginId, String extensionId) throws AuthenticationException {
		AuthorizationExtensionPoint authzPoint = AUTHZ_THREAD_LOCAL.get();
		if(authzPoint == null) {
			// 检查插件是否加载
			PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
			if(wrapper == null) {
				throw new AuthzPluginNotFoundException(String.format("Pf4j plugin not found whith pluginId [%s]", pluginId));
			}
			// 记录日志
			if(LOG.isDebugEnabled()) {
				LOG.debug(wrapper.toString());
			}
			// 查找插件内的实现对象
			List<ExtensionPoint> extensions = pluginManager.getExtensions(ExtensionPoint.class, pluginId);
			for (ExtensionPoint extension : extensions) {
				// 注解信息
				AuthzMapping mapping = extension.getClass().getAnnotation(AuthzMapping.class);
				// 判断类型
				if(mapping != null && StringUtils2.equals(mapping.id(), extensionId) 
						&& extension instanceof AuthorizationExtensionPoint) {
					authzPoint = (AuthorizationExtensionPoint) extension;
					AUTHZ_THREAD_LOCAL.set(authzPoint);
					break;
				}
			}
			if(authzPoint == null) {
				throw new AuthzPointNotFoundException(String.format("Authz Extension Point not found whith pluginId [%s], extensionId [%s]", pluginId, extensionId));
			}
		}
		return authzPoint;
	}


	public static String getPluginId(ServletRequest request, ServletResponse response , String pluginParamName) {
		HttpServletRequest httpRequest = WebUtils.toHttp(request);
        //从header中获取pluginId
        String pluginId = httpRequest.getHeader(pluginParamName);
        //如果header中不存在pluginId，则从参数中获取pluginId
        if (StringUtils2.isEmpty(pluginId)) {
            return httpRequest.getParameter(pluginParamName);
        }
        if (StringUtils2.isEmpty(pluginId)) {
            // 从 cookie 获取 pluginId
            Cookie[] cookies = httpRequest.getCookies();
            if (null == cookies || cookies.length == 0) {
                return null;
            }
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(pluginParamName)) {
                    pluginId = cookie.getValue();
                    break;
                }
            }
        }
        return pluginId;
	}

	public static String getExtensionId(ServletRequest request, ServletResponse response, String extensionParamName) {
		HttpServletRequest httpRequest = WebUtils.toHttp(request);
        //从header中获取extensionId
        String extensionId = httpRequest.getHeader(extensionParamName);
        //如果header中不存在extensionId，则从参数中获取extensionId
        if (StringUtils2.isEmpty(extensionId)) {
            return httpRequest.getParameter(extensionParamName);
        }
        if (StringUtils2.isEmpty(extensionId)) {
            // 从 cookie 获取 extensionId
            Cookie[] cookies = httpRequest.getCookies();
            if (null == cookies || cookies.length == 0) {
                return null;
            }
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(extensionParamName)) {
                    extensionId = cookie.getValue();
                    break;
                }
            }
        }
        return extensionId;
	}
	
}
