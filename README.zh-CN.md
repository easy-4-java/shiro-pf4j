# shiro-pf4j

[English](./README.md) | [简体中文](./README.zh-CN.md)

[![Java](https://img.shields.io/badge/Java-21-orange)](https://github.com/easy-4-java/shiro-pf4j) [![License](https://img.shields.io/badge/license-Apache%202.0-green)](./LICENSE)

Shiro 的 PF4J 扩展——基于 [PF4J](https://pf4j.org/)（插件框架）实现可插拔的 Shiro 认证与授权。认证、授权与主体仓库行为可由插件提供，并按请求中的插件/扩展标识动态选择。

## 目录

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

**是什么**

`shiro-pf4j` 让插件扩展 Shiro 的认证与授权：

- **扩展点**：`AuthenticatingExtensionPoint`、`AuthorizationExtensionPoint`、`PrincipalRepositoryExtensionPoint`（继承 `shiro-extension` 的 `ShiroPrincipalRepository`）——由插件实现，通过 PF4J `PluginManager` 发现。
- **Realm**：`AuthorizingRealmExtensionPoint` / `DefaultExtensionPointAuthorizingRealm` 根据请求中的插件/扩展标识，将认证与授权分派给匹配的扩展点。
- **过滤器**：`AbstractExtensionPointAuthenticatingFilter` / `DefaultExtensionPointAuthenticatingFilter`、登出过滤器，以及 `AbstracExtensionPointAuthorizationFilter` / `DefaultExtensionPointAuthorizationFilter`，将每个请求路由到正确的插件扩展。
- **映射注解**：`@AuthcMapping` / `@AuthzMapping`（id、title、desc）标注认证/授权扩展点。
- `ExtensionPointUtils` 从请求参数（默认 `plugin` / `extension`）解析插件/扩展 id。

**不是什么**

- 它不是 PF4J 引导库——`PluginManager`（插件目录、类加载策略）由你的应用持有，通过 `setPluginManager(...)` 注入。
- 它不是 Spring Boot Starter；不随包提供自动配置。

**典型场景**

| 场景 | 说明 |
| :--- | :--- |
| 多租户认证 | 每个租户提供暴露 `AuthenticatingExtensionPoint` 的插件；过滤器按请求参数选择扩展。 |
| 插件化授权 | 插件通过 `AuthorizationExtensionPoint` / `PrincipalRepositoryExtensionPoint` 提供角色与权限。 |
| 热切换认证逻辑 | 无需重新部署宿主应用即可增删/替换认证实现。 |

## 2. Features & Status

| 能力 | 状态 | 说明 |
| :--- | :--- | :--- |
| 认证扩展点 | 可用 | `AuthenticatingExtensionPoint extends ExtensionPoint`；通过 `ExtensionPointUtils.getAuthcPoint(...)` 选择。 |
| 授权扩展点 | 可用 | `AuthorizationExtensionPoint extends ExtensionPoint`。 |
| 主体仓库扩展点 | 可用 | `PrincipalRepositoryExtensionPoint extends ExtensionPoint, ShiroPrincipalRepository`。 |
| Realm 分派 | 可用 | `AuthorizingRealmExtensionPoint` / `DefaultExtensionPointAuthorizingRealm`，支持插件/扩展 id 解析。 |
| 认证过滤器 | 可用 | `AbstractExtensionPointAuthenticatingFilter` / `DefaultExtensionPointAuthenticatingFilter`（登录 URL + 提交处理）。 |
| 登出过滤器 | 可用 | `AbstractExtensionPointLogoutFilter` / `DefaultExtensionPointLogoutFilter`。 |
| 授权过滤器 | 可用 | `AbstracExtensionPointAuthorizationFilter` / `DefaultExtensionPointAuthorizationFilter`。 |
| 映射注解 | 可用 | `@AuthcMapping` / `@AuthzMapping`（`id()`、`title()`、`desc()`）。 |
| Token | 可用 | `ExtensionPointAuthenticationToken`（用户名/密码/验证码/记住我/主机多种变体）。 |
| 异常 | 可用 | `AuthcPluginNotFoundException`、`AuthcPointNotFoundException`、`AuthzPluginNotFoundException`、`AuthzPointNotFoundException`。 |

> 状态以 `feature/3.0.x` 分支上的 `3.0.x.x.20260630-SNAPSHOT` 为准。

## 3. Requirements & Compatibility

| 项目 | 版本 |
| :--- | :--- |
| JDK | 21+ |
| Maven | 3.0+（内置 Maven Wrapper 3.5.0） |
| PF4J | 3.6.0（`org.pf4j:pf4j`） |
| easy4j 依赖 | `io.github.easy4j:shiro-extension`（`3.0.x.x.20260630-SNAPSHOT`） |
| 其他 | spring-core、commons-lang3、javax.servlet-api 4.0.1 |

**版本线**

| 分支 | JDK 基线 | 版本模式 |
| :--- | :--- | :--- |
| `feature/1.0.x` | JDK 8 | `1.0.x.*` |
| `feature/2.0.x` | JDK 17 | `2.0.x.*` |
| `feature/3.0.x` | JDK 21 | `3.0.x.*` |

## 4. Architecture & Modules

```text
 请求（plugin / extension id 参数）
        |
        v
 DefaultExtensionPointAuthenticatingFilter
        |  ExtensionPointUtils.getPluginId / getExtensionId
        v
 PluginManager（PF4J）--> AuthenticatingExtensionPoint（插件）
        |
        v
 DefaultExtensionPointAuthorizingRealm
        |  AuthorizingRealmExtensionPoint
        |    +-- PrincipalRepositoryExtensionPoint
        |    +-- AuthorizationExtensionPoint
        v
 Subject --> 授权过滤器（DefaultExtensionPointAuthorizationFilter）
        |
        +-- 登出经由 DefaultExtensionPointLogoutFilter
```

本项目为**单模块**工程（packaging 为 `jar`），类位于 `org.apache.shiro.pf4j`：

| 包 | 职责 |
| :--- | :--- |
| `annotation` | `@AuthcMapping`、`@AuthzMapping` |
| `authc.point` / `authc.token` / `authc.exception` | 认证扩展点、Token、插件/扩展点异常 |
| `authz.point` / `authc.exception` | 授权与主体仓库扩展点、异常 |
| `realm` | `AuthorizingRealmExtensionPoint`、`DefaultExtensionPointAuthorizingRealm` |
| `utils` | `ExtensionPointUtils` |
| `web.filter.authc` / `web.filter.authz` | 认证 / 登出 / 授权过滤器 |

## 5. Installation

该构件尚未发布到 Maven Central。请从项目配置的制品仓库（阿里云制品仓库）获取，或从源码本地安装；`feature/3.0.x` 分支当前使用的快照版本为 `3.0.x.x.20260630-SNAPSHOT`。

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

装配默认扩展点 Realm 与认证过滤器：

```java
import org.apache.shiro.pf4j.realm.DefaultExtensionPointAuthorizingRealm;
import org.apache.shiro.pf4j.web.filter.authc.DefaultExtensionPointAuthenticatingFilter;

// 1. 分派到插件扩展点的 Realm（注入你的 PluginManager）
DefaultExtensionPointAuthorizingRealm realm =
        new DefaultExtensionPointAuthorizingRealm();
realm.setPluginManager(pluginManager);
// 请求参数名默认 "plugin" / "extension"
realm.setPluginParamName("plugin");
realm.setExtensionParamName("extension");

// 2. 从请求参数解析插件/扩展 id 的过滤器
DefaultExtensionPointAuthenticatingFilter filter =
        new DefaultExtensionPointAuthenticatingFilter();
filter.setPluginManager(pluginManager);
filter.setPluginParamName("plugin");
filter.setExtensionParamName("extension");
filter.setLoginUrl("/login");
```

**预期结果：** 每个请求由过滤器从请求参数解析插件/扩展标识，从 `PluginManager` 加载匹配的 `AuthenticatingExtensionPoint`，Realm 通过匹配的 `AuthorizingRealmExtensionPoint` 完成认证与授权。

## 7. Configuration

本库没有配置属性与前缀，采用编程方式配置：

| 扩展点 | 配置方式 |
| :--- | :--- |
| Realm / 过滤器 | `setPluginManager(PluginManager)`、`setPluginParamName(String)`、`setExtensionParamName(String)`（插件/扩展 id 的请求参数名，默认 `plugin` / `extension`） |
| 过滤器 | 标准 Shiro 过滤器 setter（`setLoginUrl` 等） |
| 插件行为 | 应用持有的 PF4J `PluginManager` 实例 |

## 8. Core Usage / API

| 类 | 职责 |
| :--- | :--- |
| `AuthenticatingExtensionPoint` | 认证插件 SPI；认证过滤器的分派目标。 |
| `AuthorizationExtensionPoint` | 授权插件 SPI。 |
| `PrincipalRepositoryExtensionPoint` | 继承 `ShiroPrincipalRepository` 的插件 SPI。 |
| `AuthorizingRealmExtensionPoint` / `DefaultExtensionPointAuthorizingRealm` | 解析插件/扩展 id（`getPluginId`、`getExtensionId`）并分派到插件扩展的 Realm。 |
| `ExtensionPointUtils` | 静态工具：`getAuthcPoint(...)`、`getAuthzPoint(...)`、`getPluginId(...)`、`getExtensionId(...)`。 |
| `ExtensionPointAuthenticationToken` | 支持用户名/密码/验证码/记住我/主机变体的 Token。 |
| `DefaultExtensionPointAuthenticatingFilter` / `DefaultExtensionPointLogoutFilter` / `DefaultExtensionPointAuthorizationFilter` | 请求路由 Web 过滤器。 |
| `@AuthcMapping` / `@AuthzMapping` | 携带 `id()`、`title()`、`desc()` 的扩展点标注/映射注解。 |

## 9. Testing & Build

```bash
# 完整构建（含测试与 JaCoCo 覆盖率报告/检查）
./mvnw clean verify

# 仅运行测试
./mvnw test

# 安装到本地仓库
./mvnw install
```

测试与门禁事实（以 pom 配置为准）：

- 存在一个 JUnit 4 测试类 `org.apache.shiro.pf4j.LoginLogoutTest`，通过 `IniSecurityManagerFactory("classpath:shiro.ini")` 配合 `ExtensionPointAuthenticationToken` 驱动登录/登出流程。注意：测试引用的 `shiro.ini` 夹具在本快照的测试资源中并不存在，因此该测试当前无法按原样通过；另外 pom 的 surefire 配置默认跳过测试。
- JaCoCo 绑定 `prepare-agent` / `report` / `check`；`check` 规则要求**行覆盖率不低于 90%**（配置了 `haltOnFailure=false`，即仅报告而不强制失败）。

## 10. Versioning & Branches

| 分支 | JDK 基线 | 版本模式 | 状态 |
| :--- | :--- | :--- | :--- |
| `feature/1.0.x` | JDK 8 | `1.0.x.*` | 活跃；当前快照 `1.0.x.20260630-SNAPSHOT` |
| `feature/2.0.x` | JDK 17 | `2.0.x.*` | 维护中 |
| `feature/3.0.x` | JDK 21 | `3.0.x.*` | 维护中 |

维护策略：1.0.x 版本线保持 JDK 8 兼容，服务于存量部署；2.0.x 与 3.0.x 版本线为现代 JDK 基线。发布制品发布到项目配置的制品仓库（阿里云制品仓库）与 GitHub Releases；项目尚未发布到 Maven Central。

## 11. Contributing & License

欢迎参与贡献——请在 [GitHub 仓库](https://github.com/easy-4-java/shiro-pf4j) 提交 Issue 或 Pull Request。

本项目基于 **Apache License 2.0** 开源。详见 [LICENSE](LICENSE)。
