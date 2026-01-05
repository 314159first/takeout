# 苍穹外卖项目代码说明文档

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MyBatis](https://img.shields.io/badge/MyBatis-2.2.0-red.svg)](https://mybatis.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

---

## 📑 目录

- [项目概述](#-项目概述)
- [项目整体架构](#️-项目整体架构)
- [模块详细说明](#-模块详细说明)
  - [sky-common（公共模块）](#️⃣-sky-common公共模块)
  - [sky-pojo（实体类模块）](#️⃣-sky-pojo实体类模块)
  - [sky-server（服务模块）](#️⃣-sky-server服务模块)
- [核心技术组件说明](#-核心技术组件说明)
- [数据库设计](#-数据库设计推测)
- [关键配置说明](#-关键配置说明)
- [主要技术栈版本](#-主要技术栈版本)
- [项目功能模块](#-项目功能模块)
- [项目启动流程](#-项目启动流程)
- [代码规范与约定](#-代码规范与约定)
- [常见问题说明](#-常见问题说明)
- [开发调试指南](#️-开发调试指南)
- [学习建议](#-学习建议)
- [项目亮点](#-项目亮点)
- [总结](#-总结)

---

## 📋 项目概述

**项目名称**：sky-take-out（苍穹外卖）  
**项目类型**：基于 Spring Boot 的外卖管理系统  
**技术栈**：Spring Boot 2.7.3 + MyBatis + MySQL + Redis + JWT + Knife4j  
**架构模式**：Maven 多模块项目

---

## 🏗️ 项目整体架构

项目采用 **Maven 多模块架构**，分为以下三个子模块：

```
sky-take-out (父工程)
├── sky-common   (公共模块)
├── sky-pojo     (实体类模块)
└── sky-server   (服务模块)
```

### 模块依赖关系图

```
┌─────────────────────────────────────┐
│      sky-take-out (父工程)           │
│   管理版本、依赖、构建配置             │
└─────────────────────────────────────┘
             │
      ┌──────┴──────┬────────────┐
      │             │            │
┌─────▼──────┐ ┌───▼─────┐ ┌───▼────────┐
│ sky-common │ │sky-pojo │ │ sky-server │
│  公共工具   │ │ 实体类  │ │  业务逻辑   │
└────────────┘ └─────────┘ └────────────┘
      ▲             ▲            │
      └─────────────┴────────────┘
           sky-server 依赖两者
```

---

## 📦 模块详细说明

### 1️⃣ sky-common（公共模块）

**作用**：提供项目中所有通用的工具类、常量、异常处理、配置属性等。

#### 📂 包结构

| 包名 | 说明 | 主要类 |
|------|------|--------|
| `constant` | 常量定义 | `MessageConstant`（错误信息）<br>`StatusConstant`（状态常量）<br>`JwtClaimsConstant`（JWT声明常量）<br>`PasswordConstant`（密码常量）<br>`AutoFillConstant`（自动填充常量） |
| `context` | 上下文管理 | `BaseContext`（ThreadLocal存储当前用户ID） |
| `enumeration` | 枚举类 | `OperationType`（操作类型枚举：INSERT/UPDATE） |
| `exception` | 自定义异常 | `BaseException`（基础异常类）<br>`AccountNotFoundException`（账号不存在）<br>`PasswordErrorException`（密码错误）<br>`OrderBusinessException`（订单业务异常）<br>等12个业务异常类 |
| `json` | JSON处理 | `JacksonObjectMapper`（自定义JSON序列化配置） |
| `properties` | 配置属性 | `JwtProperties`（JWT配置）<br>`AliOssProperties`（阿里云OSS配置）<br>`WeChatProperties`（微信配置） |
| `result` | 统一返回结果 | `Result<T>`（统一响应对象）<br>`PageResult`（分页结果对象） |
| `utils` | 工具类 | `JwtUtil`（JWT生成与解析）<br>`AliOssUtil`（阿里云OSS文件上传）<br>`HttpClientUtil`（HTTP请求工具）<br>`WeChatPayUtil`（微信支付工具） |

#### 🔑 核心类解析

**1. Result<T> - 统一返回结果**
```java
@Data
public class Result<T> implements Serializable {
    private Integer code;  // 1=成功，0=失败
    private String msg;    // 错误信息
    private T data;        // 返回数据
}
```

**2. BaseContext - 线程上下文**
```java
// 使用 ThreadLocal 存储当前登录用户ID，避免层层传递
public class BaseContext {
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    public static void setCurrentId(Long id) { ... }
    public static Long getCurrentId() { ... }
}
```

**3. JwtUtil - JWT工具**
- `createJWT()`：生成JWT令牌
- `parseJWT()`：解析JWT令牌

---

### 2️⃣ sky-pojo（实体类模块）

**作用**：定义数据传输对象（DTO）、实体类（Entity）、视图对象（VO）。

#### 📂 包结构

| 包名 | 说明 | 典型类 |
|------|------|--------|
| `dto` | 数据传输对象<br>（客户端→服务端） | `EmployeeDTO`（员工信息）<br>`EmployeeLoginDTO`（员工登录）<br>`DishDTO`（菜品信息）<br>`OrdersSubmitDTO`（订单提交）<br>等20个DTO类 |
| `entity` | 数据库实体类 | `Employee`（员工）<br>`Dish`（菜品）<br>`Setmeal`（套餐）<br>`Orders`（订单）<br>`User`（用户）<br>`Category`（分类）<br>等11个实体类 |
| `vo` | 视图对象<br>（服务端→客户端） | `EmployeeLoginVO`（登录返回）<br>`DishVO`（菜品详情）<br>`OrderVO`（订单详情）<br>`BusinessDataVO`（营业数据）<br>等13个VO类 |

#### 🔍 三种对象的区别

| 类型 | 用途 | 示例 |
|------|------|------|
| **DTO** | 接收前端请求参数 | `EmployeeDTO` - 新增/修改员工时传递的数据 |
| **Entity** | 映射数据库表 | `Employee` - 对应数据库 `employee` 表 |
| **VO** | 返回给前端的数据 | `EmployeeLoginVO` - 登录成功后返回（包含token） |

#### 📝 实体类示例

**Employee（员工实体）**
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee implements Serializable {
    private Long id;
    private String username;      // 用户名
    private String name;          // 姓名
    private String password;      // 密码
    private String phone;         // 手机号
    private String sex;           // 性别
    private String idNumber;      // 身份证号
    private Integer status;       // 状态：1启用 0禁用
    private LocalDateTime createTime;   // 创建时间
    private LocalDateTime updateTime;   // 更新时间
    private Long createUser;      // 创建人ID
    private Long updateUser;      // 更新人ID
}
```

**Orders（订单实体）**
```java
@Data
public class Orders implements Serializable {
    // 订单状态常量
    public static final Integer PENDING_PAYMENT = 1;      // 待付款
    public static final Integer TO_BE_CONFIRMED = 2;      // 待接单
    public static final Integer CONFIRMED = 3;            // 已接单
    public static final Integer DELIVERY_IN_PROGRESS = 4; // 派送中
    public static final Integer COMPLETED = 5;            // 已完成
    public static final Integer CANCELLED = 6;            // 已取消
    
    // 支付状态常量
    public static final Integer UN_PAID = 0;   // 未支付
    public static final Integer PAID = 1;      // 已支付
    public static final Integer REFUND = 2;    // 退款
    
    private Long id;
    // ...其他字段
}
```

---

### 3️⃣ sky-server（服务模块）

**作用**：核心业务逻辑层，包含控制器、服务、数据访问等。

#### 📂 包结构

| 包名 | 说明 | 主要内容 |
|------|------|----------|
| `config` | 配置类 | `WebMvcConfiguration`（Web配置、拦截器注册、Knife4j配置） |
| `controller` | 控制器层 | `admin/`（管理端控制器）<br>- `EmployeeController`（员工管理） |
| `service` | 服务接口层 | `EmployeeService`（员工服务接口） |
| `service.impl` | 服务实现层 | `EmployeeServiceImpl`（员工服务实现） |
| `mapper` | 数据访问层 | `EmployeeMapper`（MyBatis Mapper接口） |
| `handler` | 异常处理器 | `GlobalExceptionHandler`（全局异常处理） |
| `interceptor` | 拦截器 | `JwtTokenAdminInterceptor`（JWT令牌校验拦截器） |

#### 🔄 业务流程分层

```
┌─────────────────────────────────────────┐
│  客户端（前端/移动端）                    │
└─────────────┬───────────────────────────┘
              │ HTTP请求
┌─────────────▼───────────────────────────┐
│  Controller 层（控制器）                  │
│  - 接收请求参数                           │
│  - 参数校验                               │
│  - 调用Service层                          │
│  - 返回统一结果 Result<T>                 │
└─────────────┬───────────────────────────┘
              │
┌─────────────▼───────────────────────────┐
│  Service 层（业务逻辑）                   │
│  - 业务规则校验                           │
│  - 事务管理                               │
│  - 调用Mapper层                           │
└─────────────┬───────────────────────────┘
              │
┌─────────────▼───────────────────────────┐
│  Mapper 层（数据访问）                    │
│  - MyBatis SQL映射                       │
│  - 与数据库交互                           │
└─────────────┬───────────────────────────┘
              │
┌─────────────▼───────────────────────────┐
│  Database（MySQL数据库）                  │
└─────────────────────────────────────────┘
```

#### 🎯 典型业务流程示例：员工登录

**1. Controller层（EmployeeController.java）**
```java
@RestController
@RequestMapping("/admin/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO dto) {
        // 1. 调用Service层执行登录
        Employee employee = employeeService.login(dto);
        
        // 2. 生成JWT令牌
        String token = JwtUtil.createJWT(...);
        
        // 3. 构造返回对象
        EmployeeLoginVO vo = EmployeeLoginVO.builder()
            .id(employee.getId())
            .token(token)
            .build();
        
        // 4. 返回统一结果
        return Result.success(vo);
    }
}
```

**2. Service层（EmployeeServiceImpl.java）**
```java
@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;
    
    public Employee login(EmployeeLoginDTO dto) {
        // 1. 根据用户名查询数据库
        Employee employee = employeeMapper.getByUsername(dto.getUsername());
        
        // 2. 校验账号是否存在
        if (employee == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        
        // 3. 校验密码
        String password = DigestUtils.md5DigestAsHex(dto.getPassword().getBytes());
        if (!password.equals(employee.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        
        // 4. 校验账号状态
        if (employee.getStatus() == StatusConstant.DISABLE) {
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }
        
        return employee;
    }
}
```

**3. Mapper层（EmployeeMapper.java + EmployeeMapper.xml）**
```java
@Mapper
public interface EmployeeMapper {
    Employee getByUsername(String username);
}
```

```xml
<!-- EmployeeMapper.xml -->
<select id="getByUsername" resultType="Employee">
    SELECT * FROM employee WHERE username = #{username}
</select>
```

---

## 🔧 核心技术组件说明

### 1. JWT 认证机制

**流程**：
```
1. 用户登录 → 验证成功 → 生成JWT令牌 → 返回给客户端
2. 后续请求 → 携带JWT令牌（Header: token）
3. 拦截器拦截 → 解析JWT → 提取用户ID → 存入ThreadLocal
4. 业务层通过 BaseContext.getCurrentId() 获取当前用户ID
```

**相关类**：
- `JwtUtil`：生成和解析JWT
- `JwtTokenAdminInterceptor`：拦截器校验JWT（管理端）
- `JwtTokenUserInterceptor`：拦截器校验JWT（用户端）
- `BaseContext`：存储当前请求用户ID

**配置说明**：
- 管理端令牌名称：`token`
- 用户端令牌名称：`authentication`
- JWT过期时间：7200000毫秒（2小时）

---

### 2. AOP 自动填充功能

**功能说明**：使用 AOP 切面自动填充公共字段（创建时间、更新时间、创建人、更新人）。

**核心类**：
- `@Autofill` 注解：标识需要自动填充的方法
- `AutoFillAspect`：切面类，拦截带有 @Autofill 注解的方法

**使用示例**：
```java
@Mapper
public interface EmployeeMapper {
    @AutoFill(OperationType.INSERT)
    void insert(Employee employee);
    
    @AutoFill(OperationType.UPDATE)
    void update(Employee employee);
}
```

**填充字段**：
- INSERT 操作：自动设置 createTime、updateTime、createUser、updateUser
- UPDATE 操作：自动设置 updateTime、updateUser

---

### 4. 全局异常处理

**GlobalExceptionHandler.java**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // 捕获自定义业务异常
    @ExceptionHandler(BaseException.class)
    public Result exceptionHandler(BaseException ex) {
        return Result.error(ex.getMessage());
    }
    
    // 捕获SQL异常
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        // 处理唯一索引冲突等
        return Result.error("数据重复");
    }
}
```

---

### 5. 统一返回结果封装

所有接口返回 `Result<T>` 对象：

```json
// 成功示例
{
  "code": 1,
  "msg": null,
  "data": {
    "id": 1,
    "username": "admin",
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}

// 失败示例
{
  "code": 0,
  "msg": "账号不存在",
  "data": null
}
```

---

### 6. MyBatis 配置

**application.yml**
```yaml
mybatis:
  mapper-locations: classpath:mapper/*.xml  # Mapper XML文件位置
  type-aliases-package: com.sky.entity      # 实体类包路径
  configuration:
    map-underscore-to-camel-case: true      # 开启驼峰命名转换
```

**日志配置**：
```yaml
logging:
  level:
    com.sky.mapper: debug     # Mapper层打印SQL语句
    com.sky.service: info     # Service层日志级别
    com.sky.controller: info  # Controller层日志级别
```

---

### 7. Redis 缓存配置

**作用**：用于缓存菜品数据、店铺营业状态等高频访问数据，提高系统性能。

**配置**：
```yaml
spring:
  redis:
    host: ${sky.redis.host}
    port: ${sky.redis.port}
    password: ${sky.redis.password}
    database: ${sky.redis.database}
```

---

### 8. 拦截器配置

**WebMvcConfiguration.java**
```java
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {
    
    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;
    
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")          // 拦截所有管理端接口
                .excludePathPatterns("/admin/employee/login");  // 排除登录接口
    }
}
```

---

### 8. 拦截器配置

**WebMvcConfiguration.java**
```java
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {
    
    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;
    
    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;
    
    protected void addInterceptors(InterceptorRegistry registry) {
        // 管理端拦截器
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")          // 拦截所有管理端接口
                .excludePathPatterns("/admin/employee/login");  // 排除登录接口
        
        // 用户端拦截器
        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/**")           // 拦截所有用户端接口
                .excludePathPatterns("/user/user/login", "/user/shop/status");  // 排除登录和店铺状态接口
    }
}
```

---

### 9. 事务管理

项目启用了声明式事务管理：

```java
@SpringBootApplication
@EnableTransactionManagement //开启注解方式的事务管理
public class SkyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkyApplication.class, args);
    }
}
```

在需要事务的 Service 方法上添加 `@Transactional` 注解即可。

---

## 📊 数据库设计（推测）

根据实体类推测数据库表结构：

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `employee` | 员工表 | id, username, password, name, phone, status |
| `user` | 用户表 | id, openid, name, phone, avatar |
| `category` | 分类表 | id, type, name, sort |
| `dish` | 菜品表 | id, name, category_id, price, image, status |
| `dish_flavor` | 菜品口味表 | id, dish_id, name, value |
| `setmeal` | 套餐表 | id, name, category_id, price, image, status |
| `setmeal_dish` | 套餐菜品关系表 | id, setmeal_id, dish_id, copies |
| `orders` | 订单表 | id, number, status, user_id, amount |
| `order_detail` | 订单明细表 | id, order_id, dish_id, amount |
| `shopping_cart` | 购物车表 | id, user_id, dish_id, setmeal_id, amount |
| `address_book` | 地址簿表 | id, user_id, consignee, phone, detail |

---

## 🔐 关键配置说明

### application.yml（主配置）

```yaml
server:
  port: 8080

spring:
  profiles:
    active: dev  # 激活开发环境配置
  datasource:
    druid:
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3306/sky_take_out?serverTimezone=Asia/Shanghai
      username: root
      password: 123456
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.sky.entity
  configuration:
    map-underscore-to-camel-case: true

logging:
  level:
    com.sky.mapper: debug

sky:
  jwt:
    admin-secret-key: itcast
    admin-ttl: 7200000
    admin-token-name: token
    user-secret-key: itheima
    user-ttl: 7200000
    user-token-name: authentication
```

---

### application-dev.yml（开发环境配置）

```yaml
sky:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    host: localhost
    port: 3306
    database: sky_take_out
    username: root
    password: 123456
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
  alioss:
    endpoint: oss-cn-hangzhou.aliyuncs.com
    access-key-id: your-access-key-id
    access-key-secret: your-access-key-secret
    bucket-name: your-bucket-name
  wechat:
    appid: your-wechat-appid
    appsecret: your-wechat-appsecret
```

---

## 📚 主要技术栈版本

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 2.7.3 | 基础框架 |
| MyBatis | 2.2.0 | ORM框架 |
| Lombok | 1.18.30 | 简化代码 |
| Druid | 1.2.1 | 数据库连接池 |
| PageHelper | 1.3.0 | 分页插件 |
| Knife4j | 3.0.2 | 接口文档 |
| JWT | 0.9.1 | 认证授权 |
| Fastjson | 1.2.76 | JSON处理 |
| AliOSS | 3.10.2 | 文件上传 |
| WeChat Pay | 0.4.8 | 微信支付 |
| POI | 3.16 | Excel报表导出 |
| AspectJ | 1.9.4 | AOP切面编程 |

---

## 🎯 项目功能模块

根据代码结构，系统功能包括：

### 管理端（/admin）

#### 1. 员工管理模块
- **控制器**：`EmployeeController`
- **功能**：
  - 员工登录
  - 新增员工
  - 编辑员工信息
  - 分页查询员工
  - 启用/禁用员工账号
  - 修改密码

#### 2. 分类管理模块
- **控制器**：`CategoryController`
- **功能**：
  - 新增分类（菜品分类/套餐分类）
  - 修改分类
  - 删除分类
  - 启用/禁用分类
  - 分页查询分类
  - 根据类型查询分类

#### 3. 菜品管理模块
- **控制器**：`DishController`
- **功能**：
  - 新增菜品（含口味配置）
  - 修改菜品
  - 删除菜品（批量删除）
  - 起售/停售菜品
  - 分页查询菜品
  - 根据分类ID查询菜品

#### 4. 套餐管理模块
- **控制器**：`SetMealController`
- **功能**：
  - 新增套餐（关联菜品）
  - 修改套餐
  - 删除套餐（批量删除）
  - 起售/停售套餐
  - 分页查询套餐

#### 5. 公共模块
- **控制器**：`CommonController`
- **功能**：
  - 文件上传（阿里云OSS）

#### 6. 店铺管理
- **控制器**：`ShopController`
- **功能**：
  - 设置店铺营业状态
  - 查询店铺营业状态

### 用户端（/user）

#### 1. 用户管理模块
- **控制器**：`UserController`
- **功能**：
  - 微信登录
  - 用户信息查询

#### 2. 菜品浏览模块
- **控制器**：`DishController`
- **功能**：
  - 根据分类ID查询菜品列表

#### 3. 套餐浏览模块
- **控制器**：`SetmealController`
- **功能**：
  - 根据分类ID查询套餐列表
  - 根据套餐ID查询包含的菜品

#### 4. 分类浏览模块
- **控制器**：`CategoryController`
- **功能**：
  - 查询分类列表

#### 5. 店铺状态查询
- **控制器**：`ShopController`
- **功能**：
  - 查询店铺营业状态

---

## 🚀 项目启动流程

### 环境要求
- **JDK**：1.8 或以上
- **Maven**：3.6 或以上
- **MySQL**：5.7 或以上
- **Redis**：任意版本

### 启动步骤

1. **数据库准备**
   ```bash
   # 创建数据库
   CREATE DATABASE sky_take_out CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   
   # 导入SQL脚本（如果有）
   # 执行项目提供的SQL初始化脚本
   ```

2. **Redis 准备**
   ```bash
   # 启动 Redis 服务
   redis-server
   ```

3. **配置修改**
   - 修改 `sky-server/src/main/resources/application-dev.yml`
   - 配置数据库连接信息
   - 配置 Redis 连接信息
   - （可选）配置阿里云 OSS 信息
   - （可选）配置微信支付信息

4. **Maven 构建**
   ```bash
   # 在项目根目录执行
   mvn clean install
   ```

5. **启动项目**
   ```bash
   # 方式1：使用 Maven 启动
   cd sky-server
   mvn spring-boot:run
   
   # 方式2：使用 IDE 启动
   # 运行 com.sky.SkyApplication 的 main 方法
   ```

6. **访问接口文档**
   - Knife4j 接口文档：`http://localhost:8080/doc.html`
   - 管理端接口：`http://localhost:8080/admin/**`
   - 用户端接口：`http://localhost:8080/user/**`

7. **默认管理员账号**（如果有）
   - 用户名：`admin`
   - 密码：`123456`

---

## 📌 代码规范与约定

### 1. 命名规范
- **Controller**：以 `Controller` 结尾，如 `EmployeeController`
- **Service**：以 `Service` 结尾，如 `EmployeeService`
- **Mapper**：以 `Mapper` 结尾，如 `EmployeeMapper`
- **DTO**：以 `DTO` 结尾，如 `EmployeeDTO`
- **VO**：以 `VO` 结尾，如 `EmployeeLoginVO`

### 2. 注解使用
- `@RestController`：RESTful控制器
- `@Service`：服务层
- `@Mapper`：MyBatis Mapper
- `@Data`：Lombok自动生成getter/setter
- `@Builder`：建造者模式
- `@Slf4j`：日志注解

### 3. 异常处理
- 业务异常继承 `BaseException`
- 使用 `GlobalExceptionHandler` 统一捕获处理
- 返回标准 `Result` 对象

### 4. 返回格式
- 所有接口统一返回 `Result<T>`
- 成功：`code=1`，失败：`code=0`

---

## 🔍 常见问题说明

### Q1: DTO、Entity、VO 的区别？
- **DTO**：接收前端参数，如新增员工时的表单数据
- **Entity**：对应数据库表，包含所有字段
- **VO**：返回给前端，可能包含多表关联数据或排除敏感字段

### Q2: BaseContext 的作用？
用 ThreadLocal 存储当前登录用户ID，避免在方法间层层传递用户信息。

### Q3: 为什么使用多模块架构？
- **解耦**：公共代码、实体类、业务逻辑分离
- **复用**：common 和 pojo 可被多个服务模块引用
- **维护**：职责清晰，便于团队协作

### Q4: Knife4j 是什么？
Swagger 的增强工具，自动生成接口文档，访问 `/doc.html` 即可查看和测试接口。

### Q5: @AutoFill 注解的作用？
用于自动填充公共字段（创建时间、更新时间、创建人、更新人），避免在每个 Service 方法中手动设置这些字段。

### Q6: 如何调试接口？
1. 启动项目后访问 `http://localhost:8080/doc.html`
2. 在 Knife4j 界面中找到要测试的接口
3. 填写请求参数
4. 点击"发送"按钮即可看到响应结果

### Q7: 如何查看 SQL 日志？
在 `application.yml` 中已配置 `com.sky.mapper: debug`，启动项目后控制台会打印执行的 SQL 语句。

### Q8: Redis 的作用是什么？
用于缓存高频访问数据（如菜品信息、店铺营业状态），减少数据库查询压力，提升系统性能。

---

## 🛠️ 开发调试指南

### 本地开发环境搭建

1. **安装必要工具**
   - JDK 1.8+
   - Maven 3.6+
   - MySQL 5.7+
   - Redis
   - IntelliJ IDEA（推荐）或 Eclipse

2. **导入项目**
   ```bash
   # 克隆项目
   git clone <repository-url>
   
   # 使用 IDEA 打开项目
   # File -> Open -> 选择项目根目录的 pom.xml
   ```

3. **配置数据库**
   - 创建数据库 `sky_take_out`
   - 执行初始化 SQL 脚本
   - 修改 `application-dev.yml` 中的数据库连接信息

4. **启动 Redis**
   ```bash
   redis-server
   ```

5. **运行项目**
   - 找到 `SkyApplication` 类
   - 右键选择 "Run 'SkyApplication'"

### 调试技巧

#### 1. 使用断点调试
```
Controller 层 → 观察请求参数是否正确
    ↓
Service 层 → 观察业务逻辑执行过程
    ↓
Mapper 层 → 观察 SQL 执行结果
```

#### 2. 查看日志
```bash
# 在 application.yml 中配置日志级别
logging:
  level:
    com.sky.mapper: debug    # 查看 SQL
    com.sky.service: info    # 查看业务日志
    com.sky.controller: info # 查看请求日志
```

#### 3. 使用 Knife4j 测试接口
- 访问 `http://localhost:8080/doc.html`
- 选择接口 → 填写参数 → 发送请求 → 查看响应

#### 4. 查看数据库变化
```sql
-- 查看最新插入的数据
SELECT * FROM employee ORDER BY create_time DESC LIMIT 10;

-- 查看菜品数据
SELECT * FROM dish WHERE status = 1;
```

### 常见开发问题

#### 问题1：端口被占用
```bash
# 查找占用 8080 端口的进程
lsof -i:8080  # Mac/Linux
netstat -ano | findstr 8080  # Windows

# 杀掉进程或修改端口
# 在 application.yml 中修改 server.port
```

#### 问题2：数据库连接失败
- 检查 MySQL 是否启动
- 检查用户名密码是否正确
- 检查数据库名称是否存在
- 检查 MySQL 时区配置：`serverTimezone=Asia/Shanghai`

#### 问题3：Redis 连接失败
- 检查 Redis 是否启动：`redis-cli ping`
- 检查 Redis 配置：host、port、password

#### 问题4：JWT 验证失败
- 检查 token 是否过期
- 检查请求头中的 token 参数名是否正确（管理端：token，用户端：authentication）
- 检查 secret-key 是否正确

---

## 📖 学习建议

### 新手理解顺序：
1. 先看 **pom.xml** 了解项目依赖和模块关系
2. 再看 **实体类（Entity）** 理解数据库表结构
3. 然后看 **DTO/VO** 理解前后端数据传输
4. 接着看 **Mapper 接口和 XML** 理解数据访问
5. 然后看 **Service层** 理解业务逻辑
6. 最后看 **Controller层** 理解接口定义

### 学习路径建议：
1. **第一阶段**：理解项目结构和技术栈
   - 熟悉 Maven 多模块架构
   - 理解三层架构（Controller-Service-Mapper）
   - 学习 Spring Boot 基础配置

2. **第二阶段**：掌握核心功能
   - 学习 JWT 认证流程
   - 理解 AOP 自动填充机制
   - 掌握全局异常处理

3. **第三阶段**：实践业务开发
   - 从简单的 CRUD 开始（如员工管理）
   - 逐步学习复杂业务（如订单管理）
   - 学习 Redis 缓存应用

4. **第四阶段**：优化和扩展
   - 学习性能优化
   - 学习分页查询
   - 学习文件上传（OSS）

### 推荐学习资源：
- **Spring Boot 官方文档**：https://spring.io/projects/spring-boot
- **MyBatis 官方文档**：https://mybatis.org/mybatis-3/zh/index.html
- **Knife4j 文档**：https://doc.xiaominfo.com/

---

## 🎓 项目亮点

1. **标准的企业级项目结构**
   - Maven 多模块管理
   - 清晰的分层架构
   - 统一的代码规范

2. **完善的认证授权机制**
   - JWT 无状态认证
   - 管理端和用户端分离
   - 拦截器统一鉴权

3. **优雅的代码设计**
   - AOP 自动填充公共字段
   - 全局异常统一处理
   - ThreadLocal 管理用户上下文

4. **丰富的技术栈应用**
   - Spring Boot 快速开发
   - MyBatis 灵活的 ORM
   - Redis 缓存提升性能
   - Knife4j 自动化接口文档

5. **良好的开发体验**
   - Lombok 简化代码
   - 热部署支持
   - 接口文档可视化测试

---

## 📝 总结

这是一个标准的 **Spring Boot + MyBatis** 外卖管理系统，采用：
- ✅ **多模块架构**：分离公共、实体、业务
- ✅ **RESTful API**：统一返回格式
- ✅ **JWT认证**：无状态身份验证，管理端和用户端分离
- ✅ **AOP切面编程**：自动填充公共字段
- ✅ **全局异常处理**：统一错误处理
- ✅ **Lombok简化代码**：减少冗余代码
- ✅ **MyBatis持久化**：灵活的SQL映射
- ✅ **Redis缓存**：提升系统性能
- ✅ **接口文档自动生成**：Knife4j
- ✅ **事务管理**：声明式事务

**适合人群**：
- Java 后端开发初学者
- Spring Boot 学习者
- 准备面试的应届生
- 需要参考企业级项目结构的开发者

**学习收获**：
- 掌握 Spring Boot 项目开发流程
- 理解企业级代码规范
- 学习常用技术栈的集成使用
- 了解前后端分离项目的开发模式

---

## 📞 联系方式

如有问题或建议，欢迎提交 Issue 或 Pull Request。

---

**文档最后更新时间**：2026-01-05  
**项目版本**：1.0-SNAPSHOT  
**作者**：314159first
