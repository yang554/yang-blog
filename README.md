# yang-blog

一个面向个人/家庭使用的综合信息管理系统，包含博客知识库、财富记账、经期管理、家族族谱、营养膳食、课程费用、奖品抽取、商品价格追踪和用户权限管理。

仓库由一个 Spring Boot 后端和三套前端组成：

| 模块 | 技术栈 | 端口 | 用途 |
| --- | --- | --- | --- |
| `blog-server` | Spring Boot 2.7、Java 8、MyBatis-Plus、MySQL、Redis、RabbitMQ、Shiro/JWT | `8090` | REST API、认证授权、数据库与消息处理 |
| `blog-end` | Vue 2.7、Vue CLI 5、Element UI、Vuex、Vue Router、ECharts | `8091` | 管理后台与数据运营界面 |
| `blog-front` | Vue 3.4、Vite 5、TypeScript、Element Plus、Pinia、ECharts | `8092` | 公开展示端与轻量交互页面 |
| `blog-app` | uni-app、Vue 2、uni-ui | 由 HBuilderX 运行平台决定 | Android、iOS、微信小程序等多端个人助手 |

## 功能模块

- **知识博客**：文章发布与编辑、分类、标签、评论、点赞、收藏、博客统计和看板。
- **财富管理**：资产、账簿、流水明细、消费类型、收支统计、财富报表与退休规划数据。
- **经期管理**：经期记录、事件记录、历史统计和经期预测看板。
- **家族族谱**：家族成员、族谱关系、朔源图、成员来源和族谱看板。
- **营养膳食**：食品营养、身体成分、营养食谱、食谱收藏、营养评估和推荐膳食。
- **课程费用**：课程基础费用、费用规则和费用明细。
- **奖品抽取**：奖品设置、抽奖活动和抽奖结果管理。
- **商品价格追踪**：商品、价格记录和价格趋势查看。
- **用户与权限**：登录注册、微信登录、角色、用户管理、个人资料和权限控制。
- **应用与文件**：应用版本管理、文件上传下载和更新管理。

## 项目结构

```text
yang-blog/
├── blog-server/          # Spring Boot REST API
│   ├── src/main/java/    # 控制器、服务、实体、Mapper 和配置
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── mapper/       # MyBatis XML 映射
│   └── pom.xml
├── blog-end/             # Vue 2 管理后台
│   └── src/
│       ├── views/        # 各业务页面
│       ├── router/       # 路由与角色菜单
│       ├── store/        # Vuex 状态
│       └── api/          # HTTP 封装与接口
├── blog-front/           # Vue 3 展示端【未完成】
│   └── src/
│       ├── views/
│       ├── components/
│       ├── stores/
│       └── router/
├── blog-app/             # uni-app 多端应用
│   ├── pages/
│   ├── api/
│   └── manifest.json
├── yang-blog_*.sql       # 完整数据库备份或增量数据库脚本
└── package.json          # 根目录辅助依赖
```

## 环境要求

必需：

- JDK 8
- Maven 3
- MySQL 8（推荐）
- Node.js 16+（推荐使用支持当前依赖的 LTS 版本）
- npm 或兼容的包管理器

可选：

- Redis（缓存）
- RabbitMQ（异步互动事件）
- HBuilderX（运行和打包 uni-app）

部分功能依赖外部基础设施。即使本地不启用 Redis 或 RabbitMQ，也应先确认后端配置和对应配置开关是否满足实际运行环境。

## 数据库初始化

1. 创建数据库：

```sql
CREATE DATABASE `yang-blog` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

2. 导入仓库根目录的数据库脚本。建议优先使用最新的完整 SQL 文件，例如：

```bash
mysql -u root -p yang-blog < yang-blog_20261015_121554.sql
```

## 后端启动

进入后端目录并确认数据库、Redis 和 RabbitMQ 配置：

```bash
cd blog-server
mvn spring-boot:run
```

或打包运行：

```bash
mvn clean package
java -jar target/blog-0.0.1-SNAPSHOT.jar
```

默认访问地址：

- API：`http://127.0.0.1:8090`
- Swagger/OpenAPI：`http://127.0.0.1:8090/swagger-ui/index.html`
- Knife4j：以项目实际依赖和运行配置为准，通常可从 `/doc.html` 访问

后端配置位于 `blog-server/src/main/resources/application.yml`。运行前至少应检查并替换：

- MySQL 地址、用户名和密码
- Redis 地址、端口和密码
- RabbitMQ 地址、端口、用户名和密码
- JWT 密钥和过期时间
- 文件上传目录、静态资源和下载地址
- 应用基础地址

仓库配置中包含示例连接信息；不要在生产环境保留默认密码、示例密钥或外网地址。

## 管理后台启动（blog-end）

```bash
cd blog-end
npm install
npm run serve
```

默认地址：`http://127.0.0.1:8091`

开发代理把 `/api` 转发到 `http://127.0.0.1:8090`。

构建生产包：

```bash
npm run build
```

构建产物位于 `blog-end/dist/`。

## 展示端启动（blog-front）

```bash
cd blog-front
npm install
npm run dev
```

默认地址：`http://127.0.0.1:8092`

开发代理同样把 `/api` 转发到 `http://127.0.0.1:8090`。

构建生产包：

```bash
npm run build
```

构建产物位于 `blog-front/dist/` 或 Vite 配置指定的目录。

## uni-app 启动（blog-app）

1. 使用 HBuilderX 打开 `blog-app` 目录。
2. 确认 `blog-app/api/config.js` 中的开发或生产 `BASE_URL` 和 `IMG_URL`。
3. 在 HBuilderX 中选择运行平台：
   - 浏览器/H5
   - Android 模拟器或真机
   - iOS 模拟器或真机
   - 微信小程序
4. 在项目中执行运行或发行。

应用名称为“枫萱悦宁”，`manifest.json` 中包含应用版本、权限和各平台打包配置。

## API 与认证

后端主要接口按领域划分，例如：

- `/user`：用户、登录、注册和刷新令牌
- `/blog`：博客文章与互动
- `/tag`、`/type`：标签和分类
- `/DailyFlowController`、`/UserDeposit`：账簿与资产
- `/aunt`、`/event`：经期与事件
- `/TFamilyTreeController`、`/source`：族谱与来源
- `/nutrition`、`/foodNutrition`、`/NutritionalRecipe`：营养模块
- `/product`、`/price`：商品与价格追踪
- `/role`：角色权限
- `/file`：文件管理
- `/app`：应用版本

接口使用基于 Shiro 和 JWT 的身份认证。前端请求会携带令牌，过期后可通过刷新令牌续期；实际规则以后端过滤器和控制器实现为准。

## 配置说明

### 前端地址配置

- `blog-end`：通过 `vue.config.js` 的开发代理转发 `/api`；生产环境可通过部署反向代理或修改请求配置。
- `blog-front`：通过 `vite.config.ts` 的开发代理转发 `/api`。
- `blog-app`：通过 `blog-app/api/config.js` 区分开发环境和生产环境地址。

### 后端部署配置

`pom.xml` 提供 `local` 和 `docker` Maven Profile：

```bash
mvn spring-boot:run -Pdocker
```

部署到服务器时，建议使用环境变量或独立的生产配置覆盖数据库、Redis、RabbitMQ、文件目录、密钥和外部地址。

## 常用构建命令

```bash
# 后端
cd blog-server
mvn clean package

# Vue 2 管理后台
cd blog-end
npm run serve
npm run build

# Vue 3 展示端
cd blog-front
npm run dev
npm run build

# uni-app
# 使用 HBuilderX 的运行或发行菜单
```

## 安全与维护注意事项

- 部署前请轮换数据库密码、JWT 密钥、第三方服务密钥和 manifest 中的第三方平台凭据。
- 不要把真实密钥、密码和个人数据提交到公开仓库。
- SQL 文件可能包含真实结构或数据；导入前先检查并脱敏。
- 后端和前端均含局域网或公网地址示例，部署前应统一替换为实际域名或反向代理地址。
- `target/`、构建产物、压缩包和本地依赖不应作为源码交付内容；提交前检查 `.gitignore` 和工作区差异。

## 许可证

当前仓库未提供明确的开源许可证声明。若需要分发、复用或商用，请先补充许可证并确认项目中图片、字体、组件和依赖的许可要求。
