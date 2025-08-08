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

## 数据库命名规范
- 这个项目的名字是 datahub，所以数据库表的前缀是 dh_

## 近期更新记录

### 2025-08-07 功能优化与数据库设计改进

#### 1. 待抓取用户管理功能
**背景**: 原先Instagram用户名是硬编码在代码中，不便于管理和配置。

**实现内容**:
- 新建数据库表 `dh_scrape_user` 存储待抓取的用户信息
- 表结构包含：id、social_media、user_name、start_time、end_time、create_time、update_time
- 创建完整的MVC架构：
  - `ScrapeUser` 实体类 (使用MyBatis-Plus注解)
  - `ScrapeUserMapper` 接口 (使用@Select注解，避免XML转义问题)
  - `IScrapeUserService` 接口和 `ScrapeUserServiceImpl` 实现类
- 修改 `ScrapeTask.scrapeSocialMediaPost()` 方法从数据库读取有效用户列表
- 查询条件：`social_media='instagram'` 且当前时间在 start_time 和 end_time 范围内

**SQL文件**: 
- `sql/dh_scrape_user.sql` - 创建表结构
- `sql/insert_new_users.sql` - 插入20个Instagram用户数据

#### 2. 爬虫反检测优化
**背景**: 连续快速抓取多个用户容易被平台识别为机器人行为。

**实现内容**:
- 在 `scrapeSocialMediaPost()` 中添加随机延时机制
- 每个用户抓取完成后等待5-10秒随机时间
- 异常情况下也会等待，避免连续重试
- 支持任务优雅中断
- 详细的日志记录，便于监控抓取进度

#### 3. Instagram爬虫智能优化
**背景**: 部分用户Reels数量较少，无需继续无效滚动。

**实现内容**:
- 优化 `InstagramReelScraper.getUserReelsData()` 方法
- 添加智能提前退出机制：连续3次滚动无新数据时自动停止
- 适用场景：
  - 用户无公开Reels（从0开始就无数据）
  - 用户Reels数量少（中途无更多数据）
  - 已加载完所有可用内容
- 提高抓取效率，减少无效操作

#### 4. 数据库架构改进
**MyBatis-Plus注解方式**:
- 将XML映射文件改为注解方式，简化配置
- 避免XML中的转义符问题（`>=` 和 `<=`）
- 提高代码可维护性

**表设计规范**:
- 统一使用 `dh_` 表前缀
- 标准字段：create_time (自动创建时间)、update_time (自动更新时间)
- 合理的索引设计：复合索引 `idx_social_time (social_media, start_time, end_time)`

#### 5. 技术要点总结
- **数据持久化**: 完整的MVC架构，支持用户配置化管理
- **反爬虫策略**: 随机延时、智能检测、人性化行为模拟
- **性能优化**: 提前退出机制、减少无效滚动操作
- **代码质量**: 使用注解简化配置、统一异常处理、详细日志记录