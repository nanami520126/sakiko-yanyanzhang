# 缘缘庄餐饮系统 - 前端

对应后端 `sakiko-take-out`（Spring Boot，端口 8080）的管理端前端。

## 技术栈

- Vue 3 + Vite
- Element Plus
- Pinia（状态管理）
- Vue Router
- Axios

## 运行

```bash
# 安装依赖
npm install

# 启动开发服务器（默认 http://localhost:5173）
npm run dev
```

开发环境下 Vite 会把 `/admin/**` 请求代理到 `http://localhost:8080`，因此请先启动后端。

## 打包

```bash
npm run build
```

## 对接说明

- 登录成功后把后端返回的 `token` 存入 `localStorage`，之后的请求由 axios 拦截器自动加在请求头 `token` 上（后端约定，见 `application.yml` 的 `admin-token-name`）。
- 后端统一返回 `{ code, msg, data }`，`code === 1` 为成功；HTTP 401 会清空 token 并跳回登录页。

## 当前接口依赖

前端已按标准员工管理接口对接，包含：

| 功能 | 接口 |
|---|---|
| 登录 | `POST /admin/employee/login` |
| 退出 | `POST /admin/employee/logout` |
| 分页查询 | `GET /admin/employee/page` |
| 新增 | `POST /admin/employee` |
| 编辑 | `PUT /admin/employee` |
| 按 id 查询 | `GET /admin/employee/{id}` |
| 启用/禁用 | `POST /admin/employee/status/{status}?id=` |
| 批量导入 | `POST /admin/employee/import` |

注意：接口需后端对应实现后才能正常返回数据。
