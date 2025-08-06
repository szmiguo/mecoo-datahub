# Mecoo Data Hub 项目

## 项目概述
这是一个基于Spring Boot的企业级后台管理系统（原名Mecoo Admin），主要用于运营管理和数据分析，具备完整的用户权限管理、定时任务、数据爬取等功能。

## 技术栈
- **开发语言**: Java 17
- **框架**: Spring Boot 2.5.15
- **权限管理**: Apache Shiro 1.13.0
- **数据库**: MySQL (使用腾讯云CynosDB)
- **ORM**: MyBatis Plus 3.5.1
- **连接池**: Druid 1.2.23
- **模板引擎**: Thymeleaf
- **构建工具**: Maven
- **任务调度**: Quartz
- **工具库**: Hutool 5.8.39, Lombok 1.18.30

## 项目结构
```
mecoo-datahub/
├── mecoo-admin-common/         # 通用工具模块
├── mecoo-admin-framework/      # 核心框架模块
├── mecoo-admin-system/         # 系统管理模块
├── mecoo-admin-spider/         # 爬虫功能模块
├── mecoo-admin-quartz/         # 定时任务模块
├── mecoo-admin-generator/      # 代码生成模块
├── mecoo-admin-web/           # Web主模块
├── sql/                       # 数据库脚本
├── doc/                       # 文档
├── pom.xml                    # 主要Maven配置
└── run.sh                     # 启动脚本
```

## 核心功能模块

### 1. mecoo-admin-common (通用模块)
- 基础工具类和常量定义
- 异常处理机制
- 通用注解(数据权限、Excel导入导出等)
- XSS防护工具

### 2. mecoo-admin-framework (框架模块)  
- Shiro安全配置
- 数据源配置(支持多数据源)
- AOP切面(日志、数据权限、数据源切换)
- 国际化配置
- 拦截器配置

### 3. mecoo-admin-system (系统模块)
- 用户管理
- 部门管理
- 角色权限
- 菜单管理
- 字典管理
- 系统配置
- 操作日志

### 4. mecoo-admin-spider (爬虫模块)
- KOL(Key Opinion Leader)内容管理
- 社交媒体数据抓取
- 追踪任务管理
- Instagram数据爬取功能

### 5. mecoo-admin-quartz (定时任务模块)
- 定时任务配置和管理
- 任务执行日志
- Cron表达式验证

### 6. mecoo-admin-generator (代码生成模块)
- 基于数据库表结构自动生成代码
- 支持Controller、Service、Mapper、实体类生成
- 模板可定制

## 环境配置

### 开发环境
- 端口: 8080
- 数据库连接: 腾讯云CynosDB MySQL
- 日志级别: DEBUG
- 热部署: 启用

### 数据库配置
- 主库: `gz-cynosdbmysql-grp-9zdg2bxx.sql.tencentcdb.com:20334/mecoo-admin`
- 用户名: `mecoo_admin`
- 连接池: Druid (最小10个连接，最大20个连接)

### 运行方式
```bash
# 开发环境
mvn spring-boot:run

# 生产环境打包部署
mvn clean package
./run.sh start
```

## 主要特性
- 基于RBAC的权限控制
- 数据权限控制(支持按部门、按个人等多种策略)
- 防XSS攻击和SQL注入
- 操作日志记录
- 支持Excel数据导入导出
- 代码生成器快速开发
- 多数据源支持
- 国际化支持
- 验证码支持(数学运算/字符验证)
- 社交媒体数据爬取功能

## 部署说明
- 使用`run.sh`脚本进行服务管理
- 支持start/stop/restart/status/clean命令
- 自动日志管理和清理
- JVM参数: -Xms2g -Xmx4g

## 开发建议
- 遵循项目现有的代码结构和命名规范
- 新增功能时使用代码生成器提高开发效率
- 注意权限控制和数据权限配置
- 重要操作需要添加日志记录