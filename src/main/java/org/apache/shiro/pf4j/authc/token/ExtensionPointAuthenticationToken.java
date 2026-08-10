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
package org.apache.shiro.pf4j.authc.token;

import org.apache.shiro.biz.authc.token.DefaultAuthenticationToken;

/**
 * Authentication token implementation for PF4J extension point based authentication.
 * Extends {@link DefaultAuthenticationToken} to carry credentials through the
 * Shiro authentication pipeline when using plugin-provided authentication logic.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see org.apache.shiro.biz.authc.token.DefaultAuthenticationToken
 */
@SuppressWarnings("serial")
public class ExtensionPointAuthenticationToken extends DefaultAuthenticationToken {

	/**
	 * Constructs an empty token with no credentials set.
	 */
	public ExtensionPointAuthenticationToken() {
		super();
	}

	/**
	 * Constructs a token with username, password (as char array), rememberMe flag, and host.
	 *
	 * @param username   the account username
	 * @param password   the account password as a character array
	 * @param rememberMe whether to remember the subject across sessions
	 * @param host       the host from which the authentication attempt originates
	 */
	public ExtensionPointAuthenticationToken(String username, char[] password, boolean rememberMe, String host) {
		super(username, password, rememberMe, host);
	}

	/**
	 * Constructs a token with username, password (as char array), and rememberMe flag.
	 *
	 * @param username   the account username
	 * @param password   the account password as a character array
	 * @param rememberMe whether to remember the subject across sessions
	 */
	public ExtensionPointAuthenticationToken(String username, char[] password, boolean rememberMe) {
		super(username, password, rememberMe);
	}

	/**
	 * Constructs a token with username, password (as char array), captcha, rememberMe flag, and host.
	 *
	 * @param username   the account username
	 * @param password   the account password as a character array
	 * @param captcha    the captcha value for verification
	 * @param rememberMe whether to remember the subject across sessions
	 * @param host       the host from which the authentication attempt originates
	 */
	public ExtensionPointAuthenticationToken(String username, char[] password, String captcha, boolean rememberMe,
			String host) {
		super(username, password, captcha, rememberMe, host);
	}

	/**
	 * Constructs a token with username, password (as char array), captcha, and host.
	 *
	 * @param username the account username
	 * @param password the account password as a character array
	 * @param captcha  the captcha value for verification
	 * @param host     the host from which the authentication attempt originates
	 */
	public ExtensionPointAuthenticationToken(String username, char[] password, String captcha, String host) {
		super(username, password, captcha, host);
	}

	/**
	 * Constructs a token with username, password (as String), rememberMe flag, and host.
	 *
	 * @param username   the account username
	 * @param password   the account password as a string
	 * @param rememberMe whether to remember the subject across sessions
	 * @param host       the host from which the authentication attempt originates
	 */
	public ExtensionPointAuthenticationToken(String username, String password, boolean rememberMe, String host) {
		super(username, password, rememberMe, host);
	}

	/**
	 * Constructs a token with username, password (as String), and rememberMe flag.
	 *
	 * @param username   the account username
	 * @param password   the account password as a string
	 * @param rememberMe whether to remember the subject across sessions
	 */
	public ExtensionPointAuthenticationToken(String username, String password, boolean rememberMe) {
		super(username, password, rememberMe);
	}

	/**
	 * Constructs a token with username, password (as String), captcha, rememberMe flag, and host.
	 *
	 * @param username   the account username
	 * @param password   the account password as a string
	 * @param captcha    the captcha value for verification
	 * @param rememberMe whether to remember the subject across sessions
	 * @param host       the host from which the authentication attempt originates
	 */
	public ExtensionPointAuthenticationToken(String username, String password, String captcha, boolean rememberMe,
			String host) {
		super(username, password, captcha, rememberMe, host);
	}

	/**
	 * Constructs a token with username, password (as String), captcha, and host.
	 *
	 * @param username the account username
	 * @param password the account password as a string
	 * @param captcha  the captcha value for verification
	 * @param host     the host from which the authentication attempt originates
	 */
	public ExtensionPointAuthenticationToken(String username, String password, String captcha, String host) {
		super(username, password, captcha, host);
	}

}
