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
 * <b>抽象Realm</b> 
 * <p>公共需要做的事：1.记录日志；2.提高更高级api；3.封装内部处理逻辑；4.事件监听；5.基于Pf4j插件，具备扩展能力</p>
 * @author [@Loong Wan](https://github.com/loong10k)
 */
@SuppressWarnings("unchecked")
public abstract class AuthorizingRealmExtensionPoint extends AuthorizingRealm  implements ExtensionPoint{

	private static final Logger LOG = LoggerFactory.getLogger(AbstractAuthorizingRealm.class);

	//realm listeners
	protected List<AuthorizingRealmListener> realmsListeners;
	private ThreadLocal<PrincipalRepositoryExtensionPoint> THREAD_LOCAL = new ThreadLocal<PrincipalRepositoryExtensionPoint>();
	private PluginManager pluginManager;
	    
	/**
	 * 获取授权信息;
	 * 
	 * @author [@Loong Wan](https://github.com/loong10k)
	 * @param principals : PrincipalCollection是一个身份集合，因为我们现在就一个Realm，所以直接调用getPrimaryPrincipal得到之前传入的用户名即可；然后根据用户名调用UserService接口获取角色及权限信息。
	 * @return 授权信息
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
	 * 
	 *  获取身份验证相关信息
	 * 
	 *  <pre>
	 * 	首先根据传入的用户名获取User信息；然后如果user为空，那么抛出没找到帐号异常UnknownAccountException；
	 * 	如果user找到但锁定了抛出锁定异常LockedAccountException；
	 *  最后生成AuthenticationInfo信息，交给间接父类AuthenticatingRealm使用CredentialsMatcher进行判断密码是否匹配，如果不匹配将抛出密码错误异常IncorrectCredentialsException；
	 *  
	 *  另外如果密码重试此处太多将抛出超出重试次数异常ExcessiveAttemptsException；
	 *  在组装SimpleAuthenticationInfo信息时，需要传入：
	 *  	身份信息（用户名）、凭据（密文密码）、盐（username+salt），
	 *  CredentialsMatcher使用盐加密传入的明文密码和此处的密文密码进行匹配。
	 * 
	 *  </pre>
	 * 
	 * @author [@Loong Wan](https://github.com/loong10k)
	 * @param token 认证Token
	 * @return 授权信息
	 * @throws AuthenticationException 认证异常
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
		
		//调用事件监听器
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
	
	public void clearAuthorizationCache(){
		clearCachedAuthorizationInfo(SecurityUtils.getSubject().getPrincipals());
	}
	
	protected PrincipalRepositoryExtensionPoint getRepositoryPoint() {
		
		WebSubject subject = SubjectUtils.getWebSubject();
		ServletRequest request = subject.getServletRequest();
		ServletResponse response = subject.getServletResponse();
		
		PrincipalRepositoryExtensionPoint authcPoint = THREAD_LOCAL.get();
		if(authcPoint == null) {
			String pluginId =  this.getPluginId(request, response);
			// 检查插件是否加载
			PluginWrapper wrapper = getPluginManager().getPlugin(pluginId);
			if(wrapper == null) {
				throw new AuthcPluginNotFoundException(String.format("Pf4j plugin not found whith pluginId [%s]", pluginId));
			}
			// 记录日志
			if(LOG.isDebugEnabled()) {
				LOG.debug(wrapper.toString());
			}
			// 查找插件内的实现对象
			List<ExtensionPoint> extensions = getPluginManager().getExtensions(ExtensionPoint.class, pluginId);
			String extensionId = this.getExtensionId(request, response);
			for (ExtensionPoint extension : extensions) {
				// 注解信息
				AuthzMapping mapping = extension.getClass().getAnnotation(AuthzMapping.class);
				// 判断类型
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

	protected abstract String getPluginId(ServletRequest request, ServletResponse response);
	protected abstract String getExtensionId(ServletRequest request, ServletResponse response);

	public PluginManager getPluginManager() {
		return pluginManager;
	}

	public void setPluginManager(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}

	public List<AuthorizingRealmListener> getRealmsListeners() {
		return realmsListeners;
	}

	public void setRealmsListeners(List<AuthorizingRealmListener> realmsListeners) {
		this.realmsListeners = realmsListeners;
	}
	
}
