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
 * Exception thrown when a required authentication extension point cannot be found within a loaded PF4J plugin.
 * This typically indicates that the plugin does not contain an {@link org.apache.shiro.pf4j.authc.point.AuthenticatingExtensionPoint}
 * matching the requested extension identifier.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see org.apache.shiro.pf4j.utils.ExtensionPointUtils#getAuthcPoint
 */
@SuppressWarnings("serial")
public class AuthcPointNotFoundException extends AuthenticationException {

	/**
	 * Constructs a new exception with no detail message or cause.
	 */
	public AuthcPointNotFoundException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 *
	 * @param message the detail message
	 * @param cause   the underlying cause
	 */
	public AuthcPointNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 *
	 * @param message the detail message
	 */
	public AuthcPointNotFoundException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the specified cause.
	 *
	 * @param cause the underlying cause
	 */
	public AuthcPointNotFoundException(Throwable cause) {
		super(cause);
	}
}