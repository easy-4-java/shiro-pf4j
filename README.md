# shiro-pf4j

[English](./README.md) | [简体中文](./README.zh-CN.md)

Shiro extension with PF4J — pluggable authentication and authorization for Shiro based on [PF4J](https://pf4j.org/) (the plug-in framework). Authentication, authorization and principal-repository behavior can be provided by plug-ins and selected per request via plug-in / extension identifiers.

## Table of Contents

- [1. Project Overview](#1-project-overview)
- [2. Features & Status](#2-features--status)
- [3. Requirements & Compatibility](#3-requirements--compatibility)
- [4. Architecture & Modules](#4-architecture--modules)
- [5. Installation](#5-installation)
- [6. Quick Start](#6-quick-start)
- [7. Configuration](#7-configuration)
- [8. Core Usage / API](#8-core-usage--api)
- [9. Testing & Build](#9-testing--build)
- [10. Versioning & Branches](#10-versioning--branches)
- [11. Contributing & License](#11-contributing--license)

## 1. Project Overview

**What it is**

`shiro-pf4j` lets plug-ins extend Shiro's authentication and authorization:

- **Extension points**: `AuthenticatingExtensionPoint`, `AuthorizationExtensionPoint`, `PrincipalRepositoryExtensionPoint` (extends `ShiroPrincipalRepository` from `shiro-biz`) — implemented by plug-ins, discovered through the PF4J `PluginManager`.
- **Realm**: `AuthorizingRealmExtensionPoint` / `DefaultExtensionPointAuthorizingRealm` dispatch authentication and authorization to the extension point matching the request's plug-in / extension identifiers.
- **Filters**: `AbstractExtensionPointAuthenticatingFilter` / `DefaultExtensionPointAuthenticatingFilter`, logout filters, and `AbstracExtensionPointAuthorizationFilter` / `DefaultExtensionPointAuthorizationFilter` route each request to the right plug-in extension.
- **Mapping annotations**: `@AuthcMapping` / `@AuthzMapping` (id, title, desc) mark authentication/authorization extension points.
- `ExtensionPointUtils` resolves plug-in/extension ids from request parameters (e.g. `plugin` / `extension`).

**What it is not**

- It is not a PF4J bootstrap library — the `PluginManager` (plugins directory, classloader strategy) is owned by your application and injected via `setPluginManager(...)`.
- It is not a Spring Boot starter; no auto-configuration is shipped.

**Typical scenarios**

| Scenario | Description |
| :--- | :--- |
| Multi-tenant authentication | Each tenant ships a plug-in exposing `AuthenticatingExtensionPoint`; the filter picks the extension by request params. |
| Plugin-based authorization | Plug-ins expose `AuthorizationExtensionPoint` / `PrincipalRepositoryExtensionPoint` for roles and permissions. |
| Hot-swappable auth logic | Add/replace authentication implementations without redeploying the host application. |

## 2. Features & Status

| Capability | Status | Notes |
| :--- | :--- | :--- |
| Authc extension point | Available | `AuthenticatingExtensionPoint extends ExtensionPoint`; selected via `ExtensionPointUtils.getAuthcPoint(...)`. |
| Authz extension point | Available | `AuthorizationExtensionPoint extends ExtensionPoint`. |
| Principal repository extension point | Available | `PrincipalRepositoryExtensionPoint extends ExtensionPoint, ShiroPrincipalRepository`. |
| Realm dispatch | Available | `AuthorizingRealmExtensionPoint` / `DefaultExtensionPointAuthorizingRealm` with plug-in/extension id resolution. |
| Authenticating filter | Available | `AbstractExtensionPointAuthenticatingFilter` / `DefaultExtensionPointAuthenticatingFilter` (login-url + submission handling). |
| Logout filter | Available | `AbstractExtensionPointLogoutFilter` / `DefaultExtensionPointLogoutFilter`. |
| Authorization filter | Available | `AbstracExtensionPointAuthorizationFilter` / `DefaultExtensionPointAuthorizationFilter`. |
| Mapping annotations | Available | `@AuthcMapping` / `@AuthzMapping` (`id()`, `title()`, `desc()`). |
| Token | Available | `ExtensionPointAuthenticationToken` (username/password/captcha/remember-me/host variants). |
| Exceptions | Available | `AuthcPluginNotFoundException`, `AuthcPointNotFoundException`, `AuthzPluginNotFoundException`, `AuthzPointNotFoundException`. |

> Status is reported as of `3.0.x.x.20260630-SNAPSHOT` on the `feature/3.0.x` branch.

## 3. Requirements & Compatibility

| Item | Version |
| :--- | :--- |
| JDK | 21+ |
| Maven | 3.0+ (Maven Wrapper 3.5.0 bundled) |
| PF4J | 3.6.0 (`org.pf4j:pf4j`) |
| easy4j dependency | `io.github.easy4j:shiro-biz` (`3.0.x.x.20260630-SNAPSHOT`) |
| Other | spring-core, commons-lang3, javax.servlet-api 4.0.1 |

**Version lines**

| Branch | JDK baseline | Version pattern |
| :--- | :--- | :--- |
| `feature/1.0.x` | JDK 8 | `1.0.x.*` |
| `feature/2.0.x` | JDK 17 | `2.0.x.*` |
| `feature/3.0.x` | JDK 21 | `3.0.x.*` |

## 4. Architecture & Modules

```text
 Request (plugin / extension id params)
        |
        v
 DefaultExtensionPointAuthenticatingFilter
        |  ExtensionPointUtils.getPluginId / getExtensionId
        v
 PluginManager (PF4J) --> AuthenticatingExtensionPoint (plug-in)
        |
        v
 DefaultExtensionPointAuthorizingRealm
        |  AuthorizingRealmExtensionPoint
        |    +-- PrincipalRepositoryExtensionPoint
        |    +-- AuthorizationExtensionPoint
        v
 Subject --> authorization filters (DefaultExtensionPointAuthorizationFilter)
        |
        +-- logout via DefaultExtensionPointLogoutFilter
```

This is a **single-module** project (packaging `jar`), classes under `org.apache.shiro.pf4j`:

| Package | Role |
| :--- | :--- |
| `annotation` | `@AuthcMapping`, `@AuthzMapping` |
| `authc.point` / `authc.token` / `authc.exception` | Authenticating extension point, token, plug-in/point exceptions |
| `authz.point` / `authc.exception` | Authorization and principal-repository extension points, exceptions |
| `realm` | `AuthorizingRealmExtensionPoint`, `DefaultExtensionPointAuthorizingRealm` |
| `utils` | `ExtensionPointUtils` |
| `web.filter.authc` / `web.filter.authz` | Authenticating / logout / authorization filters |

## 5. Installation

The artifact is not yet published to Maven Central. Resolve it from the project's configured artifact repository (Aliyun Packages) or install it locally from source; the snapshot version currently used on the `feature/3.0.x` branch is `3.0.x.x.20260630-SNAPSHOT`.

**Maven**

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>shiro-pf4j</artifactId>
    <version>3.0.x.x.20260630-SNAPSHOT</version>
</dependency>
```

**Gradle**

```groovy
implementation 'io.github.easy4j:shiro-pf4j:3.0.x.x.20260630-SNAPSHOT'
```

## 6. Quick Start

Wire the default extension-point realm and authenticating filter:

```java
import org.apache.shiro.pf4j.realm.DefaultExtensionPointAuthorizingRealm;
import org.apache.shiro.pf4j.web.filter.authc.DefaultExtensionPointAuthenticatingFilter;

// 1. Realm dispatching to plug-in extension points (injects your PluginManager)
DefaultExtensionPointAuthorizingRealm realm =
        new DefaultExtensionPointAuthorizingRealm();
realm.setPluginManager(pluginManager);
// request parameter names default to "plugin" / "extension"
realm.setPluginParamName("plugin");
realm.setExtensionParamName("extension");

// 2. Filter resolving plug-in/extension ids from request parameters
DefaultExtensionPointAuthenticatingFilter filter =
        new DefaultExtensionPointAuthenticatingFilter();
filter.setPluginManager(pluginManager);
filter.setPluginParamName("plugin");
filter.setExtensionParamName("extension");
filter.setLoginUrl("/login");
```

**Expected result:** for each request, the filter resolves the plug-in/extension identifiers from the request parameters, loads the matching `AuthenticatingExtensionPoint` from the `PluginManager`, and the realm authenticates/authorizes through the matching `AuthorizingRealmExtensionPoint`.

## 7. Configuration

This library has no configuration properties or prefix; behavior is configured programmatically:

| Extension point | Configurable via |
| :--- | :--- |
| Realm / filters | `setPluginManager(PluginManager)`, `setPluginParamName(String)`, `setExtensionParamName(String)` (request parameter names for plug-in / extension ids) |
| Filters | standard Shiro filter setters (`setLoginUrl`, ...) |
| Plug-in behavior | the PF4J `PluginManager` instance owned by the application |

## 8. Core Usage / API

| Class | Role |
| :--- | :--- |
| `AuthenticatingExtensionPoint` | Plug-in SPI for authentication; dispatch target of the authenticating filter. |
| `AuthorizationExtensionPoint` | Plug-in SPI for authorization. |
| `PrincipalRepositoryExtensionPoint` | Plug-in SPI extending `ShiroPrincipalRepository`. |
| `AuthorizingRealmExtensionPoint` / `DefaultExtensionPointAuthorizingRealm` | Realm that resolves plug-in/extension ids (`getPluginId`, `getExtensionId`) and dispatches to plug-in extensions. |
| `ExtensionPointUtils` | Static helpers: `getAuthcPoint(...)`, `getAuthzPoint(...)`, `getPluginId(...)`, `getExtensionId(...)`. |
| `ExtensionPointAuthenticationToken` | Token with username/password/captcha/remember-me/host variants. |
| `DefaultExtensionPointAuthenticatingFilter` / `DefaultExtensionPointLogoutFilter` / `DefaultExtensionPointAuthorizationFilter` | Request-routing web filters. |
| `@AuthcMapping` / `@AuthzMapping` | Annotations with `id()`, `title()`, `desc()` for extension-point documentation/mapping. |

## 9. Testing & Build

```bash
# Full build with tests and JaCoCo coverage report/check
./mvnw clean verify

# Run tests only
./mvnw test

# Install into the local repository
./mvnw install
```

Test & gate facts (as configured in the pom):

- One JUnit 4 test class exists: `org.apache.shiro.pf4j.LoginLogoutTest`, which drives a login/logout flow via `IniSecurityManagerFactory("classpath:shiro.ini")` with `ExtensionPointAuthenticationToken`. Note: the referenced `shiro.ini` fixture is not present in the test resources at this snapshot, so the test currently cannot pass as-is; in addition the pom's surefire configuration defaults to skipping tests.
- JaCoCo is bound to `prepare-agent` / `report` / `check`; the `check` rule requires a **90% line coverage ratio** (configured with `haltOnFailure=false`, i.e. reported rather than hard-failing).

## 10. Versioning & Branches

| Branch | JDK baseline | Version pattern | Status |
| :--- | :--- | :--- | :--- |
| `feature/1.0.x` | JDK 8 | `1.0.x.*` | Active; current snapshot `1.0.x.20260630-SNAPSHOT` |
| `feature/2.0.x` | JDK 17 | `2.0.x.*` | Maintained |
| `feature/3.0.x` | JDK 21 | `3.0.x.*` | Maintained |

Maintenance strategy: the 1.0.x line keeps JDK 8 compatibility for legacy deployments; the 2.0.x and 3.0.x lines are the modern JDK baselines. Release artifacts are published to the project's configured artifact repository (Aliyun Packages) and GitHub Releases; the project has not yet published to Maven Central.

## 11. Contributing & License

Contributions are welcome — please open an issue or a pull request on the [GitHub repository](https://github.com/easy-4-java/shiro-pf4j).

This project is licensed under the **Apache License 2.0**. See [LICENSE](LICENSE) for details.
