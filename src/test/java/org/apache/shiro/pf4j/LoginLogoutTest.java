/*
 * Copyright (c) 2018 (https://github.com/easy-4-java).
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
package org.apache.shiro.pf4j;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.pf4j.authc.token.ExtensionPointAuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Assert;
import org.junit.Test;

public class LoginLogoutTest {

	@Test
	public void testHelloworld() {
		
		// 1、获取SecurityManager工厂，此处使用Ini配置文件初始化SecurityManager
		Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
		// 2、得到SecurityManager实例 并绑定给SecurityUtils
		org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		// 3、得到Subject及创建用户名/密码身份验证Token（即用户身份/凭证）
		Subject subject = SecurityUtils.getSubject();
		ExtensionPointAuthenticationToken token = new ExtensionPointAuthenticationToken("zhang", "123", false);

		try {
			// 4、登录，即身份验证
			subject.login(token);
		} catch (AuthenticationException e) {
			// 5、身份验证失败
		}
		
		Assert.assertEquals(true, subject.isAuthenticated()); // 断言用户已经登录

		// 6、退出
		subject.logout();
	}
}
