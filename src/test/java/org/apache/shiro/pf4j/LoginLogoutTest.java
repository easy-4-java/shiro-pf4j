/*
 * Copyright (c) 2018 (https://github.com/loong10k).
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
import org.apache.shiro.ini.IniSecurityManagerFactory;
import org.apache.shiro.pf4j.authc.token.ExtensionPointAuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.mgt.SecurityManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * Integration test for basic Shiro login/logout using ExtensionPointAuthenticationToken.
 */
public class LoginLogoutTest {

	@Test
	public void testHelloworld() {
		// 1. Build SecurityManager from shiro.ini
		IniSecurityManagerFactory factory = new IniSecurityManagerFactory("classpath:shiro.ini");
		// 2. Get SecurityManager and bind to SecurityUtils
		SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		// 3. Create subject and token
		Subject subject = SecurityUtils.getSubject();
		ExtensionPointAuthenticationToken token = new ExtensionPointAuthenticationToken("zhang", "123", false);

		try {
			// 4. Login
			subject.login(token);
		} catch (AuthenticationException e) {
			// 5. Authentication failed
		}

		Assert.assertEquals(true, subject.isAuthenticated());

		// 6. Logout
		subject.logout();
	}
}
