/*
 * Copyright (c) 2018, hiwepy (https://github.com/easy-4-java).
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
package org.apache.shiro.pf4j.authc.token;

import org.apache.shiro.biz.authc.token.DefaultAuthenticationToken;

/**
 * Pf4j ExtensionPoint Authentication Token
 * 
 * @author ： <a href="https://github.com/easy-4-java">hiwepy</a>
 */
@SuppressWarnings("serial")
public class ExtensionPointAuthenticationToken extends DefaultAuthenticationToken {

	public ExtensionPointAuthenticationToken() {
		super();
	}

	public ExtensionPointAuthenticationToken(String username, char[] password, boolean rememberMe, String host) {
		super(username, password, rememberMe, host);
	}

	public ExtensionPointAuthenticationToken(String username, char[] password, boolean rememberMe) {
		super(username, password, rememberMe);
	}

	public ExtensionPointAuthenticationToken(String username, char[] password, String captcha, boolean rememberMe,
			String host) {
		super(username, password, captcha, rememberMe, host);
	}

	public ExtensionPointAuthenticationToken(String username, char[] password, String captcha, String host) {
		super(username, password, captcha, host);
	}

	public ExtensionPointAuthenticationToken(String username, String password, boolean rememberMe, String host) {
		super(username, password, rememberMe, host);
	}

	public ExtensionPointAuthenticationToken(String username, String password, boolean rememberMe) {
		super(username, password, rememberMe);
	}

	public ExtensionPointAuthenticationToken(String username, String password, String captcha, boolean rememberMe,
			String host) {
		super(username, password, captcha, rememberMe, host);
	}

	public ExtensionPointAuthenticationToken(String username, String password, String captcha, String host) {
		super(username, password, captcha, host);
	}

}
