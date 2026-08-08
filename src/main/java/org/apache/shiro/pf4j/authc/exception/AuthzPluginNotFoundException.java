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
package org.apache.shiro.pf4j.authc.exception;

import org.apache.shiro.authc.AuthenticationException;

/**
 * Exception thrown when a required PF4J authorization plugin cannot be found by its plugin identifier.
 * This typically indicates that the plugin has not been loaded or the specified plugin ID is incorrect.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see org.apache.shiro.pf4j.utils.ExtensionPointUtils#getAuthzPoint
 */
@SuppressWarnings("serial")
public class AuthzPluginNotFoundException extends AuthenticationException {

	/**
	 * Constructs a new exception with no detail message or cause.
	 */
	public AuthzPluginNotFoundException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 *
	 * @param message the detail message
	 * @param cause   the underlying cause
	 */
	public AuthzPluginNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 *
	 * @param message the detail message
	 */
	public AuthzPluginNotFoundException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the specified cause.
	 *
	 * @param cause the underlying cause
	 */
	public AuthzPluginNotFoundException(Throwable cause) {
		super(cause);
	}
}