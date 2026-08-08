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
package org.apache.shiro.pf4j.authz.point;

import org.apache.shiro.biz.authz.principal.ShiroPrincipalRepository;
import org.pf4j.ExtensionPoint;

/**
 * Extension point interface for principal repository logic provided by PF4J plugins.
 * Combines the PF4J {@link ExtensionPoint} marker with {@link ShiroPrincipalRepository}
 * so that plugin implementations can supply authentication info, roles, and permissions
 * to the Shiro security framework.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see org.apache.shiro.biz.authz.principal.ShiroPrincipalRepository
 * @see org.apache.shiro.pf4j.realm.AuthorizingRealmExtensionPoint
 */
public interface PrincipalRepositoryExtensionPoint extends ExtensionPoint, ShiroPrincipalRepository  {
}
