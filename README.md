# 缘缘庄 · 外卖点餐系统

本仓库包含缘缘庄外卖点餐系统的全部源码与部署文件：

- `sakiko-take-out/` — Spring Boot 后端服务（Maven 多模块）
- `sakiko-take-out-frontend/` — Vue 3 管理端
- `sakiko-vx/` — 微信小程序用户端
- `sakiko-dev.sql` / `sakiko-prod.sql` — 开发 / 生产数据库脚本
- `docker-compose.yml`、`Dockerfile`、`.env.example` — 容器化部署配置

## 功能

**管理端（Vue 3 + Element Plus）**

- 员工管理：登录、分页查询、新增 / 编辑 / 删除
- 分类管理：菜品分类与套餐分类
- 菜品管理：含口味配置、图片上传、起售 / 停售
- 套餐管理：套餐与菜品关系维护
- 订单管理：订单查询、接单、拒单、取消、完成、状态统计

**用户端（微信小程序）**

- 浏览菜品与套餐、口味选择
- 购物车、下单、地址管理
- 历史订单、取消订单、再来一单
- 微信一键登录

**后端（Spring Boot）**

- JWT 鉴权，Token 存于 Redis
- MySQL 持久化，PageHelper 分页
- 阿里云 OSS 文件上传
- Redis 缓存
- Knife4j 接口文档

## 技术栈

| 层 | 技术 |
| --- | --- |
| 后端 | Spring Boot 2.7、MyBatis、MySQL、Redis、JWT、Knife4j |
| 管理端 | Vue 3、Vite、Element Plus、Pinia、Vue Router、Axios |
| 用户端 | 微信小程序（原生 WXML / WXSS / JS） |
| 部署 | Docker、Docker Compose、Nginx |

## 目录结构

```
sakiko/
├── docker-compose.yml            # 服务编排：mysql / redis / backend / frontend
├── .env.example                  # 环境变量模板（真实密钥放 .env，不入库）
├── sakiko-dev.sql                   # 开发库完整数据
├── sakiko-prod.sql                  # 生产库（仅表结构 + 管理员账号）
├── README.md
├── LICENSE
├── sakiko-take-out/              # 后端（Spring Boot，Maven 多模块）
│   ├── Dockerfile
│   ├── sakiko-common/
│   ├── sakiko-pojo/
│   └── sakiko-server/
├── sakiko-take-out-frontend/     # 管理端（Vue 3 + Element Plus）
│   ├── Dockerfile
│   └── nginx.conf
└── sakiko-vx/                    # 微信小程序端（不参与 Docker 部署）
```

## 快速开始（Docker）

前置：安装 Docker 与 Docker Compose v2。

```bash
# 1. 复制环境变量模板并填写密钥
cp .env.example .env

# 2. 构建并启动
docker compose up -d --build
```

启动后：

| 服务 | 地址 | 说明 |
| --- | --- | --- |
| 管理端前端 | http://localhost | Nginx（端口 80） |
| 后端接口 | http://localhost:8080 | Spring Boot |
| MySQL | localhost:3306 | root / 123456，库 `sakiko_take_out` |
| Redis | localhost:6379 | 5.0.14 |

默认管理员账号：`admin` / `123456`

## 本地开发

**后端**

```bash
cd sakiko-take-out
mvn clean package -DskipTests
java -jar sakiko-server/target/sakiko-server-1.0-SNAPSHOT.jar
```

开发配置在 `sakiko-server/src/main/resources/application-dev.yml`（含数据库、Redis、OSS、微信支付配置，**不入库**）。

**管理端**

```bash
cd sakiko-take-out-frontend
npm install
npm run dev
```

**小程序**

用微信开发者工具打开 `sakiko-vx/` 目录，修改 `app.js` 里的 `baseUrl` 指向后端地址。

## 配置说明

- 首次启动时 MySQL 会自动执行 `sakiko-prod.sql`，初始化表结构并写入管理员账号。
- 生产环境使用 `application-prod.yml`（`SPRING_PROFILES_ACTIVE=prod`），所有敏感配置通过环境变量注入：

| 变量 | 用途 |
| --- | --- |
| `DB_PASSWORD` | MySQL 密码 |
| `OSS_ENDPOINT` / `OSS_ACCESS_KEY_ID` / `OSS_ACCESS_KEY_SECRET` / `OSS_BUCKET_NAME` | 阿里云 OSS |
| `WECHAT_APPID` / `WECHAT_SECRET` / `WECHAT_MCHID` 等 | 微信支付 |

真实密钥写在项目根目录 `.env` 文件（已在 `.gitignore` 中，不入库、不进镜像），模板见 `.env.example`。

- 前端 Nginx 将 `/admin/**` 反向代理到后端容器。

## 常用命令

```bash
docker compose up -d --build   # 构建并启动
docker compose logs -f backend # 查看后端日志
docker compose ps              # 查看服务状态
docker compose down            # 停止并移除容器
docker compose down -v         # 连同数据卷一起删除（会清空数据库）
```

## 许可证

本项目基于 [MIT License](LICENSE) 开源。
