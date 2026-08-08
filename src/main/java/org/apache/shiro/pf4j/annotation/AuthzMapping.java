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
package org.apache.shiro.pf4j.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to map an authorization extension point implementation to a unique identifier.
 * When a PF4J plugin provides a {@link org.apache.shiro.pf4j.authz.point.AuthorizationExtensionPoint}
 * or {@link org.apache.shiro.pf4j.authz.point.PrincipalRepositoryExtensionPoint}, the implementing
 * class should be annotated with {@code @AuthzMapping} so that the framework can discover and route
 * authorization requests to the correct extension at runtime.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see org.apache.shiro.pf4j.authz.point.AuthorizationExtensionPoint
 * @see org.apache.shiro.pf4j.authz.point.PrincipalRepositoryExtensionPoint
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface AuthzMapping {

	/**
	 * Returns the unique identifier for this authorization extension point.
	 *
	 * @return the non-null extension point identifier
	 */
	public String id();

	/**
	 * Returns a human-readable title for this authorization extension point.
	 *
	 * @return the title, or an empty string if not specified
	 */
	public String title() default "";

	/**
	 * Returns a description of this authorization extension point.
	 *
	 * @return the description, or an empty string if not specified
	 */
	public String desc() default "";

}
