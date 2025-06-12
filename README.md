
# **wenziyue-security-starter**

wenziyue-security-starter 是一个基于 Spring Security 封装的通用安全模块 Starter，适用于 Spring Boot 项目。它通过自动配置快速集成基础登录逻辑、权限校验，支持灵活扩展默认登录服务，帮助你专注于业务开发。

------

## **✨ 功能特点**

- ✅ 集成 Spring Security，默认开启安全控制
- ✅ 提供基础的登录认证流程接口 LoginService
- ✅ 支持基于用户 ID 加载用户信息（UserDetailsServiceById）
- ✅ 支持通过刷新令牌刷新登录状态缓存（RefreshCacheByRefreshToken）
- ✅ 默认实现可被业务项目自定义替换
- ✅ Starter 自动配置，开箱即用

------

## **🛠️ 快速开始**

### **1. 引入依赖**

首先在settings.xml中添加以下认证信息
```xml
<server>
    <id>wenziyue-security</id>
    <username>你的GitHub用户名</username>
    <password>你的GitHub Token（建议只赋予 read:packages 权限）</password>
</server>
```

再在 `pom.xml` 中添加 GitHub 仓库地址：

```xml
<!-- pom.xml 中添加仓库地址（id 要与上面保持一致） -->
<repositories>
    <repository>
        <id>wenziyue-security</id>
        <url>https://maven.pkg.github.com/wenziyue1984/wenziyue-security-starter</url>
    </repository>
</repositories>
```
然后引入依赖：

```xml
<dependency>
    <groupId>com.wenziyue</groupId>
    <artifactId>wenziyue-security-starter</artifactId>
    <version>1.0.0（请使用最新版本）</version>
</dependency>
```

> 💡 注意：你需要在 Maven 的 `settings.xml` 中配置 GitHub Token 授权，才能访问私有或 GitHub Packages 的依赖。

### **2. 自动启用**

引入后无需额外配置，Spring Boot 会自动加载该 Starter 中的配置类，注册默认组件（可被业务方覆盖）。

### **3. 配置示例**
```yaml
wenziyue:
  security:
    default-login-enabled: false  # 是否开启默认登录接口，默认false（默认登录接口只适用于简单的demo或者测试使用，请勿用于生产环境）
    token-header: Authorization  # JWT 的请求头名称，默认 Authorization
    refreshTokenHeader: X-Refresh-Token  # 刷新token时返回的header头
    expire: 604800 # token过期时间，秒，默认7天
    refresh-before-expiration: 86400  # 还剩多少时间刷新token时间，秒，默认1天
    white-list:  # 白名单，不进行权限校验
      - "/user/**"
```
关于白名单，默认放行以下接口：
```yaml
"/login",
"/refresh",
"/register",
"/doc.html",
"/swagger-ui/**",
"/swagger-resources/**",
"/v3/api-docs/**",
"/swagger-ui.html",
"/webjars/swagger-ui/**"
```

------

## **🔧 自定义扩展**
> 如果启用默认登录接口，你可以进行以下扩展

### **1. 自定义登录逻辑（LoginService）**

实现并注册一个自定义 LoginService Bean 来替换默认登录逻辑，不然只能用admin/123456登录：

```java
@Service
public class MyLoginService implements LoginService {
    @Override
    public void login(String username, String password) {
        // 校验用户名密码、自定义认证逻辑
    }
}
```

------

### **2. 加载用户信息（UserDetailsServiceById）**

如果你的系统中存在用户缓存或 JWT 机制，可能需要根据用户 ID 加载完整用户信息。只需实现并注入 UserDetailsServiceById 接口：

```java
@Service
public class MyUserDetailsServiceById implements UserDetailsServiceById {
    @Override
    public UserDetails loadUserById(String userId) {
        // 根据 userId 从数据库或缓存中查询用户信息
        return new MyUserDetails(...);
    }
}
```

> 用于支持如 JWT Token 中只携带 userId，需要还原完整认证用户的场景。

------





### **3. 刷新登录状态缓存（RefreshCacheByRefreshToken）**

Starter 提供了 RefreshCacheByRefreshToken 接口，可用于处理 Refresh Token 机制。实现后可通过 RefreshToken 刷新用户登录状态（例如刷新缓存中的用户信息）：

```java
@Service
public class MyRefreshCacheByRefreshToken implements RefreshCacheByRefreshToken {
    @Override
    public void refreshCache(String refreshToken) {
        // 验证 refreshToken 是否有效，刷新相关缓存（如 Redis 中的 token 信息）
    }
}
```

------

## **📦 模块结构说明**

```
wenziyue-security-starter
├── config/
│   └── SecurityConfiguration.java     # 安全配置（含 SecurityFilterChain）
├── service/
│   ├── LoginService.java              # 登录接口
│   ├── DefaultLoginService.java       # 默认登录实现
│   ├── UserDetailsServiceById.java    # 根据用户ID加载用户详情
│   └── RefreshCacheByRefreshToken.java # 刷新缓存接口
├── WenziyueSecurityAutoConfiguration.java  # Starter 自动配置类
└── META-INF/spring.factories               # Spring Boot 自动装配入口
```

------

## **📄 版本说明**

- 要求：Spring Boot 2.7.18，JDK 8

------

## **🔗 示例项目**

你可以在 [wenziyue-blog](https://github.com/wenziyue1984/wenziyue-blog) 项目中查看该 Starter 的使用方式与集成示例。

------

## **📬 联系作者**

如有建议或问题，欢迎提 Issue 或联系作者 😊
