# Shiro 3.0.2 适配契约与验收

本次变更以用户确认的版本矩阵为事实源，沿用现有分支，不安装或初始化新的规格工具。

| 分支 | Shiro | Java | Jackson | Servlet | Spring Framework |
| --- | --- | --- | --- | --- | --- |
| feature/2.0.x | 3.0.2 | 17 | 2 | Jakarta | 6.2 |
| feature/3.0.x | 3.0.2 | 21 | 3 | Jakarta | 7.0 |

当前仓库：`shiro-pf4j`；当前版本线：`feature/3.0.x`。

## 范围与兼容性

- 所有 Shiro 模块必须解析到 3.0.2，不混用 1.x/2.x 或 Jakarta classifier。
- 同系列 easy4j 公共组件使用当前版本线，禁止 2.0.x 引用 3.0.x。
- Servlet 接口迁移到 jakarta.servlet；不替换 JDK 中仍合法的 javax.crypto、javax.naming 等命名空间。
- 2.0.x 保留 Jackson 2 API；3.0.x 使用 tools.jackson，Jackson 注解仍使用 com.fasterxml.jackson.annotation。
- 保留认证、授权、缓存及会话语义，外部 CAS/LDAP/微信等协议不以桩实现代替。
- 不修改 feature/1.0.x，不提交、推送或发布，不覆盖已有用户修改。

## 执行与验收

- [ ] 核对 Maven 有效模型、依赖树、JDK 和分支。
- [x] 对齐 Shiro、Jackson、Spring、Servlet 和公共组件的依赖声明（传递依赖树尚未验证）。
- [x] 使用目标 JDK 调用 Maven 测试流程，记录实际失败及阻塞。
- [ ] 依据编译及回归证据修正源码和测试中的接口兼容问题。
- [ ] 使用目标 JDK 运行全部现有测试，记录测试数、失败与跳过。
- [x] 静态检查没有残留 javax.servlet 导入或错误 Jackson 主版本声明；旧第三方桥接的反射类名保留。
- [ ] 对没有自动化测试、依赖缺失或外部服务未验证项明确保留未完成状态。

## 证据与状态

状态：适配进行中。编译成功、依赖静态检查或零测试执行均不能单独证明适配完成。

参考：[Shiro 迁移指南](https://shiro.apache.org/migration-guide.html)、[Jackson 3 迁移指南](https://github.com/FasterXML/jackson/blob/main/jackson3/MIGRATING_TO_JACKSON_3.md)。

## 本次执行证据（2026-11-17）

声明/导入静态检查：**通过**；分支：`feature/3.0.x`；目标 Java：`21`。

Maven 测试流程退出码：`1`。完整日志：[maven-test.log](evidence/maven-test.log)；结构化结果：[verification.json](evidence/verification.json)。


已补齐 Servlet 6 请求/响应测试替身的接口方法，移除已删除的覆盖方法；请求替身以及从真实测试提取的匿名响应对象均通过目标 JDK 独立 javac 检查。这不代表整个 PF4J 测试类或测试套件通过。

当前执行环境无法解析 Maven Central 域名，且所需缓存不完整。公共 shiro-extension 的依赖/本地 SNAPSHOT 父 POM 缺失使大量下游模块尚未进入编译；不得把此状态判为适配成功。

## 2026-11-18 全量矩阵复验

`mvn clean verify` 与 Maven 依赖树通过。现有测试 72 项，失败 0，错误 0，跳过 0。

[Maven 验证日志与依赖树](evidence/maven-verify-shiro-3.0.2.log)。
