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

@SuppressWarnings("serial")
public class AuthcPluginNotFoundException extends AuthenticationException {

	public AuthcPluginNotFoundException() {
		super();
	}

	public AuthcPluginNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthcPluginNotFoundException(String message) {
		super(message);
	}

	public AuthcPluginNotFoundException(Throwable cause) {
		super(cause);
	}
}